package splitwise.factory;

import splitwise.enums.SplitType;
import splitwise.strategy.EqualSplitStrategy;
import splitwise.strategy.PercentageSplitStrategy;
import splitwise.strategy.SplitStrategy;

/**
 * Factory for creating SplitStrategy instances.
 * Provides a central place to instantiate different splitting algorithms.
 * 
 * This factory uses the Strategy pattern to encapsulate different expense
 * splitting algorithms and makes it easy to add new splitting methods.
 * 
 * Example:
 * SplitStrategy strategy =
 * SplitStrategyFactory.createSplitStrategy(SplitType.EQUAL);
 * // Creates an EqualSplitStrategy for equal expense splitting
 * 
 * Supported Split Types:
 * - EQUAL: Each person pays equal share
 * - PERCENTAGE: Each person pays a specified percentage
 */
public class SplitStrategyFactory {

	/**
	 * Creates a SplitStrategy based on the specified split type.
	 * 
	 * Algorithms provided:
	 * 1. EQUAL: Divides expense equally among all participants
	 * Example: ₹300 split among 3 people = ₹100 each
	 * 
	 * 2. PERCENTAGE: Each participant pays a percentage of the total
	 * Example: Alice 40%, Bob 30%, Charlie 30% of ₹300
	 * = Alice ₹120, Bob ₹90, Charlie ₹90
	 * 
	 * @param splitType The type of splitting to use
	 * @return Appropriate SplitStrategy implementation
	 * @throws Error if split type is not recognized
	 */
	public static SplitStrategy createSplitStrategy(SplitType splitType) {
		switch (splitType) {
			case EQUAL:
				return new EqualSplitStrategy();
			case PERCENTAGE:
				return new PercentageSplitStrategy();
			default:
				throw new Error("Please choose a valid split type. Supported: EQUAL, PERCENTAGE");
		}
	}
}
