package car_rental_system.database;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import car_rental_system.factories.UserFactory;
import car_rental_system.models.User;

public class UserRepo {
	private final Map<UUID, User> userRepo;

	public UserRepo() {
		userRepo = new ConcurrentHashMap<>();
	}

	public User getUser(UUID userId) {
		return userRepo.get(userId);
	}

	public void addUser(String name) {
		var user = UserFactory.createUser(name);

		UUID userId = user.getUserId();
		userRepo.put(userId, user);
	}
}
