package car_rental_system.billing;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import car_rental_system.database.ReservationRepo;
import car_rental_system.models.Receipt;
import car_rental_system.service.PaymentService;

public class TimeBasedBilling implements BillingStrategy {
	private static final double PRICE_PER_MINUTE = 6.5;
	PaymentService paymentService;
	ReservationRepo reservationRepo;

	public TimeBasedBilling(PaymentService paymentService, ReservationRepo reservationRepo) {
		this.paymentService = paymentService;
		this.reservationRepo = reservationRepo;
	}

	@Override
	public Receipt bill(UUID reservationId, long billedAtMillis) {
		var reservation = reservationRepo.getReservation(reservationId);
		// Todo

		long durationMillis = billedAtMillis - reservation.getRentedTime();
		long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);

		reservation.setAmount(calculateAmount(durationMinutes));

		return this.paymentService.pay(reservationId);
	}

	private double calculateAmount(long durationMinutes) {
		return durationMinutes * PRICE_PER_MINUTE;
	}

}
