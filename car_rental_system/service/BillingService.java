package car_rental_system.service;

import java.util.UUID;

import car_rental_system.billing.BillingStrategy;
import car_rental_system.models.Receipt;

public class BillingService {
	private BillingStrategy billingStrategy;

	public BillingService(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

	public Receipt bill(UUID reservationId, long billedAtMillis) {
		return billingStrategy.bill(reservationId, billedAtMillis);
	}

	public void setBillingStrategy(BillingStrategy billingStrategy) {
		this.billingStrategy = billingStrategy;
	}

}
