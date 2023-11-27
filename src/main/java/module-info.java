module org.connect4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.logging;

    opens org.connect4 to javafx.fxml;
    exports org.connect4;
    exports org.connect4.game;
    opens org.connect4.game to javafx.fxml;
}