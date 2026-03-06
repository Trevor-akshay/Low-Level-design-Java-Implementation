# Task Management — Low Level Design

A multi-user task management system supporting task creation, updates, deletion, and priority/status tracking.

---

## Class Entities

| Class / Interface       | Role                                                                 |
| ----------------------- | -------------------------------------------------------------------- |
| `TaskService`           | Entry point — validates requests and delegates to `TaskManager`      |
| `TaskManager`           | Repository layer — owns the in-memory user and task databases        |
| `Task`                  | Core entity: taskId, userId, priority, status, tags, and description |
| `TaskRequestBody`       | DTO for task creation requests                                       |
| `TaskUpdateRequestBody` | DTO for task update requests                                         |
| `User`                  | Represents a system user who owns tasks                              |
| `TaskFactory`           | Creates `Task` instances from `TaskRequestBody`                      |
| `TaskStatus` _(enum)_   | `NEW`, `IN_PROGRESS`, `DONE`                                         |
| `TaskTags` _(enum)_     | Categorization labels (e.g., `BUG`, `FEATURE`, `URGENT`)             |

---

## Functional Requirements

1. **Create task**: A user submits a `TaskRequestBody`; a `Task` is created and stored under their userId.
2. **Update task**: A user updates an existing task by taskId — only the task owner can modify it.
3. **Delete task**: A user deletes a task by taskId — only the task owner can delete it.
4. Sanity checks are performed on all inputs before any write operation.

---

## Non-Functional Requirements

- **Thread safety**: `ConcurrentHashMap` is used for both the user DB and the per-user task map — safe for concurrent reads.
- **Correctness**: Write operations (create/update/delete) should use per-user locks in a production system to prevent race conditions.
- **Extensibility**: Add new `TaskStatus` or `TaskTags` values without changing service logic.

---

## Concurrency Requirements

- `ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Task>>` — outer map keyed by userId, inner map keyed by taskId.
- In production, update and delete operations should acquire a per-user lock (e.g., `ReentrantLock`) to prevent lost-update race conditions.

---

## Class Diagram

```
TaskService
    └── TaskManager
            ├── usersDb: ConcurrentHashMap<UUID, User>
            ├── userTasksDb: ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Task>>
            ├── createTask(userId, Task) : boolean
            ├── updateTask(userId, TaskUpdateRequestBody) : boolean
            └── deleteTask(userId, taskId) : boolean

Task
    ├── taskId: UUID
    ├── userId: UUID
    ├── priority: int
    ├── status: TaskStatus
    ├── tags: TaskTags[]
    └── description: String

TaskFactory
    └── createTask(userId, TaskRequestBody) : Task

TaskStatus (enum): NEW, IN_PROGRESS, DONE
TaskTags  (enum): BUG, FEATURE, URGENT, ...
```

---

## Design Patterns Used

| Pattern    | Where                                                                              |
| ---------- | ---------------------------------------------------------------------------------- |
| Factory    | `TaskFactory` — separates task construction from service logic                     |
| Repository | `TaskManager` — owns in-memory persistence and query operations                    |
| DTO        | `TaskRequestBody`, `TaskUpdateRequestBody` — decouple API inputs from domain model |
