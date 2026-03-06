package car_rental_system.billing;

import java.util.UUID;

import car_rental_system.models.Receipt;

public interface BillingStrategy {
	public Receipt bill(UUID reservationId, long billedAtMillis);
}
