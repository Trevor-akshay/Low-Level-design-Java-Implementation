package car_rental_system.payment;

import java.util.UUID;

import car_rental_system.database.ReservationRepo;
import car_rental_system.factories.ReceiptFactoy;
import car_rental_system.models.Receipt;

public class CreditPayment implements PaymentStrategy {
	private final ReservationRepo reservationRepo;

	public CreditPayment(ReservationRepo reservationRepo) {
		this.reservationRepo = reservationRepo;
	}

	@Override
	public Receipt pay(UUID reservationID) {
		var reservation = reservationRepo.getReservation(reservationID);

		var userId = reservation.getUserId();
		var vehilceId = reservation.getVehicleId();
		var amount = reservation.getAmount();

		System.out.printf("%s paid the amount of %s with Credit and returned the %s vehicle back", userId, amount,
				vehilceId);

		return ReceiptFactoy.createReceipt(userId, vehilceId, amount);
	}

}
