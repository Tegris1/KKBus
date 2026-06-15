package pasir.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pasir.dtos.ReservationDto;
import pasir.dtos.ReservationTicketDto;
import pasir.model.Reservation;
import pasir.services.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }



    @GetMapping("/transactions")
    public ResponseEntity<List<Reservation>> getAllReservations(){
        return ResponseEntity.ok(reservationService.getAllTransactions());
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationTicketDto>> getRouteReservations(){
        return ResponseEntity.ok(reservationService.getRouteReservations());
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDto reservationDto
            ){
        Reservation changedReservation = reservationService.updateTransaction(id, reservationDto);
        return ResponseEntity.ok(changedReservation);
    }

    @PostMapping({"/transactions", "/reservations"})
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationDto reservationDto){
        Reservation newReservation = reservationService.createTransaction(reservationDto);
        return ResponseEntity.ok(newReservation);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id){
        reservationService.deleteTransaction(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id){
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

}
