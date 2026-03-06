package logger.formatter;

import logger.models.LogMessage;

public interface Formatter {
	String format(LogMessage message);
}
