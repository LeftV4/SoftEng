module org.example.softeng {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // UI Libraries
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires eu.hansolo.toolbox;
    requires eu.hansolo.toolboxfx;
    requires eu.hansolo.fx.countries;
    requires eu.hansolo.fx.heatmap;

    // Icon Libraries
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    //Game Engine
    requires com.almasb.fxgl.all;

    //Permissions
    opens Classes to javafx.fxml;
    exports Classes;
}