package com.github.sentrionic.olympusblog.controller;

import com.github.sentrionic.olympusblog.dto.ValidationError;
import com.github.sentrionic.olympusblog.dto.password.ChangePasswordDTO;
import com.github.sentrionic.olympusblog.dto.password.ForgotPasswordDTO;
import com.github.sentrionic.olympusblog.dto.password.ResetPasswordDTO;
import com.github.sentrionic.olympusblog.dto.user.AuthDto;
import com.github.sentrionic.olympusblog.dto.user.LoginDTO;
import com.github.sentrionic.olympusblog.dto.user.RegisterDTO;
import com.github.sentrionic.olympusblog.dto.user.UpdateUserDTO;
import com.github.sentrionic.olympusblog.mapper.UserMapper;
import com.github.sentrionic.olympusblog.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final UserMapper mapper;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDto register(@Valid @RequestBody RegisterDTO input) {
        return service.register(input);
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthDto login(@RequestBody LoginDTO input) {
        return service.login(input);
    }

    @PostMapping("/users/logout")
    @ResponseStatus(HttpStatus.OK)
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) new SecurityContextLogoutHandler().logout(request, response, authentication);
        return true;
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public AuthDto getUser() {
        var user = service.getCurrentUser();
        return mapper.mapEntityToDto(user);
    }

    @PutMapping("/user")
    public ResponseEntity<Object> updateUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String bio
    ) {
        var input = new UpdateUserDTO(email, username, bio, image);
        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            var list = violations.stream().map(
                    v -> new ValidationError(v.getPropertyPath().toString(), v.getMessage())
            ).toList();
            return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(service.updateUser(input), HttpStatus.OK);
    }

    @PutMapping("/users/change-password")
    @ResponseStatus(HttpStatus.OK)
    public AuthDto changePassword(@Valid @RequestBody ChangePasswordDTO input) {
        return service.changePassword(input);
    }

    @PostMapping("/users/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public boolean forgotPassword(@Valid @RequestBody ForgotPasswordDTO input) {
        return service.forgotPassword(input.getEmail());
    }

    @PostMapping("/users/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public AuthDto resetPassword(@Valid @RequestBody ResetPasswordDTO input) {
        return service.resetPassword(input);
    }

}
