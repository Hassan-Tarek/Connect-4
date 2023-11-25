module org.connect4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.connect4 to javafx.fxml;
    exports org.connect4;
}