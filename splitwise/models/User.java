package splitwise.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the Splitwise application.
 * Users are identified by a unique UUID and have a name.
 * 
 * Example:
 * User alice = new User(UUID.randomUUID(), "Alice");
 * User bob = new User(UUID.randomUUID(), "Bob");
 */
public class User {
	private final UUID userId;
	private String name;

	/**
	 * Creates a new User instance.
	 * 
	 * @param userId Unique identifier for the user
	 * @param name   Display name of the user
	 */
	public User(UUID userId, String name) {
		this.userId = userId;
		this.name = name;
	}

	/**
	 * Gets the unique identifier of this user.
	 * 
	 * @return the user's UUID
	 */
	public UUID getUserId() {
		return userId;
	}

	/**
	 * Gets the name of this user.
	 * 
	 * @return the user's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param name the new name for this user
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns true if this user is equal to another object.
	 * Two users are equal if they have the same userId.
	 * 
	 * @param o the object to compare with
	 * @return true if the users are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return Objects.equals(userId, user.userId);
	}

	/**
	 * Returns the hash code of this user based on its userId.
	 * 
	 * @return hash code of the user
	 */
	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}

	/**
	 * Returns a string representation of this user.
	 * 
	 * @return string representation in format "User{userId, name}"
	 */
	@Override
	public String toString() {
		return "User{" +
				"userId=" + userId +
				", name='" + name + '\'' +
				'}';
	}
}
