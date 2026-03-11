package com.AlgoVista.graphs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class GraphSubCardController {
    @FXML private VBox cardContainer;
    @FXML private Label cardTitle;

    @FXML
    public void initialize() {
        // Enforce strong clipping to prevent background images from bleeding outside
        // the rounded corners. JavaFX sometimes fails to clip background images correctly
        // with CSS alone if not completely structured right.
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        // Bind the clip dimensions to the container dimensions so it responds to scaling/resizing
        clip.widthProperty().bind(cardContainer.widthProperty());
        clip.heightProperty().bind(cardContainer.heightProperty());
        cardContainer.setClip(clip);
    }

    public void setData(String title) {
        cardTitle.setText(title);
    }
}