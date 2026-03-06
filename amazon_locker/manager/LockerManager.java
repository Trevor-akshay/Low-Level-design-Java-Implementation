package amazon_locker.manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import amazon_locker.enums.Type;
import amazon_locker.enums.Size;
import amazon_locker.exceptions.InvalidCodeException;
import amazon_locker.exceptions.NoLockerAvailableException;
import amazon_locker.models.Locker;
import amazon_locker.models.UserPackage;
import amazon_locker.utils.CodeGenerator;

public class LockerManager {
	private final ConcurrentHashMap<Size, ConcurrentHashMap<Type, BlockingQueue<Locker>>> lockers;
	private final ConcurrentHashMap<String, Locker> codeToLockerMapping;

	public LockerManager(
			ConcurrentHashMap<Size, ConcurrentHashMap<Type, BlockingQueue<Locker>>> lockers,
			ConcurrentHashMap<String, Locker> codeToLockerMapping) {
		this.lockers = lockers;
		this.codeToLockerMapping = codeToLockerMapping;
	}

	public String assignLocker(UserPackage userPackage) throws NoLockerAvailableException {
		/*
		 * Core logic:
		 * 1. Driver is gonna come with the package
		 * 2. Get the size of the package.
		 * 3. Ask the system for the best fit
		 * 4. If found assign it and generate the code
		 * 5. Open the locker, Place the package in the locker, close it.
		 * Edge case.
		 * 1. If the size is unavailable, try again later.
		 * 2. If hardware error for opening, get another one.
		 */

		var packageSize = userPackage.getSize();
		var packageType = userPackage.getType();
		var packageTemperature = userPackage.getTemperature();
		var locker = getBestFit(packageSize, packageType, packageTemperature);
		if (locker == null)
			throw new NoLockerAvailableException(
					"Sorry at the moment no locker with the specified size is available");

		var code = CodeGenerator.generateUniqueCode();
		try {
			locker.open();

			codeToLockerMapping.put(code, locker);

			return code;
		} catch (Exception e) {
			// Log and let the company know about the hardware failure.
			return this.assignLocker(userPackage);
		}
	}

	public void pickUp(String code) {
		/*
		 * 1. user comes up with the code
		 * 2. Enters the code.
		 * 3. Check the codeToLockerMapping, and get the locker
		 * 4. Update the db, add the locker again to the queue
		 * 5. Open the locker
		 * 
		 * Edge case:
		 * 1. Valid the code, if wrong throw exeception.
		 * 2. If hardware failure, Notify Amazon
		 */

		try {
			var locker = codeToLockerMapping.get(code);
			if (locker == null)
				throw new InvalidCodeException("Invalid code, please verify it");

			var lockerSize = locker.getSize();
			locker.open();
			lockers.compute(lockerSize, (key, value) -> {
				var type = locker.getLockerType();
				value.get(type).offer(locker);
				return value;
			});
			codeToLockerMapping.remove(code);
		} catch (InvalidCodeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// Notify Amazon, or customer support. So they could assign someone to force
			// open the locker, Log that the locker with the code is malfunctioning.
		}
	}

	private Locker getBestFit(Size packageSize, Type packageType, int temperature) {
		for (var size : Size.values()) {
			if (size.getSize() < packageSize.getSize())
				continue;

			for (var type : Type.values()) {
				if (!acceptanceFactor(packageType, type))
					continue;

				var queue = lockers.get(size).get(type);

				if (queue == null || queue.isEmpty())
					continue;

				return queue.poll();
			}
		}
		return null;
	}

	private boolean acceptanceFactor(Type packageType, Type lockerType) {
		if (packageType.equals(lockerType))
			return true;
		else if (packageType == Type.STANDARD && lockerType == Type.COLD)
			return true;
		return false;
	}
}