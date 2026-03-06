package logger_chain_of_responsibility.appenders;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import logger_chain_of_responsibility.formatter.Formatter;
import logger_chain_of_responsibility.models.LogMessage;

public class FileAppender implements Appender {
	private final Formatter formatter;
	private final BufferedWriter writer;
	// 1. The buffer
	private final BlockingDeque<LogMessage> queue;

	public FileAppender(Formatter formatter, String fileName) {
		this.formatter = formatter;

		try {
			this.writer = new BufferedWriter(new FileWriter(fileName, true));
			this.queue = new LinkedBlockingDeque<>();// Unbounded queue for simplicity
			// 2. Start the worker thread
			Thread thread = new Thread(this::processQueue);
			thread.setDaemon(true);// Ensure it doesn't block JVM shutdown
			thread.start();
		} catch (IOException e) {
			throw new RuntimeException("Failed to open log file", e);
		}
	}

	// t1, t2, t3,
	@Override
	public void append(LogMessage message) {
		// // t4, t5

		// // blocking queue -> worker threads
		// try {
		// writer.write(formatter.format(message));
		// writer.newLine();
		// writer.flush(); // flush can be batched or delayed
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// Producer: Just drop the message in the queue (fast!)
		queue.offer(message);
	}
	// t1 t2 t3

	// blocking queue of cap = 3
	// 3 worker threads

	public synchronized void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processQueue() {
		try {
			while (true) {
				// take(): Checks the queue. If it's empty, it blocks (waits) until an element
				// becomes available. This is like waiting for a doorbell ring; you can relax
				// (and the CPU can sleep) until a package actually arrives.
				//Retrieve message from queue efficiently
				var data = queue.take();

				//Write to file
				writer.append(formatter.format(data));
				writer.newLine();
				writer.flush();
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

}
