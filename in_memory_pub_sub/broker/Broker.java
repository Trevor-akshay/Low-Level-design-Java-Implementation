package in_memory_pub_sub.broker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import in_memory_pub_sub.consumer.Consumer;
import in_memory_pub_sub.models.Message;
import in_memory_pub_sub.models.Topic;
import in_memory_pub_sub.store.IStore;

public class Broker {
	private final Map<Topic, List<Consumer>> consumers;
	private final IStore iStore;
	private final ReentrantReadWriteLock lock;

	public Broker(Map<Topic, List<Consumer>> consumers, IStore iStore) {
		this.consumers = consumers;
		this.iStore = iStore;
		this.lock = new ReentrantReadWriteLock();
	}

	public void publish(Topic topic, Message message) {
		iStore.publish(topic, message);
		notifyMessage(topic);
	}

	private final void notifyMessage(Topic topic) {
		lock.readLock().lock();
		try {
			for (var consumer : consumers.get(topic)) {
				consumer.notifyMessage(topic);
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	public void addConsumer(Topic topic, Consumer consumer) {
		lock.writeLock().lock();
		try {
			consumers.computeIfPresent(topic, (key, value) -> {
				value.add(consumer);
				return value;
			});
		} finally {
			lock.writeLock().unlock();
		}
	}

	public List<Message> poll(Topic topic, int offSet, int limit) {
		return iStore.getMessagesFromOffset(topic, offSet, limit);
	}

	public Message poll(Topic topic, int offSet) {
		return iStore.getMessageFromOffset(topic, offSet);
	}

	public IStore getIStore() {
		lock.readLock().lock();
		try {
			return this.iStore;
		} finally {
			lock.readLock().unlock();
		}
	}

}
