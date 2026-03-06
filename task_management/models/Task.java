package task_management.models;

import java.util.Set;
import java.util.UUID;

import task_management.enums.TaskStatus;
import task_management.enums.TaskTags;

public class Task {
	private final UUID taskId;
	private final UUID userId;
	private volatile Integer priority;
	private volatile TaskStatus status;
	private volatile Set<TaskTags> tags;
	private volatile String description;

	public void setDescription(String description) {
		this.description = description;
	}

	public Task(UUID taskId, UUID userId, Integer priority, TaskStatus status, Set<TaskTags> tags, String description) {
		this.taskId = taskId;
		this.userId = userId;
		this.priority = priority;
		this.status = status;
		this.tags = tags;
		this.description = description;
	}

	public UUID getTaskId() {
		return taskId;
	}

	public UUID getUserId() {
		return userId;
	}

	public Integer getPriority() {
		return priority;
	}

	public synchronized void setPriority(int priority) {
		this.priority = priority;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public synchronized void setStatus(TaskStatus status) {
		this.status = status;
	}

	public synchronized Set<TaskTags> getTags() {
		return Set.copyOf(tags);
	}

	public synchronized void addTag(TaskTags taskTag) {
		this.tags.add(taskTag);
	}

	public synchronized void setTags(Set<TaskTags> taskTags) {
		this.tags = taskTags;
	}

	public synchronized void removeTag(TaskTags taskTags) {
		this.tags.remove(taskTags);
	}

	public String getDescription() {
		return description;
	}
}
