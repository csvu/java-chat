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
    requires javafaker;
    requires java.sql;
    requires jakarta.mail;
    requires de.jensd.fx.glyphs.fontawesome;

    requires static lombok;
    requires java.desktop;

    opens mop.app.client to javafx.fxml;
    opens mop.app.client.controller to javafx.fxml;
    opens mop.app.client.controller.admin to javafx.fxml;
    opens mop.app.client.controller.auth to javafx.fxml;
    opens mop.app.client.controller.user to javafx.fxml;
    opens mop.app.client.model to javafx.base;
    opens mop.app.client.util to javafx.fxml;
    opens mop.app.client.dao to javafx.fxml;
    exports mop.app.client;
    exports mop.app.client.controller;
    exports mop.app.client.controller.admin;
    exports mop.app.client.controller.auth;
    exports mop.app.client.controller.user;
    exports mop.app.client.model;
    exports mop.app.client.util;
    exports mop.app.client.dao;
}