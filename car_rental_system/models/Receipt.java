package car_rental_system.models;

import java.util.UUID;

public class Receipt {
	private final UUID receiptId;
	private final UUID userId;
	private final UUID vehicleId;
	private final double amountPaid;

	public Receipt(UUID receiptId, UUID userId, UUID vehicleId, double amountPaid) {
		this.receiptId = receiptId;
		this.userId = userId;
		this.vehicleId = vehicleId;
		this.amountPaid = amountPaid;
	}

	public UUID getReceiptId() {
		return receiptId;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getVehicleId() {
		return vehicleId;
	}

}
