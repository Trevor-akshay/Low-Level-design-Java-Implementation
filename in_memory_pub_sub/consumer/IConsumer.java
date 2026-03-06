package in_memory_pub_sub.consumer;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import in_memory_pub_sub.broker.Broker;

import in_memory_pub_sub.models.Topic;

public interface IConsumer {
	public AtomicInteger getOffSet(Topic topic);

	public UUID getConsumerId();

	public Broker getBroker();
}
