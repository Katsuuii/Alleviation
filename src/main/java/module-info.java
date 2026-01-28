module com.example.alleviationfixed {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires javafx.swing;

    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;

    requires org.controlsfx.controls;
    requires org.slf4j;

    // ✅ allow FXML to access controllers
    opens Controller to javafx.fxml;

    // (optional) export if you need access from other modules
    exports Controller;
}
