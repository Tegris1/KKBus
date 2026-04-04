package pasir.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pasir.dtos.ReservationDto;
import pasir.model.Reservation;
import pasir.services.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }



    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(){
        return ResponseEntity.ok(reservationService.getAllTransactions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDto reservationDto
            ){
        Reservation changedReservation = reservationService.updateTransaction(id, reservationDto);
        return ResponseEntity.ok(changedReservation);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationDto reservationDto){
        Reservation newReservation = reservationService.createTransaction(reservationDto);
        return ResponseEntity.ok(newReservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id){
        reservationService.deleteTransaction(id);

        return ResponseEntity.ok().build();
    }

}
