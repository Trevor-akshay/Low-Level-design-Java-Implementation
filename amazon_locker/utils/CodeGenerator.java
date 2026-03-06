package amazon_locker.utils;

import java.util.Random;

public class CodeGenerator {
	public static String generateUniqueCode() {
		Random random = new Random();
		Integer value = random.nextInt() * 10000;

		return "" + value.hashCode();
	}
}
