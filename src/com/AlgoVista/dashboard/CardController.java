package com.AlgoVista.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CardController {
    @FXML
    private Label algoName;

    public void setData(String name) {
        algoName.setText(name);
    }
}