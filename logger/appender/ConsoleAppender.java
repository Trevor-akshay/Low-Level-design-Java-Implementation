package logger.appender;

import logger.enums.LogLevel;
import logger.formatter.Formatter;
import logger.models.LogMessage;

public class ConsoleAppender extends AbstractAppender {
	public ConsoleAppender(Formatter formatter, LogLevel configuredLogLevel) {
		super(formatter, configuredLogLevel);
	}

	@Override
	public void write(LogMessage logMessage) {
		var formattedMessage = formatter.format(logMessage);
		System.out.println(formattedMessage);
	}
}
