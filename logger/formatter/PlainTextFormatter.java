package logger.formatter;

import logger.models.LogMessage;

public class PlainTextFormatter implements Formatter {

	@Override
	public String format(LogMessage logMessage) {
		return String.format(
				"%s-[%s]:%s",
				logMessage.getTimestamp(),
				logMessage.getLogLevel(),
				logMessage.getMessage());
	}

}
