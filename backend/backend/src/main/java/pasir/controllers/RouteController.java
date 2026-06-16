package pasir.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pasir.dtos.RouteDto;
import pasir.dtos.RouteOccurrenceDto;
import pasir.model.Route;
import pasir.services.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor()
public class RouteController {
    private final RouteService routeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'SECRETARY')")
    @PostMapping
    public ResponseEntity<Route> createRoute(@Valid @RequestBody RouteDto routeDto) {
        Route newRoute = routeService.createRoute(routeDto);
        return ResponseEntity.ok().body(newRoute);
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping(params = {"destination","origin"})
    public ResponseEntity<List<RouteOccurrenceDto>> getAllRoutesByDestinationAndOrigin(@Valid @RequestParam String destination, @Valid @RequestParam String origin) {
        return ResponseEntity.ok(routeService.findAllByDestinationAndOrigin(destination, origin));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @Valid @RequestBody RouteDto routeDto) {
        Route updatedRoute = routeService.updateRoute(routeDto, id);
        if(updatedRoute == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok().body(updatedRoute);

    }
}
