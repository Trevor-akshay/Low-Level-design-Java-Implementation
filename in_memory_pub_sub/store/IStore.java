package in_memory_pub_sub.store;

import java.util.List;

import in_memory_pub_sub.models.Message;
import in_memory_pub_sub.models.Topic;

public interface IStore {
	public void publish(Topic topic, Message message);

	public List<Message> getMessagesFromOffset(Topic topic, int offset, int limit);

	public Message getMessageFromOffset(Topic topic, int offSet);
}
