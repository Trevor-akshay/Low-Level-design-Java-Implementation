package parkinglot.strategy.paymenttype;

import java.util.Map;

import parkinglot.enums.VehicleType;
import parkinglot.models.Ticket;

/**
 * Charges a per-hour price based on vehicle type.
 */
public class VehicleBasedFee implements FeeStrategy {
	private static final long MILLIS_PER_HOUR = 60L * 60L * 1000L;

	private final Map<VehicleType, Double> pricePerHourByVehicle = Map.of(
			VehicleType.BIKE, 5.5,
			VehicleType.BUS, 6.0,
			VehicleType.CAR, 7.5);

	@Override
	public double calculateFee(Ticket ticket, long exitTimeMillis) {
		long entryTimeMillis = ticket.getEntryTimeMillis();
		long durationMillis = Math.max(0L, exitTimeMillis - entryTimeMillis);
		long hours = (durationMillis + MILLIS_PER_HOUR - 1) / MILLIS_PER_HOUR;

		VehicleType vehicleType = ticket.getVehicleType();
		double pricePerHour = pricePerHourByVehicle.getOrDefault(vehicleType, 0.0);
		return hours * pricePerHour;
	}
}
