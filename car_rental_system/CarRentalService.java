package car_rental_system;

import java.util.UUID;

import car_rental_system.database.UserRepo;
import car_rental_system.database.VehicleRepo;
import car_rental_system.enums.VehicleType;
import car_rental_system.models.Receipt;
import car_rental_system.service.BillingService;
import car_rental_system.service.BookingService;
import car_rental_system.service.PaymentService;
import car_rental_system.service.ReservationService;

public class CarRentalService {
	public static volatile CarRentalService INSTANCE;
	private BookingService bookingService;
	@SuppressWarnings("unused")
	private PaymentService paymentService;
	private BillingService billingService;
	private ReservationService reservationService;

	private VehicleRepo vehicleRepo;
	private UserRepo userRepo;

	private CarRentalService(VehicleRepo vehicleRepo, UserRepo userRepo,
			BookingService bookingService, PaymentService paymentService, BillingService billingService,
			ReservationService reservationService) {
		this.vehicleRepo = vehicleRepo;
		this.userRepo = userRepo;

		this.billingService = billingService;
		this.paymentService = paymentService;
		this.bookingService = bookingService;
		this.reservationService = reservationService;
	}

	public static CarRentalService getInstance(VehicleRepo vehicleRepo, UserRepo userRepo,
			BookingService bookingService, PaymentService paymentService, BillingService billingService,
			ReservationService reservationService) {
		if (INSTANCE == null) {
			synchronized (CarRentalService.class) {
				INSTANCE = new CarRentalService(vehicleRepo, userRepo, bookingService, paymentService,
						billingService, reservationService);
			}
		}
		return INSTANCE;
	}

	public void addUser(String name) {
		userRepo.addUser(name);
	}

	public void addVehicle(VehicleType vehicleType) {
		vehicleRepo.addVehicle(vehicleType);
	}

	public void book(VehicleType vehicleType, UUID userId, String from, String to, int distance) {
		long rentTime = System.currentTimeMillis();
		bookingService.book(vehicleType, userId, rentTime, from, to, distance);
	}

	public void book(VehicleType vehicleType, UUID userId, long startTimeMillis, long endTimeMillis, String from,
			String to, int distance) {
		bookingService.book(vehicleType, userId, startTimeMillis, endTimeMillis, from, to, distance);
	}

	public void startRental(UUID reservationId) {
		reservationService.startRental(reservationId);
	}

	public void cancelReservation(UUID reservationId) {
		reservationService.cancel(reservationId);
	}

	public Receipt returnVehicle(UUID reservationId) {
		long billedAtMillis = System.currentTimeMillis();
		// Billing triggers payment; after that we complete the reservation and release
		// the vehicle.
		var receipt = billingService.bill(reservationId, billedAtMillis);
		// If the caller skipped an explicit startRental(), allow completing by starting
		// first.
		try {
			reservationService.startRental(reservationId);
		} catch (Error ignored) {
			// Already RENTED or not eligible; complete() will validate.
		}
		reservationService.complete(reservationId);
		return receipt;
	}
}