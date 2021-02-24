package com.tomaszekem.userservice.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

import static com.tomaszekem.userservice.user.Constants.BULK_MAX_SIZE;

@RequestMapping("/api/users")
@RestController
@Validated
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<List<UserDTO>> registerUsers(@RequestBody @Size(min = 1, max = BULK_MAX_SIZE) Set<@Valid RegisterUserCommand> commands) {
        List<UserDTO> result = userService.registerNewUsers(commands);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping
    ResponseEntity<List<UserDTO>> updateUsers(@RequestBody @Size(min = 1, max = BULK_MAX_SIZE) List<@Valid UpdateUserCommand> commands) {
        List<UserDTO> result = userService.updateUsers(commands);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping
    ResponseEntity<Void> deleteUsers(@RequestBody @Valid DeleteUsersCommand command) {
        userService.deleteUsers(command);
        return ResponseEntity.ok().build();
    }


}
