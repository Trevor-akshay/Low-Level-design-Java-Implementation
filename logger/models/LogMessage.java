package logger.models;

import logger.enums.LogLevel;

public class LogMessage {
	private final LogLevel logLevel;
	private final long timestamp;
	private final String message;

	public LogMessage(String message, long timestamp, LogLevel logLevel) {
		this.message = message;
		this.timestamp = timestamp;
		this.logLevel = logLevel;
	}

	public String getMessage() {
		return message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

}
