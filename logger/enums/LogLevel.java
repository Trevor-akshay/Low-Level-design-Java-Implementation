package logger.enums;

public enum LogLevel {
	DEBUG(1),
	INFO(2),
	WARNING(3),
	ERROR(4),
	FATAL(5);

	private int priority;

	LogLevel(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}
}