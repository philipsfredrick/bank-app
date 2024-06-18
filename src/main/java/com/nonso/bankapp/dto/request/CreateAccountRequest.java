package com.nonso.bankapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nonso.bankapp.entities.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1508384104914593410L;

    @NotBlank(message = "firstname must not be blank")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "lastname must not be blank")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "email must not be blank")
    @Email(message = "please enter a valid email address")
    private String email;

    @NotNull(message = "bank account detail must be added")
    @Size(min = 1, message = "bank account detail must be added")
    @JsonProperty("bank_accounts")
    List<CreateWalletRequest> bankAccounts;

}
