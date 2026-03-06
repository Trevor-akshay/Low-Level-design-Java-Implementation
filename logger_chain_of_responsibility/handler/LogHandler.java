package logger_chain_of_responsibility.handler;

import java.util.ArrayList;
import java.util.List;

import logger_chain_of_responsibility.appenders.Appender;
import logger_chain_of_responsibility.models.LogMessage;

/**
 * LogHandler — abstract Chain of Responsibility node for log message routing.
 *
 * <p>
 * Each handler in the chain is responsible for exactly one log level (or
 * range).
 * When {@link #handle(LogMessage)} is called:
 * <ul>
 * <li>If {@link #canHandle(LogMessage)} returns {@code true}, the message is
 * broadcast
 * to all registered {@link Appender} instances via
 * {@link #notifyAppenders}.</li>
 * <li>Otherwise, the message is forwarded to the {@link #next} handler in the
 * chain.</li>
 * </ul>
 *
 * <p>
 * Handlers are linked at construction time — the chain is built once by
 * {@link logger_chain_of_responsibility.service.LogManager} and is read-only at
 * runtime.
 *
 * <p>
 * <b>Adding a new level</b>: subclass {@link LogHandler}, implement
 * {@link #canHandle},
 * and insert into the chain in {@code LogManager}.
 */
public abstract class LogHandler {
	/**
	 * The next handler in the chain; {@code null} if this is the terminal handler.
	 */
	LogHandler next;
	/** Appenders this handler broadcasts to when it claims a message. */
	List<Appender> appenders;

	LogHandler(LogHandler next) {
		this.next = next;
		appenders = new ArrayList<>();
	}

	/**
	 * Registers an additional appender — future messages handled by this node will
	 * also go to it.
	 */
	public void addAppender(Appender appender) {
		appenders.add(appender);
	}

	/**
	 * Routes the message: handle it locally (if {@link #canHandle} is true) or
	 * pass it to {@link #next}.
	 *
	 * @param logMessage the message to route
	 */
	public void handle(LogMessage logMessage) {
		if (canHandle(logMessage)) {
			// This handler is responsible — broadcast to all registered appenders.
			notifyAppenders(logMessage);
		} else
			// Not our responsibility — delegate down the chain.
			next.handle(logMessage);
	}

	/** Broadcasts the message to every appender registered with this handler. */
	public void notifyAppenders(LogMessage logMessage) {
		for (var appender : appenders)
			appender.append(logMessage);
	}

	/**
	 * Sets the successor handler in the chain (useful for dynamic chain
	 * reconfiguration).
	 */
	public void setNext(LogHandler next) {
		this.next = next;
	}

	/**
	 * Determines whether this handler is responsible for the given message.
	 * Typically compares the message's log level to this handler's configured
	 * level.
	 *
	 * @param logMessage the incoming log message
	 * @return {@code true} if this handler should process the message;
	 *         {@code false} to delegate
	 */
	protected abstract boolean canHandle(LogMessage logMessage);
}
