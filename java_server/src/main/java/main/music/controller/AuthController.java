package main.music.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import main.music.dto.UserDTO;
import main.music.model.Users;
import main.music.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        Users user = userService.register(userDTO.getUsername(), userDTO.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        // 사용자 인증 시도
        Optional<Users> foundUserOpt = userService.login(userDTO.getUsername(), userDTO.getPassword());

        if (!foundUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Users foundUser = foundUserOpt.get();
        // 세션 객체 얻기
        HttpSession session = request.getSession(false);
        session.setAttribute("user", foundUser);  // 세션에 사용자 정보 저장

        log.info("User logged in. Session ID: {}, User: {}", session.getId(), foundUser);
        return ResponseEntity.ok("User logged in successfully!");
    }

}
