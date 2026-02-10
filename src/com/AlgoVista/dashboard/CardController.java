package com.AlgoVista.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CardController {
    @FXML
    private Label algoName;

    public void setData(String name) {
        algoName.setText(name);
    }

    @FXML
    private void handleCardClick() {
        System.out.println("Card clicked!");
        String algo = algoName.getText();

        if (algo.equals("Graph")) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphCategory.fxml"));

                Parent root = loader.load();

                Stage stage = (Stage) algoName.getScene().getWindow();

                double width = stage.getWidth();
                double height = stage.getHeight();
                double x = stage.getX();
                double y = stage.getY();

                Scene scene = new Scene(root, width, height);
                stage.setScene(scene);

                stage.setX(x);
                stage.setY(y);

                System.out.println("GraphCategory loaded successfully!");
            } catch (IOException e) {
                System.err.println("Error: Could not find GraphCategory.fxml. Check your resources folder.");
                e.printStackTrace();
            }
        }
    }
}