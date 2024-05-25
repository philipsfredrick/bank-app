package com.nonso.bankapp.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -853193466700603827L;

    private String message;
}
