package car_rental_system.payment;

import java.util.UUID;

import car_rental_system.models.Receipt;

public interface PaymentStrategy {
	public Receipt pay(UUID reservationID);
}
