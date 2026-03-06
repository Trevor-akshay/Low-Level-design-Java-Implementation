package amazon_locker.enums;

public enum Size {
	SMALL(1),
	MEDIUM(2),
	LARGE(3),
	EXTRA_LARGE(4);

	private int size;

	private Size(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}
}
