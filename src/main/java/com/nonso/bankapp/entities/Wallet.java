package com.nonso.bankapp.entities;

import com.nonso.bankapp.entities.enums.AccountType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE wallet SET deleted_at = NOW() WHERE  id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Table(name = "wallet")
public class Wallet {
    @Id
    @GenericGenerator(name = "native", strategy = "native")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "Missing required field wallet_no")
    @Column(name = "wallet_no", nullable = false)
    private String walletNumber;

    @NotNull(message = "Missing required field currency_code")
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @NotNull(message = "Missing required field balance")
    @Column(name = "balance", scale = 9, precision = 18, nullable = false)
    private BigDecimal balance;

    @ToString.Exclude
    @NotNull(message = "Missing required field account_id")
    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

//    @Enumerated(EnumType.STRING)
//    @NotNull(message = "account type must be specified")
//    @Column(name = "account_type", columnDefinition = "ENUM('SAVINGS','CURRENT','FIXED_DEPOSIT','DOMICILIARY')", nullable = false)
//    private AccountType accountType;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
