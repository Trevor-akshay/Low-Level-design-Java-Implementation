package splitwise.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import splitwise.enums.SplitType;

/**
 * Represents an expense that is split among multiple users.
 * Each expense tracks who paid, the total amount, which users are involved,
 * and how the expense should be split (equal, percentage, etc).
 * 
 * Example:
 * Expense dinner = new Expense(UUID.randomUUID(), alice, 300.0,
 * "Dinner", LocalDate.now(),
 * List.of(alice, bob, charlie),
 * SplitType.EQUAL);
 * This represents a 300 rupee dinner split equally among 3 people.
 */
public class Expense {
	private final UUID expenseId;
	private User paidBy;
	private double amount;
	private String description;
	private final LocalDate createdAt;
	private List<User> users;
	private SplitType splitType;

	/**
	 * Creates a new Expense instance.
	 * 
	 * @param expenseId   Unique identifier for the expense
	 * @param paidBy      User who paid for this expense
	 * @param amount      Total amount of the expense (should be positive)
	 * @param description Brief description of the expense
	 * @param createdAt   Date when expense was created (parameter ignored, uses
	 *                    current date)
	 * @param users       List of users involved in this expense
	 * @param splitType   How the expense should be split (EQUAL, PERCENTAGE, etc)
	 */
	public Expense(UUID expenseId, User paidBy,
			double amount, String description, LocalDate createdAt, List<User> users,
			SplitType splitType) {
		this.expenseId = expenseId;
		this.paidBy = paidBy;
		this.amount = amount;
		this.description = description;
		this.createdAt = LocalDate.now();
		this.users = users;
		this.splitType = splitType;
	}

	/**
	 * Gets the unique identifier of this expense.
	 * 
	 * @return the expense's UUID
	 */
	public UUID getExpenseId() {
		return expenseId;
	}

	/**
	 * Gets the user who paid for this expense.
	 * 
	 * @return the user who paid
	 */
	public User getPaidBy() {
		return paidBy;
	}

	/**
	 * Updates the user who paid for this expense.
	 * 
	 * @param paidBy the new user who paid
	 */
	public void setPaidBy(User paidBy) {
		this.paidBy = paidBy;
	}

	/**
	 * Gets the total amount of this expense.
	 * 
	 * @return the amount as a double
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Updates the total amount of this expense.
	 * 
	 * @param amount the new amount (should be positive)
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Gets the description of this expense.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Updates the description of this expense.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the date when this expense was created.
	 * 
	 * @return the creation date
	 */
	public LocalDate getCreatedAt() {
		return createdAt;
	}

	/**
	 * Gets the list of users involved in this expense.
	 * 
	 * @return list of users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Updates the list of users involved in this expense.
	 * 
	 * @param users the new list of users
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * Gets the split type for this expense.
	 * 
	 * @return the split type (EQUAL, PERCENTAGE, etc)
	 */
	public SplitType getSplitType() {
		return splitType;
	}

	/**
	 * Updates the split type for this expense.
	 * 
	 * @param splitType the new split type
	 */
	public void setSplitType(SplitType splitType) {
		this.splitType = splitType;
	}

	/**
	 * Returns true if this expense is equal to another object.
	 * Two expenses are equal if they have the same expenseId.
	 * 
	 * @param o the object to compare with
	 * @return true if the expenses are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Expense expense = (Expense) o;
		return Objects.equals(expenseId, expense.expenseId);
	}

	/**
	 * Returns the hash code of this expense based on its expenseId.
	 * 
	 * @return hash code of the expense
	 */
	@Override
	public int hashCode() {
		return Objects.hash(expenseId);
	}

	/**
	 * Returns a string representation of this expense.
	 * 
	 * @return string representation in format "Expense{id, amount, description}"
	 */
	@Override
	public String toString() {
		return "Expense{" +
				"expenseId=" + expenseId +
				", amount=" + amount +
				", description='" + description + '\'' +
				", createdAt=" + createdAt +
				'}';
	}
}
