package in_memory_pub_sub.publisher;

import in_memory_pub_sub.broker.Broker;
import in_memory_pub_sub.models.Message;
import in_memory_pub_sub.models.Topic;

public class Publisher implements IPublisher {
	private Broker broker;

	public Publisher(Broker broker) {
		this.broker = broker;
	}

	@Override
	public void publish(Topic topic, Message message) {
		broker.publish(topic, message);
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
}
