module lecbilling.mokopanemakhetha {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Database
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.postgresql.jdbc;

    // Logging
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // Open packages for JavaFX reflection
    opens lecbilling.mokopanemakhetha to javafx.fxml;
    opens lecbilling.mokopanemakhetha.config to javafx.fxml;
    opens lecbilling.mokopanemakhetha.service to javafx.fxml;
    opens lecbilling.mokopanemakhetha.model to javafx.fxml;

    // Export packages
    exports lecbilling.mokopanemakhetha;
    exports lecbilling.mokopanemakhetha.config;
    exports lecbilling.mokopanemakhetha.service;
    exports lecbilling.mokopanemakhetha.model;
}