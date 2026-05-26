package pasir.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pasir.dtos.RouteDto;
import pasir.model.Route;
import pasir.services.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor()
public class RouteController {
    private final RouteService routeService;

    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<List<Route>> getAllRoutesByDestinationAndOrigin(@Valid @RequestParam String destination, @Valid @RequestParam String origin) {
        return ResponseEntity.ok(routeService.findAllByDestinationAndOrigin(destination, origin));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<Route> updateRoute(@Valid @RequestBody RouteDto routeDto, @Valid @RequestBody Long id) {
        Route updatedRoute = routeService.updateRoute(routeDto, id);
        if(updatedRoute == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok().body(updatedRoute);

    }
}
