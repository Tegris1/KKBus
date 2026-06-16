package pasir.dtos;

public record DriverPassengerReservationDto(
        Long reservationId,
        String passengerName,
        String email,
        String phoneNumber,
        Integer seats,
        String boardingStop,
        String alightingStop,
        String discountType
) {
}
