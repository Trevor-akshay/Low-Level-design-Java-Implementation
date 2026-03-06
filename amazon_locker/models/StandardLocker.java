package amazon_locker.models;

import java.util.UUID;

import amazon_locker.enums.Type;
import amazon_locker.enums.Size;

public class StandardLocker extends Locker {
	private final int minAllowedTemp;

	public StandardLocker(Size size, UUID lockerId, String code, Type lockerType, int minAllowedTemp) {
		super(size, lockerId, code, lockerType);
		this.minAllowedTemp = minAllowedTemp;
	}

	@Override
	public boolean canAccept(int temperature) {
		return minAllowedTemp <= temperature;
	}

}
