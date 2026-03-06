package amazon_locker.models;

import java.util.UUID;

import amazon_locker.enums.Type;
import amazon_locker.enums.Size;

public abstract class Locker {
	private final Size size;
	private final UUID lockerId;
	private final Type lockerType;

	private final String code;

	public Locker(Size size, UUID lockerId, String code, Type lockerType) {
		this.size = size;
		this.lockerId = lockerId;
		this.code = code;
		this.lockerType = lockerType;
	}

	abstract public boolean canAccept(int temperature);

	public void open() {

	}

	public Size getSize() {
		return size;
	}

	public UUID getLockerId() {
		return lockerId;
	}

	public String getCode() {
		return code;
	}

	public Type getLockerType() {
		return lockerType;
	}
}
