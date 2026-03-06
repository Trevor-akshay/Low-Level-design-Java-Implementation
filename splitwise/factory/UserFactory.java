package splitwise.factory;

import java.util.UUID;

import splitwise.models.User;

/**
 * Factory for creating User instances.
 * Generates unique UUIDs for each new user.
 * 
 * Example:
 * User alice = UserFactory.createUser("Alice");
 * // Creates a new User with a random UUID and name "Alice"
 */
public class UserFactory {

	/**
	 * Creates a new User with a randomly generated UUID.
	 * 
	 * @param name Name of the user
	 * @return A new User instance with unique UUID
	 */
	public static User createUser(String name) {
		UUID userId = UUID.randomUUID();
		return new User(userId, name);
	}
}
