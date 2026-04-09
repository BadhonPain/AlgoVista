package com.AlgoVista.sorting;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SortingCategoryController {

    public void BackToDashBoard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openVisualizer(MouseEvent event) {
        String algoType = "";
        Node source = (Node) event.getSource();
        String id = source.getId();

        switch (id) {
            case "bubbleCard":
                algoType = "Bubble Sort";
                break;
            case "selectionCard":
                algoType = "Selection Sort";
                break;
            case "insertionCard":
                algoType = "Insertion Sort";
                break;
            case "countingCard":
                algoType = "Counting Sort";
                break;
            case "radixCard":
                algoType = "Radix Sort";
                break;
            case "bucketCard":
                algoType = "Bucket Sort";
                break;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SortingVisualizer.fxml"));
            Parent root = loader.load();
            SortingVisualizerController controller = loader.getController();
            controller.initAlgorithm(algoType);

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
