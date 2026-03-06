package logger_chain_of_responsibility.appenders;

import logger_chain_of_responsibility.formatter.Formatter;
import logger_chain_of_responsibility.models.LogMessage;

public class ConsoleAppender implements Appender {
	private Formatter formatter;

	public ConsoleAppender(Formatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void append(LogMessage logMessage) {
		var formattedMessage = formatter.format(logMessage);
		System.out.println(formattedMessage);
	}

	public void setFormater(Formatter formatter) {
		this.formatter = formatter;
	}
}
