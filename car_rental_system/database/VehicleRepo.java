package car_rental_system.database;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import car_rental_system.enums.VehicleType;
import car_rental_system.factories.VehicleFactory;
import car_rental_system.models.Vehicle;

public class VehicleRepo {
	/**
	 * Concurrency notes
	 * -----------------
	 * This repository supports concurrent access from multiple threads (e.g.,
	 * multiple users trying
	 * to book / add / remove vehicles at the same time).
	 *
	 * We protect the *repository data structures* (the two maps) using a Read/Write
	 * lock:
	 * - Read lock: used for pure lookups/iteration over the maps.
	 * Multiple readers can run in parallel, improving throughput for read-heavy
	 * workloads.
	 * - Write lock: used for operations that mutate the maps or require a
	 * multi-step update
	 * to be atomic (e.g., addVehicle updates both maps).
	 * Writers are exclusive and also block readers to prevent seeing
	 * partially-applied updates.
	 *
	 * Why a ReentrantReadWriteLock here?
	 * - The constructor accepts arbitrary Map implementations. Even if callers pass
	 * thread-safe maps
	 * (e.g., ConcurrentHashMap), many operations in this class are *compound*
	 * (read-then-act and
	 * multi-structure updates) and still need external coordination to stay
	 * consistent.
	 * - It provides better concurrency than synchronizing every method, because
	 * reads don't block
	 * other reads.
	 *
	 * Note: Vehicle availability is handled via an atomic flag inside Vehicle (see
	 * getVehicleFromType).
	 * We keep that fine-grained atomic compare-and-set (CAS) to avoid a single
	 * global write-lock for
	 * every booking attempt. The lock is mainly for keeping the repository maps
	 * consistent.
	 */
	private final Map<UUID, Vehicle> vehicles;
	private final Map<VehicleType, Set<UUID>> vehiclesPerType;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public VehicleRepo(Map<UUID, Vehicle> vehicles, Map<VehicleType, Set<UUID>> vehiclesPerType) {
		this.vehicles = vehicles;
		this.vehiclesPerType = vehiclesPerType;
	}

	public Vehicle getVehicle(UUID vehicleId) {
		// Read lock allows multiple concurrent reads without blocking each other.
		lock.readLock().lock();
		try {
			var vehicle = vehicles.get(vehicleId);
			if (vehicle == null)
				throw new Error("Vehicle not found");

			return vehicle;
		} finally {
			lock.readLock().unlock();
		}
	}

	public Vehicle getVehicleFromType(VehicleType vehicleType) {
		/*
		 * Locking strategy for selection:
		 * - We take the read lock to safely read/iterate the repository maps while
		 * other threads may
		 * be adding/removing vehicles.
		 * - We do NOT take the write lock for the actual "reserve" decision. Instead we
		 * rely on an
		 * atomic compareAndSet on the per-vehicle availability flag.
		 *
		 * This is efficient because many threads can attempt reads/selection
		 * concurrently and only
		 * the winning CAS transitions availability from true -> false.
		 */
		lock.readLock().lock();
		try {
			var vehicleIds = vehiclesPerType.get(vehicleType);
			if (vehicleIds == null || vehicleIds.isEmpty())
				throw new Error("No Vehicle available at the moment");

			for (var vId : vehicleIds) {
				var vehicle = vehicles.get(vId);
				if (vehicle == null)
					continue;
				boolean reserved = vehicle.tryReserve();
				if (reserved)
					return vehicle;
			}
		} finally {
			lock.readLock().unlock();
		}

		throw new Error("No Vehicle available at the moment");

	}

	public void addVehicle(VehicleType vehicleType) {
		// Write lock ensures both maps are updated as one atomic operation.
		lock.writeLock().lock();
		try {
			var vehicle = VehicleFactory.createVehicle(vehicleType);

			UUID vehicleId = vehicle.getVehicleId();
			vehicles.put(vehicleId, vehicle);

			vehiclesPerType.compute(vehicleType, (key, value) -> {
				if (value == null)
					value = ConcurrentHashMap.newKeySet();
				value.add(vehicleId);
				return value;
			});
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void removeVehicle(UUID vehicleId) {
		// Write lock blocks concurrent selectors while we remove a vehicle from
		// circulation.
		lock.writeLock().lock();
		try {
			var vehicle = vehicles.get(vehicleId);
			if (vehicle == null)
				throw new Error("Vehicle not found");

			vehicle.markOutOfService();
			vehicles.remove(vehicleId);
			var typeSet = vehiclesPerType.get(vehicle.getVehicleType());
			if (typeSet != null) {
				typeSet.remove(vehicleId);
				if (typeSet.isEmpty())
					vehiclesPerType.remove(vehicle.getVehicleType());
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Returns a stable snapshot of vehicle IDs for a given type.
	 *
	 * We return a copy so callers can iterate without holding our lock.
	 */
	public Set<UUID> getVehicleIdsByTypeSnapshot(VehicleType vehicleType) {
		lock.readLock().lock();
		try {
			var ids = vehiclesPerType.get(vehicleType);
			if (ids == null || ids.isEmpty())
				return Set.of();
			return new HashSet<>(ids);
		} finally {
			lock.readLock().unlock();
		}
	}
}
