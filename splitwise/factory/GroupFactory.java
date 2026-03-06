package splitwise.factory;

import java.util.UUID;

import splitwise.models.Group;

/**
 * Factory for creating Group instances.
 * Generates unique UUIDs for each new group.
 * 
 * Example:
 * Group tripGroup = GroupFactory.createGroup("European Trip");
 * // Creates a new Group with a random UUID and name "European Trip"
 */
public class GroupFactory {

	/**
	 * Creates a new Group with a randomly generated UUID.
	 * 
	 * The group is initialized with empty lists for expenses and users.
	 * These are populated later through group repository methods.
	 * 
	 * @param groupName Name of the group
	 * @return A new Group instance with unique UUID
	 */
	public static Group createGroup(String groupName) {
		return new Group(UUID.randomUUID(), groupName);
	}
}
