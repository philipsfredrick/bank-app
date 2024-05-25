package com.nonso.bankapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4040333413291612929L;

    @NotNull(message = "amount deposit must have a value")
    @Positive(message = "amount deposit must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;

    @Size(min = 10, max = 10, message = "source_wallet_no length must be 10")
    @NotBlank(message = "source_wallet_no must not be blank")
    @JsonProperty("source_wallet_no")
    private String sourceWalletNo;

    @Size(min = 10, max = 10, message = "destination_wallet_no length must be 10")
    @NotBlank(message = "destination_wallet_no must not be blank")
    @JsonProperty("destination_wallet_no")
    private String destinationWalletNo;
}
