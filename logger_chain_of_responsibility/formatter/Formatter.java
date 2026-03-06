package logger_chain_of_responsibility.formatter;

import logger_chain_of_responsibility.models.LogMessage;

public interface Formatter {
	String format(LogMessage message);
}
