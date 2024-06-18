package com.nonso.bankapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6631370518583055750L;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;
}
