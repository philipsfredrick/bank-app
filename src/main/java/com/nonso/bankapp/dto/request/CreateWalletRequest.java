package com.nonso.bankapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nonso.bankapp.entities.Account;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5262672551090717089L;

    @Size(min = 3, max = 3)
    @NotBlank(message = "currency code must not be blank")
    @JsonProperty("currency_code")
    private String currencyCode;

    @NotNull(message = "initial deposit must have a value")
    @Positive(message = "initial deposit must be positive")
    @JsonProperty("initial_deposit")
    private BigDecimal initialDeposit;

}
