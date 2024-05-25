package com.nonso.bankapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegistrationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4122537134934090702L;

    @NotBlank(message = "firstname must not be blank")
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "lastname must not be blank")
    @JsonProperty("lastName")
    private String lastName;

    @NotBlank(message = "email must not be blank")
    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, message = "password length must not be less than 6")
    private String password;

    @NotBlank(message = "please upload your image file")
    private String imageUrl;

}
