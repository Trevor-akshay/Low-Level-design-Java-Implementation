package task_management;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import task_management.factory.TaskFactory;
import task_management.models.Task;
import task_management.models.TaskRequestBody;
import task_management.models.TaskUpdateRequestBody;
import task_management.models.User;
import task_management.repo.TaskManager;

/*
	1. Entitites.
		User
		Task
		TaskOperation enum (create,update)
		TaskStatus enum(new,in_progress,done)
		TaskSerive (entry point)
		TaskManager/TaskRepository (contains the task db and user db)


	2. Classes
		User
			- integer id
			- string name

		Task
			- integer taskId
			- integer userId
			- int priority
			- Status status
			- String[] tags
			- String description

		TaskService
			+ createTask (int userId, Task task)
				1. Delegate to the TaskManager to do the implementation.
			+ updateTask (int userId, Task task)
				1. Delegate to the TaskManager to do the implementation.
			+ deleteTask (int userId, Task task)
				1. Delegate to the TaskManager to do the implementation.
			
		TaskManager
			+createTask(int userId,Task task)
				#Core logic
					1. Do a sanity check on the input given by the user.
					2. Append the task to the Task db under the userId

			+updateTask(int userIdd,Task task)
				#Core logic
					1. Do a sanity check on the input given by the user.
					2. Find the respective task in the Task DB under a UserId
					3. If found, update the values with the given details.
					4. If not, tell them to create a new task.
				# edge Logic.
					1. Check if current UserId is same as the Userid in the Task, if so continue else throw error.

			+deleteTask (int userId,Task task)
				#Core logic
					1. Check if task exist under the userid, if so delete it.
					2. If not, throw an error.
			*/
/**
 * TaskService — thin facade over {@link TaskManager}.
 *
 * <p>
 * Entry point for all task operations: create, update, and delete.
 *
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Translates incoming DTOs ({@link TaskRequestBody},
 * {@link TaskUpdateRequestBody}) into
 * domain objects via {@link TaskFactory}, then delegates to
 * {@link TaskManager}.</li>
 * <li>Owns the in-memory user and task databases passed at construction; both
 * are
 * {@link ConcurrentHashMap} for thread-safe concurrent reads.</li>
 * </ul>
 *
 * <p>
 * All business validation (ownership checks, existence checks) is performed
 * inside
 * {@link TaskManager}.
 */
public class TaskService {
	TaskManager taskManager;

	/**
	 * Constructs the service and wires up the underlying {@link TaskManager}.
	 *
	 * @param usersDb     in-memory user store (userId → User)
	 * @param userTasksDb in-memory task store (userId → (taskId → Task))
	 */
	public TaskService(ConcurrentHashMap<UUID, User> usersDb,
			ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Task>> userTasksDb) {
		taskManager = new TaskManager(usersDb, userTasksDb);
	}

	/**
	 * Creates a new task for the specified user.
	 *
	 * <p>
	 * Uses {@link TaskFactory} to build a {@link Task} from the request DTO,
	 * then delegates to {@link TaskManager#createTask}.
	 *
	 * @param userId          the ID of the user creating the task
	 * @param taskRequestBody DTO carrying task attributes (description, priority,
	 *                        tags, status)
	 * @return {@code true} if the task was created successfully; {@code false}
	 *         otherwise
	 */
	public boolean createTask(UUID userId, TaskRequestBody taskRequestBody) {
		Task task = TaskFactory.createTask(userId, taskRequestBody);
		return this.taskManager.createTask(userId, task);
	}

	/**
	 * Updates an existing task owned by the specified user.
	 *
	 * <p>
	 * Only the task owner can modify their own tasks — {@link TaskManager} enforces
	 * this.
	 *
	 * @param userId the ID of the user making the update
	 * @param task   DTO with the taskId and updated fields
	 * @return {@code true} if the update succeeded; {@code false} if the task was
	 *         not found
	 *         or the user is not the owner
	 */
	public boolean updateTask(UUID userId, TaskUpdateRequestBody task) {
		return this.taskManager.updateTask(userId, task);
	}

	/**
	 * Deletes a task owned by the specified user.
	 *
	 * @param userId the ID of the user requesting deletion
	 * @param taskId the ID of the task to delete
	 * @return {@code true} if the task was deleted; {@code false} if not found or
	 *         ownership check fails
	 */
	public boolean deleteTask(UUID userId, UUID taskId) {
		return this.taskManager.deleteTask(userId, taskId);
	}
}
