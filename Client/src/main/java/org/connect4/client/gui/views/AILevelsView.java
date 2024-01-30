package org.connect4.client.gui.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import org.connect4.client.utils.Constants;

public class AILevelsView {
    private final VBox layout;
    private final Button easyLevelButton;
    private final Button mediumLevelButton;
    private final Button hardLevelButton;

    public AILevelsView() {
        this.layout = new VBox(Constants.LAYOUT_PADDING);
        this.easyLevelButton = new Button("Easy");
        this.mediumLevelButton = new Button("Medium");
        this.hardLevelButton = new Button("Hard");

        createLayout();
    }

    public VBox getLayout() {
        return layout;
    }

    public Button getEasyLevelButton() {
        return easyLevelButton;
    }

    public Button getMediumLevelButton() {
        return mediumLevelButton;
    }

    public Button getHardLevelButton() {
        return hardLevelButton;
    }

    private void createLayout() {
        // Button setup
        easyLevelButton.setMinWidth(120);
        easyLevelButton.setMinHeight(40);
        easyLevelButton.setId("easyLevelButton");

        mediumLevelButton.setMinWidth(120);
        mediumLevelButton.setMinHeight(40);
        mediumLevelButton.setId("mediumLevelButton");

        hardLevelButton.setMinWidth(120);
        hardLevelButton.setMinHeight(40);
        hardLevelButton.setId("hardLevelButton");

        // Layout setup
        layout.setAlignment(Pos.CENTER);
        layout.setMinWidth(Constants.AI_LEVELS_VIEW_WIDTH);
        layout.setMinHeight(Constants.AI_LEVELS_VIEW_HEIGHT);
        layout.getChildren().addAll(easyLevelButton, mediumLevelButton, hardLevelButton);
    }
}
