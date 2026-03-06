package twitter;

import java.util.List;
import java.util.UUID;

public class Twitter {
	/*
		User:
			1. String username
			2. UUID userId
			3. List<User> followers
			4. List<User> followees
			5. List<Post> posts

		Post:
			1. User user
			2. long timeStamp
			3. List<Comment> comments
			4. List<User> likes

		FeedResponse:
			1. UUID postId
			2. long timeStamp
			3. List<Post> posts

		{List<Post>,timestamp,UUID} getFeed(UUID userId, int limit, timestamp, uuid);
	 */

}
