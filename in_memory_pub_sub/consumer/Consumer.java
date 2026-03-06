package in_memory_pub_sub.consumer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import in_memory_pub_sub.broker.Broker;
import in_memory_pub_sub.models.Message;
import in_memory_pub_sub.models.Topic;

public class Consumer implements IConsumer {
	private final UUID consumerId;
	private final ConcurrentHashMap<Topic, AtomicInteger> offSets;
	private final ConsumerExecutor consumerExecutor;
	private Broker broker;

	public Consumer(UUID consumerId, ConcurrentHashMap<Topic, AtomicInteger> offSets, Broker broker) {
		this.consumerId = consumerId;
		this.offSets = offSets;
		this.broker = broker;
		this.consumerExecutor = new ConsumerExecutor();
	}

	public List<Message> poll(Topic topic, int maxMessages) {
		var offset = offSets.computeIfAbsent(topic, t -> new AtomicInteger(0));

		// pull a batch
		List<Message> batch = broker.poll(topic, offset.get(), maxMessages);

		// advance offset by how many we actually got
		offset.addAndGet(batch.size());
		return batch;
	}

	public void notifyMessage(Topic topic) {
		consumerExecutor.getTopicFromOffset(this, topic);
	}

	@Override
	public AtomicInteger getOffSet(Topic topic) {
		return offSets.get(topic);
	}

	@Override
	public Broker getBroker() {
		return broker;
	}

	@Override
	public UUID getConsumerId() {
		return consumerId;
	}
}
