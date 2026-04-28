package pasir.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pasir.dtos.UserDto;
import pasir.model.User;
import pasir.services.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto dto,@RequestBody Long id){
        User user = userService.updateUserDetails(dto, id);
        if(user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }
}
