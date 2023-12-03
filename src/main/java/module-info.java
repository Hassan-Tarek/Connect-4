module org.connect4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.logging;

    opens org.connect4 to javafx.fxml;
    exports org.connect4;
    exports org.connect4.game.core;
    opens org.connect4.game.core to javafx.fxml;
    exports org.connect4.game.enums;
    opens org.connect4.game.enums to javafx.fxml;
    exports org.connect4.game.exceptions;
    opens org.connect4.game.exceptions to javafx.fxml;
    exports org.connect4.game.utils;
    opens org.connect4.game.utils to javafx.fxml;
    exports org.connect4.ai.enums;
    opens org.connect4.ai.enums to javafx.fxml;
    exports org.connect4.ai.heuristics;
    opens org.connect4.ai.heuristics to javafx.fxml;
    exports org.connect4.ai.strategies;
    opens org.connect4.ai.strategies to javafx.fxml;
    exports org.connect4.ai.utils;
    opens org.connect4.ai.utils to javafx.fxml;
}