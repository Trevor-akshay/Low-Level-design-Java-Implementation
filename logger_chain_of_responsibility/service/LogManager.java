package logger_chain_of_responsibility.service;

import logger_chain_of_responsibility.appenders.Appender;
import logger_chain_of_responsibility.enums.LogLevel;
import logger_chain_of_responsibility.handler.DebugHandler;
import logger_chain_of_responsibility.handler.ErrorHandler;
import logger_chain_of_responsibility.handler.InfoHandler;
import logger_chain_of_responsibility.handler.LogHandler;

public class LogManager {
	LogHandler errorLogHandler = new ErrorHandler(null);
	LogHandler debugLogHandler = new DebugHandler(errorLogHandler);
	LogHandler infoLogHandler = new InfoHandler(debugLogHandler);

	public void addAppender(LogLevel logLevel, Appender appender) {
		switch (logLevel) {
			case DEBUG -> debugLogHandler.addAppender(appender);
			case INFO -> infoLogHandler.addAppender(appender);
			case ERROR -> errorLogHandler.addAppender(appender);
		}
	}

	public LogHandler getChain() {
		return infoLogHandler;
	}
}
