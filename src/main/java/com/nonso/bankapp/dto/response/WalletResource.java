package com.nonso.bankapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResource implements Serializable {

    @Serial
    private static final long serialVersionUID = -2804508162050347652L;

    @NotNull(message = "id must not be blank")
    private Long id;

    @Size(min = 3, max = 3)
    @JsonProperty("currency_code")
    @NotBlank(message = "currency code must not be blank")
    private String currencyCode;

    @JsonProperty("wallet_no")
    @NotNull(message = "walletNumber must have a value")
    private String walletNumber;

    @NotNull(message = "initial deposit must have a value")
    private BigDecimal balance;
}
