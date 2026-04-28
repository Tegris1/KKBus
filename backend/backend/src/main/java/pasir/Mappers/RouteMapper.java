package pasir.Mappers;

import org.mapstruct.Mapper;
import pasir.dtos.RouteDto;
import pasir.model.Route;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    Route toEntity(RouteDto dto);
    RouteDto toDto(Route entity);
    Route update(Route route, RouteDto routeDto);
    Route update(Long id, RouteDto routeDto);
}
