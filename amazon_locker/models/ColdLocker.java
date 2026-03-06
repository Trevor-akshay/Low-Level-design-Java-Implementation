package amazon_locker.models;

import java.util.UUID;

import amazon_locker.enums.Type;
import amazon_locker.enums.Size;

public class ColdLocker extends Locker {
	private int currentTemp;
	private final int maxAllowedTemp;

	public ColdLocker(Size size, UUID lockerId, String code, Type lockerType, int currentTemp,
			int maxAllowedTemp) {
		super(size, lockerId, code, lockerType);
		this.currentTemp = currentTemp;
		this.maxAllowedTemp = maxAllowedTemp;
	}

	@Override
	public boolean canAccept(int temperature) {
		return temperature <= maxAllowedTemp;
	}

	public int getCurrentTemp() {
		return currentTemp;
	}

	public void setCurrentTemp(int currentTemp) {
		this.currentTemp = currentTemp;
	}

	public int getMaxAllowedTemp() {
		return maxAllowedTemp;
	}

}
