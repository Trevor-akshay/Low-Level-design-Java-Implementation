package task_management.factory;

import java.util.UUID;

import task_management.models.Task;
import task_management.models.TaskRequestBody;

public class TaskFactory {
	public static Task createTask(UUID userId, TaskRequestBody taskRequestBody) {
		UUID taskId = UUID.randomUUID();

		var priority = taskRequestBody.getPriority();
		var status = taskRequestBody.getStatus();
		var taskTags = taskRequestBody.getTags();
		var description = taskRequestBody.getDescription();

		return new Task(taskId, userId, priority, status, taskTags, description);
	}
}
