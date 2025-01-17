module mop.app.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires bcrypt;
    requires com.zaxxer.hikari;
    requires de.jensd.fx.glyphs.fontawesome;
    requires jakarta.mail;
    requires jakarta.persistence;
    requires javafaker;
    requires org.hibernate.orm.core;
    requires org.hibernate.orm.hikaricp;
    requires org.slf4j;

    requires static lombok;
    requires snakeyaml;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    opens mop.app.client to javafx.fxml;
    opens mop.app.client.controller to javafx.fxml;
    opens mop.app.client.controller.admin to javafx.fxml;
    opens mop.app.client.controller.auth to javafx.fxml;
    opens mop.app.client.controller.user to javafx.fxml;
    opens mop.app.client.model to javafx.base, org.hibernate.orm.core, com.fasterxml.jackson.databind;
    opens mop.app.client.model.user to javafx.base, org.hibernate.orm.core, com.fasterxml.jackson.databind;

    opens mop.app.client.util to javafx.fxml;
    opens mop.app.client.dao to javafx.base;
    opens mop.app.client.dto to javafx.base, org.hibernate.orm.core, com.fasterxml.jackson.databind;

    exports mop.app.client;
    exports mop.app.client.controller;
    exports mop.app.client.controller.admin;
    exports mop.app.client.controller.auth;
    exports mop.app.client.controller.user;
    exports mop.app.client.model;
    exports mop.app.client.model.user;
    exports mop.app.client.util;
    exports mop.app.client.dao;
    exports mop.app.client.dto;
    exports mop.app.client.network;
}