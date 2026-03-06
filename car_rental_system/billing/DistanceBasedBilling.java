package car_rental_system.billing;

import java.util.UUID;

import car_rental_system.database.ReservationRepo;
import car_rental_system.models.Receipt;
import car_rental_system.service.PaymentService;

public class DistanceBasedBilling implements BillingStrategy {
	private static final double PRICE_PER_MILE = 7.2;
	PaymentService paymentService;
	ReservationRepo reservationRepo;

	public DistanceBasedBilling(PaymentService paymentService, ReservationRepo reservationRepo) {
		this.paymentService = paymentService;
		this.reservationRepo = reservationRepo;
	}

	@Override
	public Receipt bill(UUID reservationId, long billedAtMillis) {
		var reservation = reservationRepo.getReservation(reservationId);

		var distance = reservation.getDistance();
		reservation.setAmount(calculateAmount(distance));

		return paymentService.pay(reservationId);
	}

	private double calculateAmount(int distance) {
		return distance * PRICE_PER_MILE;
	}
}
