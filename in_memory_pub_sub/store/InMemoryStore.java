package in_memory_pub_sub.store;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import in_memory_pub_sub.models.Message;
import in_memory_pub_sub.models.Topic;

public class InMemoryStore implements IStore {
	private final ConcurrentHashMap<UUID, List<Message>> topics;

	public InMemoryStore() {
		this.topics = new ConcurrentHashMap<>();
	}

	@Override
	public void publish(Topic topic, Message message) {
		// computeIfAbsent is atomic for map insertion
		List<Message> messages = topics.computeIfAbsent(topic.getTopicId(), k -> new ArrayList<>());

		// Only lock the specific list for this topic
		synchronized (messages) {
			messages.add(message);
		}
	}

	@Override
	public List<Message> getMessagesFromOffset(Topic topic, int offset, int limit) {
		if (limit <= 0)
			return List.of();

		var messages = topics.get(topic.getTopicId());
		if (messages == null)
			return List.of();
		synchronized (messages) {
			if (messages.isEmpty() || offset >= messages.size())
				return List.of();
			// optional cap for fairness
			limit = Math.min(16, limit);

			// Calculate safe end index
			int end = Math.min(messages.size(), offset + limit);

			// Return a COPY to avoid ConcurrentModificationException outside the lock
			return new ArrayList<>(messages.subList(offset, end));
		}
	}

	public Message getMessageFromOffset(Topic topic, int offSet) {
		var messages = topics.get(topic.getTopicId());
		if (messages == null) return null;
		synchronized (messages) {
			if (messages.isEmpty())
				return null;

			return messages.get(offSet);
		}
	}
}
