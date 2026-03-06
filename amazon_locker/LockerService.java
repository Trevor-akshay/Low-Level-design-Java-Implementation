package amazon_locker;

import amazon_locker.exceptions.NoLockerAvailableException;
import amazon_locker.manager.LockerManager;
import amazon_locker.models.UserPackage;

/**
 * LockerService — the public-facing API for the Amazon Locker system.
 *
 * <p>
 * Orchestrates two primary workflows:
 * <ol>
 * <li><b>Drop-off</b>: a delivery driver brings a {@link UserPackage}; the
 * service
 * delegates to {@link LockerManager} to find a compatible locker, opens it,
 * stores the
 * package, and notifies the customer with a unique pickup code.</li>
 * <li><b>Pick-up</b>: the customer presents the pickup code; the service
 * delegates to
 * {@link LockerManager} to validate the code, open the locker, and return the
 * slot to
 * the available pool.</li>
 * </ol>
 */
public class LockerService {
	private final LockerManager lockerManager;

	public LockerService(LockerManager lockerManager) {
		this.lockerManager = lockerManager;
	}

	/**
	 * Handles a package drop-off.
	 *
	 * <p>
	 * Delegates locker assignment to {@link LockerManager#assignLocker}, then
	 * notifies
	 * the customer with the generated pickup code via {@link #notifyUser}.
	 *
	 * @param userPackage the package being dropped off (size, type, temperature
	 *                    requirements)
	 * @throws NoLockerAvailableException if no compatible locker is currently free
	 */
	public void dropOff(UserPackage userPackage) throws NoLockerAvailableException {
		var code = lockerManager.assignLocker(userPackage);
		notifyUser(code); // send code to customer via email / SMS
	}

	/**
	 * Handles a package pick-up.
	 *
	 * <p>
	 * Validates the customer's code, opens the locker, retrieves the package,
	 * and returns the locker to the available pool.
	 *
	 * @param code the unique pickup code supplied by the customer
	 */
	public void pickUp(String code) {
		lockerManager.pickUp(code);
	}

	/**
	 * Notifies the customer of their pickup code (stub — hook for SMS / email
	 * integration).
	 *
	 * @param code the generated pickup code to send to the customer
	 */
	private void notifyUser(String code) {
		// TODO: integrate with SMS / email notification service
	}
}
