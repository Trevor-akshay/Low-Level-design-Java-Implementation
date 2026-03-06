package logger_chain_of_responsibility;

import java.util.List;

import logger_chain_of_responsibility.appenders.Appender;
import logger_chain_of_responsibility.enums.LogLevel;
import logger_chain_of_responsibility.models.LogMessage;
import logger_chain_of_responsibility.service.LogManager;

/**
 * Logger — entry point for the Chain of Responsibility logging framework.
 *
 * <p>
 * Unlike the basic Observer-based logger, this implementation routes each log
 * message
 * through a <b>handler chain</b>. The chain is built by {@link LogManager} and
 * consists of
 * level-specific handlers (e.g., DebugHandler → InfoHandler → ErrorHandler).
 * Each handler
 * either claims the message (its level matches) or passes it down to the next
 * handler.
 *
 * <p>
 * Coarse filtering: messages whose priority is below {@code configuredLevel}
 * are
 * discarded before entering the chain entirely.
 */
public class Logger {
	LogManager logManager;
	/**
	 * Global threshold — messages below this level never enter the handler chain.
	 */
	LogLevel configuredLevel;

	/**
	 * All appenders registered with this logger (each may also be assigned to
	 * specific handlers).
	 */
	List<Appender> appenders;

	public Logger(LogLevel configuredLevel, List<Appender> appenders) {
		logManager = new LogManager();
		this.appenders = appenders;
		this.configuredLevel = configuredLevel;
	}

	/** Registers an appender with a specific log level handler in the chain. */
	public void addAppenderToLevel(LogLevel logLevel, Appender appender) {
		this.appenders.add(appender);
	}

	/**
	 * Logs a message at the given level.
	 *
	 * <p>
	 * First checks if the level meets the global threshold, then creates a
	 * {@link LogMessage} and forwards it to the root of the handler chain via
	 * {@link LogManager#getChain()}.
	 *
	 * @param logLevel severity level of the message
	 * @param message  text to log
	 */
	public void log(LogLevel logLevel, String message) {
		if (logLevel.getPriority() >= configuredLevel.getPriority()) {
			LogMessage logMessage = new LogMessage(message, logLevel);
			// Hand the message to the chain root — it will route to the correct handler.
			logManager.getChain().handle(logMessage);

		}
	}

	/** Convenience method: logs at DEBUG level. */
	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	/** Convenience method: logs at ERROR level. */
	public void error(String message) {
		log(LogLevel.ERROR, message);
	}

	/** Convenience method: logs at INFO level. */
	public void info(String message) {
		log(LogLevel.INFO, message);
	}
}
