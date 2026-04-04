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
@RequestMapping("/route")
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
}
