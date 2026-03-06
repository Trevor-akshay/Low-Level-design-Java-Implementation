package parkinglot.service;

import java.util.Map;

import parkinglot.exceptions.PaymentFailedException;
import parkinglot.models.Ticket;
import parkinglot.strategy.PaymentStrategy;
import parkinglot.strategy.paymenttype.FeeStrategy;

public class PaymentService {
	private final PaymentStrategy paymentStrategy;
	private final FeeStrategy paymentTypeStrategy;

	public PaymentService(FeeStrategy paymentTypeStrategy, PaymentStrategy paymentStrategy) {
		this.paymentStrategy = paymentStrategy;
		this.paymentTypeStrategy = paymentTypeStrategy;
	}

	public boolean processPayment(Ticket ticket, Map<Integer, ParkingFloor> floors) {
		if (ticket == null) {
			throw new IllegalArgumentException("ticket cannot be null");
		}
		if (ticket.isPaid()) {
			throw new IllegalStateException("Ticket is already paid");
		}

		long exitTimeMillis = System.currentTimeMillis();
		var fee = paymentTypeStrategy.calculateFee(ticket, exitTimeMillis);
		var isPaymentSuccess = paymentStrategy.pay(fee);

		if (isPaymentSuccess) {
			var floorId = ticket.getFloorId();
			var slot = ticket.getSlot();
			var floor = floors.get(floorId);
			if (floor == null) {
				throw new IllegalStateException("Invalid floorId on ticket: " + floorId);
			}

			ticket.markPaid(exitTimeMillis);
			floor.freeSlot(slot);

			return true;
		}

		throw new PaymentFailedException("Payment failed; vehicle cannot exit until payment succeeds");
	}
}
