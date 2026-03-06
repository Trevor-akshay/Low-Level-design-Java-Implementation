package amazon_locker.models;

import java.util.UUID;

import amazon_locker.enums.Size;
import amazon_locker.enums.Type;

public class UserPackage {
	private final UUID packageId;
	private final Size size;
	private final Type type;
	private int temperature;

	public UserPackage(UUID packageId, Size size, Type type, int temperature) {
		this.packageId = packageId;
		this.size = size;
		this.type = type;
		this.temperature = temperature;
	}

	public UUID getPackageId() {
		return packageId;
	}

	public Size getSize() {
		return size;
	}

	public Type getType() {
		return type;
	}

	public int getTemperature() {
		return temperature;
	}

}
