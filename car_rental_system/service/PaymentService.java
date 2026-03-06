package car_rental_system.service;

import java.util.UUID;

import car_rental_system.models.Receipt;
import car_rental_system.payment.PaymentStrategy;

public class PaymentService {
	private PaymentStrategy paymentStrategy;

	public PaymentService(PaymentStrategy paymentStrategy) {
		this.paymentStrategy = paymentStrategy;
	}

	public Receipt pay(UUID reservationId) {
		return paymentStrategy.pay(reservationId);
	}

	public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
		this.paymentStrategy = paymentStrategy;
	}
}
