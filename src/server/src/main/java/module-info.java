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

    requires bcrypt;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.zaxxer.hikari;
    requires jakarta.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires org.hibernate.orm.hikaricp;
    requires org.slf4j;
    requires org.yaml.snakeyaml;

    requires static lombok;

    opens mop.app.server to javafx.fxml;
    opens mop.app.server.controller to javafx.fxml;
    opens mop.app.server.dao to javafx.base;
    opens mop.app.server.dto to org.hibernate.orm.core;
    opens mop.app.server.util to javafx.base;

    exports mop.app.server;
    exports mop.app.server.controller;
    exports mop.app.server.dao;
    exports mop.app.server.dto;
    exports mop.app.server.util;
}