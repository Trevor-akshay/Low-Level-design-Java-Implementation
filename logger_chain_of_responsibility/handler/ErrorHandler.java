package logger_chain_of_responsibility.handler;

import logger_chain_of_responsibility.enums.LogLevel;
import logger_chain_of_responsibility.models.LogMessage;

public class ErrorHandler extends LogHandler {

	public ErrorHandler(LogHandler next) {
		super(next);
	}

	@Override
	protected boolean canHandle(LogMessage logMessage) {
		return LogLevel.ERROR.getPriority() == logMessage.getLogLevel().getPriority();
	}
}
