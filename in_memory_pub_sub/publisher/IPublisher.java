package in_memory_pub_sub.publisher;

import in_memory_pub_sub.models.Message;
import in_memory_pub_sub.models.Topic;

public interface IPublisher {
	public void publish(Topic topic, Message message);
}
