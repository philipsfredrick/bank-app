INSERT INTO `account` (`id`, `email`, `first_name`, `last_name`, `created_at`, `updated_at`, `deleted_at`)
VALUES (4, 'account@test.com', 'test', 'account', NOW(), NOW(), NULL),
       (5, 'account2@test.com', 'Jane', 'John', NOW(), NOW(), NULL);


INSERT INTO `wallet` (`id`, `wallet_no`, `currency_code`, `balance`, `account_id`, `created_at`, `updated_at`,
                      `deleted_at`)
VALUES (11, '9433010941', 'NGN', 3000.0000, 4, NOW(), NOW(), NULL),
       (12, '9688710733', 'USD', 549000.0000, 4, NOW(), NOW(), NULL),
       (13, '1875143698', 'CAD', 1000000.0000, 4, NOW(), NOW(), NULL),
       (14, '5334505531', 'EUR', 700000.0000, 4, NOW(), NOW(), NULL),
       (15, '6645873522', 'EUR', 3040.0000, 5, NOW(), NOW(), NULL),
       (16, '8671266853', 'GBP', 550800.0000, 5, NOW(), NOW(), NULL),
       (17, '7035476817', 'CAD', 1000000.0000, 5, NOW(), NOW(), NULL),
       (18, '9130704994', 'USD', 1001000.0000, 5, NOW(), NOW(), NULL),
       (19, '7324108711', 'USD', 1000.0000, 5, NOW(), NOW(), NULL);

