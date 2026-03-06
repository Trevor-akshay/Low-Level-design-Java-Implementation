package logger_chain_of_responsibility.enums;

public enum LogLevel {
	INFO(1),
	DEBUG(2),
	ERROR(3);

	private int priority;

	LogLevel(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}
}
