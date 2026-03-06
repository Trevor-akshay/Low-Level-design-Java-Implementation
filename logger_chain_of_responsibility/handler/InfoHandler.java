package logger_chain_of_responsibility.handler;

import logger_chain_of_responsibility.enums.LogLevel;
import logger_chain_of_responsibility.models.LogMessage;

public class InfoHandler extends LogHandler {

	public InfoHandler(LogHandler next) {
		super(next);
	}

	@Override
	protected boolean canHandle(LogMessage logMessage) {
		return LogLevel.INFO.getPriority() == logMessage.getLogLevel().getPriority();
	}
}
