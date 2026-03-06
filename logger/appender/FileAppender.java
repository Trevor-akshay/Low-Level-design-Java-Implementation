package logger.appender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import logger.enums.LogLevel;
import logger.formatter.Formatter;
import logger.models.LogMessage;

public class FileAppender extends AbstractAppender {
	private BufferedWriter bufferedWriter;
	private BlockingQueue<LogMessage> blockingQueue;
	private boolean shouldBlockWhenFull;

	public FileAppender(Formatter formatter, LogLevel configuredLogLevel, boolean shouldBlockWhenFull) {
		super(formatter, configuredLogLevel);
		this.shouldBlockWhenFull = shouldBlockWhenFull;
		try {
			this.bufferedWriter = new BufferedWriter(new FileWriter("log.txt", true));
			this.blockingQueue = new ArrayBlockingQueue<>(5000);

			Thread thread = new Thread(this::processQueue);
			thread.setDaemon(true);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(LogMessage logMessage) {
		if (shouldBlockWhenFull) {
			try {
				blockingQueue.put(logMessage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			blockingQueue.offer(logMessage);
		}
	}

	private void processQueue() {
		while (true) {
			try {
				var logMessage = blockingQueue.take();
				var formattedMessage = formatter.format(logMessage);
				bufferedWriter.write(formattedMessage);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setShouldBlockWhenFull(boolean shouldBlockWhenFull) {
		this.shouldBlockWhenFull = shouldBlockWhenFull;
	}
}
