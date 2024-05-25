package com.nonso.bankapp.service;

import com.nonso.bankapp.dto.ErrorCode;
import com.nonso.bankapp.dto.request.AdminRegistrationRequest;
import com.nonso.bankapp.dto.request.AuthenticationRequest;
import com.nonso.bankapp.dto.response.AuthenticationResponse;
import com.nonso.bankapp.entities.Admin;
import com.nonso.bankapp.entities.Token;
import com.nonso.bankapp.entities.enums.Role;
import com.nonso.bankapp.exception.BankAppException;
import com.nonso.bankapp.exception.UnAuthorizedException;
import com.nonso.bankapp.repository.AdminRepository;
import com.nonso.bankapp.repository.TokenRepository;
import com.nonso.bankapp.security.JwtService;
//import com.nonso.bankapp.utils.CloudinaryService;
import com.nonso.bankapp.utils.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.nonso.bankapp.dto.ErrorCode.INVALID_CREDENTIALS;
import static com.nonso.bankapp.entities.enums.Role.ADMIN;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    private final AdminRepository adminRepository;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;



    @Transactional
    public void register(AdminRegistrationRequest request, MultipartFile file) {
        try {
            var admin = Admin.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .imageUrl(cloudinaryService.uploadImage(file))
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(ADMIN)
                    .build();
            adminRepository.save(admin);
        } catch (Exception e) {
            log.error(format("An error occurred while creating employee account. " +
                    "Please contact support. " + " Possible reasons: %s", e.getLocalizedMessage()));
            throw new BankAppException("An error occurred while creating employee account, please contact support.", INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            var admin = (Admin) authentication.getPrincipal();
            var token = jwtService.generateJwtToken(admin);
            revokeAllAdminToken(admin);
            persistAdminToken(admin, token);

            return new AuthenticationResponse(token);
        } catch (Exception e) {
            log.error(format("An error occurred while authenticating login request, please contact support. " +
                    "Possible reasons: %s", e.getLocalizedMessage()));
            throw new UnAuthorizedException("Invalid email or/and password", INVALID_CREDENTIALS);
        }
    }

    private void revokeAllAdminToken(Admin admin) {
        tokenRepository.invalidateAllAdminTokens(admin.getId());
    }

    private void persistAdminToken(Admin admin, String token) {
        tokenRepository.save(new Token(token, admin));
    }
}
