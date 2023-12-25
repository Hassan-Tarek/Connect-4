module org.connect4.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires Game;

    opens org.connect4.server to javafx.fxml;
    exports org.connect4.server;
}
