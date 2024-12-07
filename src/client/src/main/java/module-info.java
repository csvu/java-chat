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

    requires de.jensd.fx.glyphs.fontawesome;

    requires bcrypt;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.zaxxer.hikari;
    requires jakarta.mail;
    requires jakarta.persistence;
    requires javafaker;
    requires java.sql;
    requires java.desktop;
    requires org.hibernate.orm.core;
    requires org.hibernate.orm.hikaricp;
    requires org.slf4j;
    requires org.yaml.snakeyaml;

    requires static lombok;

    opens mop.app.client to javafx.fxml;
    opens mop.app.client.controller to javafx.fxml;
    opens mop.app.client.controller.admin to javafx.fxml;
    opens mop.app.client.controller.auth to javafx.fxml;
    opens mop.app.client.controller.user to javafx.fxml;
    opens mop.app.client.model to javafx.base;
    opens mop.app.client.util to javafx.fxml;
    opens mop.app.client.dao to javafx.base;
    opens mop.app.client.dto to javafx.base, org.hibernate.orm.core, com.fasterxml.jackson.databind;

    exports mop.app.client;
    exports mop.app.client.controller;
    exports mop.app.client.controller.admin;
    exports mop.app.client.controller.auth;
    exports mop.app.client.controller.user;
    exports mop.app.client.model;
    exports mop.app.client.util;
    exports mop.app.client.dao;
    exports mop.app.client.dto;
    exports mop.app.client.network;
}