package com.AlgoVista.graphs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GraphSubCardController {
    @FXML private Label cardTitle;

    public void setData(String title) {
        cardTitle.setText(title);
    }
}