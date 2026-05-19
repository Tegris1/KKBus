package pasir.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pasir.dtos.RouteDto;
import pasir.model.Route;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    Route toEntity(RouteDto dto);
    RouteDto toDto(Route entity);
    Route update(@MappingTarget Route route, RouteDto routeDto);
}
