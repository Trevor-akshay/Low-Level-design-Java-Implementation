package car_rental_system.factories;

import java.util.UUID;

import car_rental_system.models.Receipt;

public class ReceiptFactoy {
	public static Receipt createReceipt(UUID userId, UUID vehicleId, double amount) {
		UUID receiptId = UUID.randomUUID();
		return new Receipt(receiptId, userId, vehicleId, amount);
	}
}
