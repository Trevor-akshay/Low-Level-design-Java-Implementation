package splitwise.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a group of users sharing expenses.
 * A group maintains a list of expenses and the users involved.
 * 
 * Example:
 * Group tripGroup = new Group(UUID.randomUUID(), "European Trip");
 * tripGroup.addUsers(List.of(alice, bob, charlie));
 */
public class Group {
	private final UUID groupId;
	private final String groupName;
	private final List<Expense> expenses;
	private final List<User> users;

	/**
	 * Creates a new Group instance.
	 * 
	 * @param groupId   Unique identifier for the group
	 * @param groupName Name/description of the group
	 */
	public Group(UUID groupId, String groupName) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.expenses = new ArrayList<>();
		this.users = new ArrayList<>();
	}

	/**
	 * Gets the unique identifier of this group.
	 * 
	 * @return the group's UUID
	 */
	public UUID getGroupId() {
		return groupId;
	}

	/**
	 * Gets the name of this group.
	 * 
	 * @return the group's name
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Adds an expense to this group.
	 * 
	 * @param expense the expense to add
	 */
	public void addExpense(Expense expense) {
		this.expenses.add(expense);
	}

	/**
	 * Adds multiple users to this group.
	 * 
	 * @param user list of users to add
	 */
	public void addUsers(List<User> user) {
		this.users.addAll(user);
	}

	/**
	 * Removes a user from this group.
	 * 
	 * @param user the user to remove
	 * @return true if the user was removed, false if not found
	 */
	public boolean removeUser(User user) {
		return this.users.remove(user);
	}

	/**
	 * Gets all expenses in this group.
	 * 
	 * @return list of expenses
	 */
	public List<Expense> getExpenses() {
		return expenses;
	}

	/**
	 * Gets all users in this group.
	 * 
	 * @return list of users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Returns true if this group is equal to another object.
	 * Two groups are equal if they have the same groupId.
	 * 
	 * @param o the object to compare with
	 * @return true if the groups are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Group group = (Group) o;
		return Objects.equals(groupId, group.groupId);
	}

	/**
	 * Returns the hash code of this group based on its groupId.
	 * 
	 * @return hash code of the group
	 */
	@Override
	public int hashCode() {
		return Objects.hash(groupId);
	}

	/**
	 * Returns a string representation of this group.
	 * 
	 * @return string representation in format "Group{id, name, memberCount}"
	 */
	@Override
	public String toString() {
		return "Group{" +
				"groupId=" + groupId +
				", groupName='" + groupName + '\'' +
				", members=" + users.size() +
				'}';
	}
}
