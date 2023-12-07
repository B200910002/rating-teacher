package com.ocean.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ocean.security.jwt.JWTFilter;
import com.ocean.security.jwt.TokenProvider;
import com.ocean.service.StudentService;
import com.ocean.service.dto.StudentDTO;
import com.ocean.web.rest.vm.LoginVM;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final StudentService studentService; // Add StudentService

    public UserJWTController(
        TokenProvider tokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        StudentService studentService
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.studentService = studentService; // Initialize StudentService
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/get_auth_info")
    public ResponseEntity<?> getAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Assuming username is the student code

        // Fetch student info based on username (student code)
        Optional<StudentDTO> studentDTO = studentService.findOneByStudentCode(username);

        if (studentDTO.isPresent()) {
            // Assuming the token is still valid and you have access to it
            String token = ""; // Extract the token from the SecurityContext or Authentication object

            // Create a response object containing student info and token
            AuthInfoResponse response = new AuthInfoResponse(studentDTO.get(), token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Class for response
    static class AuthInfoResponse {

        private StudentDTO studentInfo;
        private String token;

        public AuthInfoResponse(StudentDTO studentInfo, String token) {
            this.studentInfo = studentInfo;
            this.token = token;
        }

        // Getters
        public StudentDTO getStudentInfo() {
            return studentInfo;
        }

        public String getToken() {
            return token;
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
