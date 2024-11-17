module mop.app.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires org.slf4j;
    requires jakarta.mail;

    requires static lombok;
    requires java.desktop;

    opens mop.app.client to javafx.fxml;
    opens mop.app.client.controller.user;
    exports mop.app.client.controller.user;
    opens mop.app.client.controller to javafx.fxml;
    exports mop.app.client;
    exports mop.app.client.controller;
}