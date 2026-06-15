package pasir.dtos;

import pasir.model.User;

public record DriverOptionDto(Long id, String name) {
    public static DriverOptionDto from(User user) {
        return new DriverOptionDto(user.getId(), user.getUsername());
    }
}
