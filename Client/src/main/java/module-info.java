module org.connect4.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires Game;


    opens org.connect4.client to javafx.fxml;
    exports org.connect4.client;
}