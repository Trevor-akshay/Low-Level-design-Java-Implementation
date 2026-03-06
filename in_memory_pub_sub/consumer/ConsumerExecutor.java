package in_memory_pub_sub.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import in_memory_pub_sub.models.Topic;

public class ConsumerExecutor {
	private final static ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
		Thread thread = new Thread(r);
		thread.setName("single-executor-thread");
		return thread;
	});

	public void getTopicFromOffset(Consumer consumer, Topic topic) {
		var offSet = consumer.getOffSet(topic);
		var broker = consumer.getBroker();

		executorService.submit(() -> {
			var message = broker.poll(topic, offSet.get());
			while (message != null) {
				System.out.println(
						"Consumer of Id: " + consumer.getConsumerId() + "  received the topic message: "
								+ message.getMessage());
				message = broker.poll(topic, offSet.incrementAndGet());
			}
		});
	}

	public void shutdown() {
		executorService.shutdown();
	}
}
