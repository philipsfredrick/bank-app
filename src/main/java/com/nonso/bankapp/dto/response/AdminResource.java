package com.nonso.bankapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResource implements Serializable {

    @Serial
    private static final long serialVersionUID = 2565267229249894706L;

    @NotNull(message = "id must not be blank")
    private Long id;

    @JsonProperty("first_name")
    @NotBlank(message = "firstname must not be blank")
    private String firstname;

    @JsonProperty("last_name")
    @NotBlank(message = "lastname must not be blank")
    private String lastname;
}
