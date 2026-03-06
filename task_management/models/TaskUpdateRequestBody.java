package task_management.models;

import java.util.Set;
import java.util.UUID;

import task_management.enums.TaskStatus;
import task_management.enums.TaskTags;

public class TaskUpdateRequestBody {
	private final UUID taskId;
	private Integer priority;
	private TaskStatus status;
	private Set<TaskTags> tags;
	private String description;

	public TaskUpdateRequestBody(UUID taskId, Integer priority, TaskStatus status, Set<TaskTags> tags,
			String description) {
		this.taskId = taskId;
		this.priority = priority;
		this.status = status;
		this.tags = tags;
		this.description = description;
	}

	public UUID getTaskId() {
		return taskId;
	}

	public Integer getPriority() {
		return priority;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public Set<TaskTags> getTags() {
		return tags;
	}

	public String getDescription() {
		return description;
	}
}
