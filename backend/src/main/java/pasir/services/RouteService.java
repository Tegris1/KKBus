package pasir.services;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import pasir.dtos.RouteDto;
import pasir.model.Route;
import pasir.repositories.RouteRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    public Route findById(long id) {
        return routeRepository.findById(id).orElse(null);
    }

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public Route createRoute(RouteDto routeDto) {
        Route route = new Route();
        route.setDestination(routeDto.getDestination());
        route.setPrice(routeDto.getPrice());
        route.setOrigin(routeDto.getOrigin());
        route.setArrivalTime(routeDto.getArrivalTime());
        route.setDepartureTime(routeDto.getDepartureTime());
        return routeRepository.save(route);
    }
}
