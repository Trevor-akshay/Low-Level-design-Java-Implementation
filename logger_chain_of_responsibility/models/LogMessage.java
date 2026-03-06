package logger_chain_of_responsibility.models;

import java.util.Date;

import logger_chain_of_responsibility.enums.LogLevel;

public class LogMessage {
	private final String message;

	private final LogLevel logLevel;

	private final Long timestamp;

	public LogMessage(String message, LogLevel logLevel) {
		this.message = message;
		this.logLevel = logLevel;
		this.timestamp = new Date().getTime();
	}

	public String getMessage() {
		return message;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}
}
