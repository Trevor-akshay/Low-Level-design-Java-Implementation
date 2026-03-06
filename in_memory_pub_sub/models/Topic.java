package in_memory_pub_sub.models;

import java.util.Objects;
import java.util.UUID;

public class Topic {
	private final UUID topicId;
	private final String topicName;

	public Topic(UUID topicId, String topicName) {
		this.topicId = topicId;
		this.topicName = topicName;
	}

	public String getTopicName() {
		return topicName;
	}

	public UUID getTopicId() {
		return topicId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Topic other))
			return false;
		return Objects.equals(this.topicId, other.topicId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topicId);
	}
}
