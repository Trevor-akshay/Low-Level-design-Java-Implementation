package logger_chain_of_responsibility.appenders;

import logger_chain_of_responsibility.models.LogMessage;

public interface Appender {
	void append(LogMessage logMessage);
}
