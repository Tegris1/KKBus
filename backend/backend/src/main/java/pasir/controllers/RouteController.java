package pasir.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pasir.dtos.RouteDto;
import pasir.model.Route;
import pasir.services.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class RouteController {
    @Autowired
    private RouteService routeService;

    public  RouteController(RouteService routeService) {
        this.routeService = routeService;
    }
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
}
