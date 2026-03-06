package snakeAndLadders.factory;

import snakeAndLadders.enums.ObstacleTypes;
import snakeAndLadders.exceptions.UnknownObstacleException;
import snakeAndLadders.obstacles.Ladder;
import snakeAndLadders.obstacles.Obstacle;
import snakeAndLadders.obstacles.Portal;
import snakeAndLadders.obstacles.Snake;

public class ObstacleFactory {
	public static Obstacle generateObstacle(ObstacleTypes obstacleType, int start, int end) throws Exception {
		switch (obstacleType) {
			case Snake:
				return new Snake(start, end);
			case Ladder:
				return new Ladder(start, end);
			case Portal:
				return new Portal(start,end);
			default:
				throw new UnknownObstacleException("Obstacle not valid");
		}
	}
}
