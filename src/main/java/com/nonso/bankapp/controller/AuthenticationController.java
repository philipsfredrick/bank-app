package com.nonso.bankapp.controller;

import com.google.gson.Gson;
import com.nonso.bankapp.dto.request.AdminRegistrationRequest;
import com.nonso.bankapp.dto.request.AuthenticationRequest;
import com.nonso.bankapp.dto.response.AuthenticationResponse;
import com.nonso.bankapp.dto.response.RegistrationResponse;
import com.nonso.bankapp.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistrationResponse> register(@RequestParam("register") String register,
                                                         @RequestParam("file") MultipartFile file) {
        AdminRegistrationRequest request = new Gson().fromJson(register, AdminRegistrationRequest.class);
        authenticationService.register(request,file);
        return new ResponseEntity<>(new RegistrationResponse("Admin account created successfully"), CREATED);
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return new ResponseEntity<>(authenticationService.authenticate(request), OK);
    }
}
