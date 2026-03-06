package in_memory_pub_sub.models;

public class Message {
	private final String message;
	private final long timeStamp;

	public Message(String message, long timeStamp) {
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
}
