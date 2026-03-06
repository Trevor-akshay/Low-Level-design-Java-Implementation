package car_rental_system.factories;

import java.util.UUID;

import car_rental_system.models.User;

public class UserFactory {
	static public User createUser(String name) {
		UUID userId = UUID.randomUUID();
		return new User(userId, name);
	}
}
