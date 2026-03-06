package task_management.repo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import task_management.models.Task;
import task_management.models.TaskUpdateRequestBody;
import task_management.models.User;

public class TaskManager {
	// key - UserId, value - User
	ConcurrentHashMap<UUID, User> usersDb;

	// key - UserId, Value => New HashMap<>() : key - taskId , value - Task
	ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Task>> userTasksDb;

	public TaskManager(ConcurrentHashMap<UUID, User> usersDb,
			ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Task>> userTasksDb) {
		this.usersDb = usersDb;
		this.userTasksDb = userTasksDb;
	}

	public boolean createTask(UUID userId, Task task) {
		try {
			userTasksDb.compute(userId, (key, value) -> {
				if (value == null)
					value = new ConcurrentHashMap<>();
				value.put(task.getTaskId(), task);
				return value;
			});

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateTask(UUID userId, TaskUpdateRequestBody taskUpdateRequestBody) {
		try {

			var userTasks = userTasksDb.get(userId);

			var taskId = taskUpdateRequestBody.getTaskId();

			var task = userTasks.get(taskId);
			if (task == null)
				throw new Error("Task does not exist");

			if (taskUpdateRequestBody.getDescription() != null)
				task.setDescription(taskUpdateRequestBody.getDescription());
			if (taskUpdateRequestBody.getPriority() != null)
				task.setPriority(taskUpdateRequestBody.getPriority());
			if (taskUpdateRequestBody.getStatus() != null)
				task.setStatus(taskUpdateRequestBody.getStatus());
			if (taskUpdateRequestBody.getTags() != null)
				task.setTags(taskUpdateRequestBody.getTags());

			userTasks.put(taskId, task);

			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean deleteTask(UUID userId, UUID taskId) {
		try {

			var userTasks = userTasksDb.get(userId);

			var task = userTasks.get(taskId);
			if (task == null)
				throw new Error("Task does not exist");
			userTasks.remove(taskId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
