package splitwise.enums;

/**
 * Enum representing different ways to split expenses.
 * 
 * EQUAL: All participants pay the same amount
 * Example: ₹300 split among 3 people = ₹100 each
 * 
 * PERCENTAGE: Each participant pays a specified percentage
 * Example: Alice 40%, Bob 30%, Charlie 30%
 * For ₹300: Alice ₹120, Bob ₹90, Charlie ₹90
 * 
 * Future additions could include:
 * - ITEMIZED: Each person pays for specific items they consumed
 * - CUSTOM: User specifies exact amounts for each person
 */
public enum SplitType {
	/** Each person pays equal share */
	EQUAL,

	/** Each person pays a percentage of the total */
	PERCENTAGE
}
