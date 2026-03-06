package logger_chain_of_responsibility.handler;

import logger_chain_of_responsibility.enums.LogLevel;
import logger_chain_of_responsibility.models.LogMessage;

public class DebugHandler extends LogHandler {

	public DebugHandler(LogHandler next) {
		super(next);
	}

	@Override
	protected boolean canHandle(LogMessage logMessage) {
		return LogLevel.DEBUG.getPriority() == logMessage.getLogLevel().getPriority();
	}

}
