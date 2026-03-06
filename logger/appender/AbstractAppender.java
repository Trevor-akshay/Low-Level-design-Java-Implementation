package logger.appender;

import logger.enums.LogLevel;
import logger.formatter.Formatter;
import logger.models.LogMessage;

public abstract class AbstractAppender {
	protected LogLevel configuredLogLevel;
	protected Formatter formatter;

	protected AbstractAppender(Formatter formatter, LogLevel configuredLogLevel) {
		this.formatter = formatter;
		this.configuredLogLevel = configuredLogLevel;
	}

	protected boolean shouldLog(LogMessage message) {
		return configuredLogLevel.getPriority() <= message.getLogLevel().getPriority();
	}

	public void append(LogMessage logMessage) {
		if (!shouldLog(logMessage))
			return;
		write(logMessage);
	}

	abstract protected void write(LogMessage logMessage);

	public void setAppenderConfiguredLogLevel(LogLevel configuredLogLevel) {
		this.configuredLogLevel = configuredLogLevel;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

}
