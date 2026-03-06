package logger_chain_of_responsibility.formatter;

import logger_chain_of_responsibility.models.LogMessage;

public class PlainTextFormatter implements Formatter {
	@Override
	public String format(LogMessage message) {
		return String.format(
				"%s - [%s]: %s",
				message.getTimestamp(),
				message.getLogLevel(),
				message.getMessage());
	}
}
