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
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openVisualizer(MouseEvent event) {
        String algoType = "";
        Node source = (Node) event.getSource();
        String id = source.getId();
        
        switch (id) {
            case "bubbleCard": algoType = "Bubble Sort"; break;
            case "selectionCard": algoType = "Selection Sort"; break;
            case "insertionCard": algoType = "Insertion Sort"; break;
            case "mergeCard": algoType = "Merge Sort"; break;
            case "quickCard": algoType = "Quick Sort"; break;
            case "heapCard": algoType = "Heap Sort"; break;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SortingVisualizer.fxml"));
            Parent root = loader.load();
            SortingVisualizerController controller = loader.getController();
            controller.initAlgorithm(algoType);
            
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
