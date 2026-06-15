package pasir.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pasir.dtos.RoleUpdateDto;
import pasir.dtos.DriverOptionDto;
import pasir.dtos.UserDto;
import pasir.model.User;
import pasir.model.Role;
import pasir.services.UserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<DriverOptionDto>> getDrivers() {
        return ResponseEntity.ok(userService.findByRole(Role.EMPLOYEE).stream()
                .map(DriverOptionDto::from)
                .toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@RequestBody UserDto dto, @PathVariable Long id){
        User user = userService.updateUserDetails(dto, id);
        if(user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> makeEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(userService.makeEmployee(id));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestBody @Valid RoleUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUserRole(id, dto.getRole()));
    }
}
