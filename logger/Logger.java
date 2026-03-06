package logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logger.appender.AbstractAppender;
import logger.appender.ConsoleAppender;
import logger.appender.FileAppender;
import logger.enums.LogLevel;
import logger.formatter.PlainTextFormatter;
import logger.models.LogMessage;

/**
 * Logger — central entry point for application logging.
 *
 * <p>
 * <b>Dual-level filtering</b>: Messages are filtered at two independent points:
 * <ol>
 * <li><b>Logger level</b> ({@code configuredLogLevel}): coarse global filter —
 * messages
 * below this level are discarded immediately without reaching any
 * appender.</li>
 * <li><b>Appender level</b>: each {@link AbstractAppender} has its own
 * threshold so
 * different sinks can capture different severity bands (e.g., file captures
 * INFO+,
 * console captures ERROR+ only).</li>
 * </ol>
 *
 * <p>
 * Default configuration (set in the constructor):
 * <ul>
 * <li>{@link ConsoleAppender} at INFO level with plain-text format</li>
 * <li>{@link FileAppender} at INFO level with plain-text format (non-append
 * mode)</li>
 * </ul>
 * Both can be replaced or supplemented via {@link #addAppender}.
 */
public class Logger {
	private final List<AbstractAppender> appenders;
	/** Messages below this level are discarded before reaching any appender. */
	private LogLevel configuredLogLevel;

	public Logger(LogLevel configuredLogLevel) {
		appenders = new ArrayList<>();
		appenders.add(new ConsoleAppender(new PlainTextFormatter(), LogLevel.INFO));
		appenders.add(new FileAppender(new PlainTextFormatter(), LogLevel.INFO, false));

		this.configuredLogLevel = configuredLogLevel;
	}

	/**
	 * Core log method — coarse filters by the global log level, then fans out to
	 * all appenders.
	 *
	 * @param logLevel severity of the message
	 * @param message  text to log
	 */
	private void log(LogLevel logLevel, String message) {
		// Global filter: discard anything below the configured threshold.
		if (configuredLogLevel.getPriority() > logLevel.getPriority())
			return;

		LogMessage logMessage = new LogMessage(message, new Date().getTime(), logLevel);
		notifyAllAppenders(logMessage);
	}

	/**
	 * Broadcasts the log message to every registered appender (each may further
	 * filter by level).
	 */
	private final void notifyAllAppenders(LogMessage logMessage) {
		for (var appender : appenders) {
			appender.append(logMessage);
		}
	}

	/** Logs at DEBUG level — most verbose; typically disabled in production. */
	public void DEBUG(String message) {
		log(LogLevel.DEBUG, message);
	}

	/** Logs at INFO level — general operational events. */
	public void INFO(String message) {
		log(LogLevel.INFO, message);
	}

	/** Logs at ERROR level — recoverable failures that need attention. */
	public void ERROR(String message) {
		log(LogLevel.ERROR, message);
	}

	/**
	 * Logs at FATAL level — unrecoverable failures; application may need to
	 * terminate.
	 */
	public void FATAL(String message) {
		log(LogLevel.FATAL, message);
	}

	/**
	 * Logs at WARNING level — unexpected conditions that do not yet cause failures.
	 */
	public void WARNING(String message) {
		log(LogLevel.WARNING, message);
	}

	/**
	 * Changes the global log level filter. Takes effect immediately for all
	 * subsequent log calls.
	 */
	public void setConfiguredLogLevel(LogLevel configuredLogLevel) {
		this.configuredLogLevel = configuredLogLevel;
	}

	/**
	 * Adds a new appender to the notification list. Future log messages will also
	 * be sent to it.
	 */
	public void addAppender(AbstractAppender appender) {
		appenders.add(appender);
	}
}
