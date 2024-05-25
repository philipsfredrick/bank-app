package com.nonso.bankapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PaginatedTransactionDetailResource implements Serializable {

    @Serial
    private static final long serialVersionUID = 6608508297350485010L;

    @NotNull(message = "content must have a value")
    @JsonProperty("content")
    private List<TransactionDetailResource> transactionDetailResource;

    @NotNull(message = "current_page must have a value")
    @JsonProperty("current_page")
    private Integer currentPage;

    @NotNull(message = "total_page must have a value")
    @JsonProperty("total_page")
    private Integer totalPage;

    @NotNull(message = "total_elements must have a value")
    @JsonProperty("total_elements")
    private Long totalElements;
}
