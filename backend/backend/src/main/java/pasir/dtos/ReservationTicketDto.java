package pasir.dtos;

import pasir.model.Reservation;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record ReservationTicketDto(
        Long id,
        Double amount,
        BigDecimal discountAmount,
        Integer pointsSpent,
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
                reservation.getDiscountAmount(),
                reservation.getPointsSpent(),
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
