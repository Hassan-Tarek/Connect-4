package org.connect4.client.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.connect4.client.gui.views.AILevelsView;
import org.connect4.client.utils.Constants;
import org.connect4.game.ai.enums.AIType;

import java.util.Objects;
import java.util.function.Consumer;

public class AILevelsViewController implements BaseController {
    private final Stage stage;
    private final Stage parentStage;
    private final AILevelsView aiLevelsView;
    private final Consumer<AIType> aiTypeConsumer;

    public AILevelsViewController(Stage parentStage, Consumer<AIType> aiTypeConsumer) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.aiLevelsView = new AILevelsView();
        this.aiTypeConsumer = aiTypeConsumer;

        initialize();
        setEventHandlers();
    }

    @Override
    public void initialize() {
        aiLevelsView.getEasyLevelButton().setFocusTraversable(false);
        aiLevelsView.getMediumLevelButton().setFocusTraversable(false);
        aiLevelsView.getHardLevelButton().setFocusTraversable(false);

    }

    @Override
    public void setEventHandlers() {
        aiLevelsView.getEasyLevelButton().setOnAction(this::onAILevelSelected);
        aiLevelsView.getMediumLevelButton().setOnAction(this::onAILevelSelected);
        aiLevelsView.getHardLevelButton().setOnAction(this::onAILevelSelected);
    }

    @Override
    public void showView() {
        Scene scene = new Scene(aiLevelsView.getLayout(), Constants.AI_LEVELS_VIEW_WIDTH, Constants.AI_LEVELS_VIEW_HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Constants.STYLE_SHEET)).toExternalForm());

        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("AI Levels");
        stage.showAndWait();
    }

    @Override
    public void closeView() {
        stage.close();
    }

    private void onAILevelSelected(ActionEvent event) {
        if (event.getSource() == aiLevelsView.getEasyLevelButton()) {
            aiTypeConsumer.accept(AIType.RANDOM_CHOICE_AI);
        } else if (event.getSource() == aiLevelsView.getMediumLevelButton()) {
            aiTypeConsumer.accept(AIType.MINIMAX_WITHOUT_PRUNING_AI);
        } else if (event.getSource() == aiLevelsView.getHardLevelButton()) {
            aiTypeConsumer.accept(AIType.MINIMAX_WITH_PRUNING_AI);
        }

        closeView();
    }
}
