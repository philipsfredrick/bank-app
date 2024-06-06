package com.nonso.bankapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResource implements Serializable {

    @Serial
    private static final long serialVersionUID = -2118644437452081625L;


    @NotNull(message = "id must not be blank")
    private Long id;

    @JsonProperty("first_name")
    @NotBlank(message = "firstname must not be blank")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "lastname must not be blank")
    private String lastName;

    @Email(message = "email must not be blank")
    @JsonProperty("email")
    @NotBlank(message = "email must not be blank")
    private String email;

    @JsonProperty("accounts")
    @NotNull(message = "accounts must have a value")
    private List<WalletResource> bankAccounts;
}
