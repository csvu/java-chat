module mop.app.server {
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

    requires static lombok;

    opens mop.app.server to javafx.fxml;
    opens mop.app.server.controller to javafx.fxml;
    exports mop.app.server;
    exports mop.app.server.controller;
}