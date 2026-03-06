package splitwise.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import splitwise.exceptions.InvalidUserException;
import splitwise.factory.GroupFactory;
import splitwise.models.Expense;
import splitwise.models.Group;
import splitwise.models.User;

/**
 * Repository for managing groups and their associated expenses.
 * 
 * A group represents a collection of users and their shared expenses.
 * 
 * Example:
 * Group tripGroup = new Group(UUID.randomUUID(), "European Trip");
 * // Users and expenses are added to this group
 */
public class GroupRepo {
	private final Map<UUID, Group> groups;

	/**
	 * Initializes the GroupRepo with a group storage map.
	 * 
	 * @param groups Map to store groups, typically a ConcurrentHashMap for
	 *               thread-safety
	 */
	public GroupRepo(Map<UUID, Group> groups) {
		this.groups = groups;
	}

	/**
	 * Creates a new group and adds an initial expense and users to it.
	 * This is useful when creating a group along with its first expense.
	 * 
	 * Example:
	 * Expense hotelExpense = ...
	 * groupRepo.saveGroup("Trip to Paris", hotelExpense, List.of(alice, bob,
	 * charlie));
	 * // Group "Trip to Paris" created with the hotel expense
	 * 
	 * @param groupName Name of the group
	 * @param expense   Initial expense to add to the group
	 * @param users     List of users to add to the group
	 */
	public void saveGroup(String groupName, Expense expense, List<User> users) {
		var group = GroupFactory.createGroup(groupName);

		group.addExpense(expense);
		group.addUsers(users);

		groups.put(group.getGroupId(), group);
	}

	/**
	 * Saves/registers an existing group object.
	 * Used when creating a group without initial expenses or users.
	 * 
	 * Example:
	 * UUID groupId = service.createGroup("Apartment Rent");
	 * // Uses this method internally to register the group
	 * 
	 * @param group The group to save
	 * @throws InvalidUserException if group is null
	 */
	public void saveGroup(Group group) {
		if (group == null) {
			throw new InvalidUserException("Group cannot be null");
		}
		groups.put(group.getGroupId(), group);
	}

	/**
	 * Retrieves a group by its UUID.
	 * 
	 * Example:
	 * Group tripGroup = groupRepo.getGroup(tripGroupId);
	 * // Returns the group or null if not found
	 * 
	 * @param groupId UUID of the group to retrieve
	 * @return Group object, or null if not found
	 */
	public Group getGroup(UUID groupId) {
		return groups.get(groupId);
	}

	/**
	 * Deletes a group by its UUID.
	 * Also removes all expenses and user associations for that group.
	 * 
	 * Example:
	 * boolean deleted = groupRepo.deleteGroup(groupId);
	 * // Group and all its expenses are removed
	 * 
	 * @param groupId UUID of the group to delete
	 * @return true if group was deleted, false if group not found
	 */
	public boolean deleteGroup(UUID groupId) {
		return groups.remove(groupId) != null;
	}

	/**
	 * Removes a user from a group.
	 * The user won't be included in future expense splitting for this group,
	 * but existing expenses they were part of remain recorded.
	 * 
	 * Example:
	 * boolean removed = groupRepo.removeUserFromGroup(groupId, alice);
	 * // Alice is removed from the group
	 * 
	 * @param groupId UUID of the group
	 * @param user    User to remove from the group
	 * @return true if user was removed, false if user not in group or group not
	 *         found
	 */
	public boolean removeUserFromGroup(UUID groupId, User user) {
		Group group = groups.get(groupId);
		if (group == null) {
			return false;
		}
		return group.removeUser(user);
	}

	/**
	 * Retrieves all groups currently registered.
	 * 
	 * @return Map of UUID to Group
	 */
	public Map<UUID, Group> getExpenses() {
		return new HashMap<>(groups);
	}

}
