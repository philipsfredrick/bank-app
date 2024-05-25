package com.nonso.bankapp.testcontainer;

import org.testcontainers.containers.MySQLContainer;

public class BankAppMySQLDBContainer extends MySQLContainer<BankAppMySQLDBContainer> {
    private static final String IMAGE_VERSION = "mysql:latest";

    private static final String MYSQL_DATABASE = "bank_app";

    private static final String MYSQL_USER = "dev_user";

    private static final String MYSQL_PASSWORD = "2023mercedes";

    private static BankAppMySQLDBContainer container;

    public BankAppMySQLDBContainer() {
        super(IMAGE_VERSION);
    }

    public static  BankAppMySQLDBContainer getInstance() {
        if (container == null) {
            container = (new BankAppMySQLDBContainer())
                    .withEnv("TZ", "America/New_York")
                    .withDatabaseName(MYSQL_DATABASE)
                    .withUsername(MYSQL_USER)
                    .withPassword(MYSQL_PASSWORD);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        String additionalParams = "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8" +
                "&allowPublicKeyRetrieval=true&caseInsensitiveIdentifiers=true";
        System.setProperty("DB_URL", container.getJdbcUrl() + additionalParams);
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
        //DO NOT DELETE THIS METHOD.
        //We don't want to execute super.stop() because we want to reuse docker image
    }
}
