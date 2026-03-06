package task_management.models;

import java.util.UUID;

public class User {
	private final UUID id;
	private String name;

	public User(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
