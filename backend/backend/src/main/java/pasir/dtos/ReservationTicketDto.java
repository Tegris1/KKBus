package pasir.dtos;

import pasir.model.Reservation;

import java.time.LocalDateTime;

public record ReservationTicketDto(
        Long id,
        Double amount,
        Integer seats,
        Long routeId,
        String origin,
        String destination,
        LocalDateTime departureTime
) {
    public static ReservationTicketDto from(Reservation reservation) {
        return new ReservationTicketDto(
                reservation.getId(),
                reservation.getAmount(),
                reservation.getSeats(),
                reservation.getRoute().getId(),
                reservation.getRoute().getOrigin(),
                reservation.getRoute().getDestination(),
                reservation.getTravelDepartureTime() == null
                        ? reservation.getRoute().getDepartureTime()
                        : reservation.getTravelDepartureTime()
        );
    }
}
