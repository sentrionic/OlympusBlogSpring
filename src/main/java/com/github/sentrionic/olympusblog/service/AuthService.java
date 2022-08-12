package com.github.sentrionic.olympusblog.service;

import com.github.sentrionic.olympusblog.dto.Email;
import com.github.sentrionic.olympusblog.dto.password.ChangePasswordDTO;
import com.github.sentrionic.olympusblog.dto.password.ResetPasswordDTO;
import com.github.sentrionic.olympusblog.dto.user.AuthDto;
import com.github.sentrionic.olympusblog.dto.user.LoginDTO;
import com.github.sentrionic.olympusblog.dto.user.RegisterDTO;
import com.github.sentrionic.olympusblog.dto.user.UpdateUserDTO;
import com.github.sentrionic.olympusblog.exception.FileUploadException;
import com.github.sentrionic.olympusblog.exception.ProfileNotFoundException;
import com.github.sentrionic.olympusblog.exception.SpringRestException;
import com.github.sentrionic.olympusblog.mapper.UserMapper;
import com.github.sentrionic.olympusblog.model.User;
import com.github.sentrionic.olympusblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redis;
    private final FileUploadService fileUploadService;
    private final MailService mailService;
    private final UserMapper mapper;

    @Transactional
    public AuthDto register(@NotNull RegisterDTO input) {
        var checkUser = userRepository.existsByUsername(input.getUsername());
        if (checkUser) {
            throw new SpringRestException("Username already taken");
        }

        checkUser = userRepository.existsByUsername(input.getEmail());
        if (checkUser) {
            throw new SpringRestException("Email already in use");
        }

        var user = new User();
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername());
        user.setPassword(encodePassword(input.getPassword()));
        user.setImage(String.format("https://gravatar.com/avatar/%s?d=identicon", getImageFromEmail(input.getEmail())));

        userRepository.save(user);

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        return mapper.mapEntityToDto(user);
    }

    public AuthDto login(@NotNull LoginDTO input) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return mapper.mapEntityToDto(getCurrentUser());
    }

    private String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + username));
    }

    public User getOptionalUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    public AuthDto updateUser(@NotNull UpdateUserDTO input) {
        var user = getCurrentUser();

        if (!input.getEmail().equals(user.getEmail())) {
            var emailExists = userRepository.existsByEmail(input.getEmail());
            if (emailExists) throw new SpringRestException("Email already in use");
        }

        if (!input.getUsername().equals(user.getUsername())) {
            var usernameExists = userRepository.existsByUsername(input.getUsername());
            if (usernameExists) throw new SpringRestException("Username already in use");
        }

        if (input.getImage() != null) {
            var directory = String.format("spring/%s/avatar", user.getId());
            try {
                var url = fileUploadService.uploadAvatarImage(input.getImage(), directory);
                user.setImage(url);
            } catch (IOException e) {
                throw new FileUploadException();
            }
        }

        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setBio(input.getBio());

        userRepository.save(user);

        return mapper.mapEntityToDto(user);
    }

    public AuthDto changePassword(@NotNull ChangePasswordDTO input) {
        var user = this.getCurrentUser();
        user.setPassword(passwordEncoder.encode(input.getNewPassword()));

        userRepository.save(user);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        input.getNewPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return mapper.mapEntityToDto(user);
    }

    public boolean forgotPassword(@NotNull String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException("An account with this email does not exist"));
        var token = UUID.randomUUID();
        redis.saveUserId(user.getId().toString(), token);

        var message =
                "Click the following link to reset your email: <a href=\"localhost:3000/reset-password/${token}\">Reset Password</a>";

        mailService.sendMail(new Email("Reset Password", email, message));

        return true;
    }

    @NotNull
    public AuthDto resetPassword(@NotNull ResetPasswordDTO input) {
        var userId = redis.getUserId(input.getToken());

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException("An account with this email does not exist"));

        user.setPassword(passwordEncoder.encode(input.getNewPassword()));

        userRepository.save(user);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        input.getNewPassword()
                )
        );

        redis.deleteKey(input.getToken());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return mapper.mapEntityToDto(user);
    }

    public String getImageFromEmail(String email) {
        return Hex.encodeHexString(email.getBytes(UTF_8));
    }
}
