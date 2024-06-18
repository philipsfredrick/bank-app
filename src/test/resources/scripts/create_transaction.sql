INSERT INTO `transaction` (`id`, `type`, `amount`, `status`, `currency_code`, `admin_id`, `wallet_id`,
                           `consort_wallet_id`, `created_at`, `updated_at`, `deleted_at`)
VALUES (1, 'DEBIT', 560.000000000, 'SUCCESSFUL', 'USD', 2, 8, 14, NOW(), NOW(), NULL),
       (2, 'DEBIT', 600.000000000, 'SUCCESSFUL', 'EUR', 2, 11, 10, NOW(), NOW(), NULL),
       (3, 'DEBIT', 800.000000000, 'SUCCESSFUL', 'USD', 2, 14, 15, NOW(), NOW(), NULL);

INSERT INTO `transaction_detail` (`id`, `type`, `amount`, `old_balance`, `new_balance`, `wallet_id`, `transaction_id`,
                                  `account_id`, `created_at`, `updated_at`, `deleted_at`)
VALUES (1, 'DEBIT', -560.000000000, 3000.000000000, 2440.000000000, 8, 1, 3, NOW(), NOW(), NULL),
       (2, 'CREDIT', 560.000000000, 3000.000000000, 3560.000000000, 14, 1, 4, NOW(), NOW(), NULL),
       (3, 'DEBIT', -600.000000000, 3560.000000000, 2960.000000000, 11, 2, 4, NOW(), NOW(), NULL),
       (4, 'CREDIT', 600.000000000, 2440.000000000, 3040.000000000, 10, 2, 3, NOW(), NOW(), NULL),
       (5, 'DEBIT', -800.000000000, 550000.000000000, 549200.000000000, 14, 3, 4, NOW(), NOW(), NULL),
       (6, 'CREDIT', 800.000000000, 550000.000000000, 550800.000000000, 15, 3, 4, NOW(), NOW(), NULL);
