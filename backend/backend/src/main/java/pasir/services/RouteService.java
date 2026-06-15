package pasir.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pasir.Mappers.RouteMapper;
import pasir.dtos.RouteDto;
import pasir.model.Role;
import pasir.model.Route;
import pasir.repositories.RouteRepository;
import pasir.repositories.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final UserRepository userRepository;

    public Route findById(long id) {
        return routeRepository.findById(id).orElse(null);
    }

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public Route createRoute(RouteDto routeDto) {
        validateDriver(routeDto.getDriverId());
        Route route = routeMapper.toEntity(routeDto);

        return routeRepository.save(route);
    }

    public List<Route> findAllByDestinationAndOrigin(String destination, String origin) {
        return routeRepository.findByDestinationAndOrigin(destination, origin);
    }

    public Route updateRoute(RouteDto routeDto, Long id) {
        validateDriver(routeDto.getDriverId());
        Route route = routeRepository.findById(id).orElse(null);
        if (route == null) {return null;}
        Route updatedRoute = routeMapper.update(route, routeDto);
        return routeRepository.save(updatedRoute);
    }

    private void validateDriver(Long driverId) {
        var driver = userRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono kierowcy"));
        if (driver.getRole() != Role.EMPLOYEE) {
            throw new IllegalArgumentException("Wybrany uzytkownik nie jest kierowca");
        }
    }
}
