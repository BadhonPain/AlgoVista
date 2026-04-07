package com.AlgoVista.dp;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.AlgoVista.utils.ShortcutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KnapsackController {
    @FXML private HBox rootPane;
    @FXML private VBox shopContainer;
    @FXML private GridPane dpGrid;
    @FXML private Pane ghostPane;
    @FXML private TextField inputCapacity, itemName, itemWeight, itemValue;
    @FXML private Label statusLabel, speedLabel;
    @FXML private Slider speedSlider;
    @FXML private TextFlow pseudoCodeFlow;

    private static class Item {
        String name;
        int weight;
        int value;
        Color color;

        Item(String name, int weight, int value, Color color) {
            this.name = name; this.weight = weight; this.value = value; this.color = color;
        }
    }

    private List<Item> items = new ArrayList<>();
    private int[][] dp;
    private StackPane[][] cells;
    private int capacity = 7;
    private int currItemIdx = 1;
    private int currWeight = 0;
    private double animationSpeed = 1.0;
    private boolean isAnimating = false;
    private Timeline timeline;

    private final String[] pseudoCode = {
        "int knapsack(Item[] items, int W) {",
        "  int n = items.length;",
        "  int[][] dp = new int[n+1][W+1];",
        "  for (int i=1; i<=n; i++) {",
        "    for (int w=0; w<=W; w++) {",
        "      if (items[i-1].weight <= w)",
        "        dp[i][w] = max(items[i-1].value + dp[i-1][w-items[i-1].weight], dp[i-1][w]);",
        "      else",
        "        dp[i][w] = dp[i-1][w];",
        "    }",
        "  }",
        "  return dp[n][W];",
        "}"
    };

    @FXML
    public void initialize() {
        if (speedSlider != null) {
            double initSpeed = com.AlgoVista.utils.SettingsManager.getSpeed();
            speedSlider.setValue(initSpeed);
            animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(initSpeed);
            if (speedLabel != null) { speedLabel.setText(String.format("%.2fx", initSpeed)); }
        }
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationSpeed = com.AlgoVista.utils.SettingsManager.getTimelineRate(newVal.doubleValue());
            speedLabel.setText(String.format("%.2fx", newVal.doubleValue()));
            if (timeline != null) timeline.setRate(animationSpeed);
        });
        
        setupDefaultItems();
        setupPseudoCode();
        resetVisualization();

        rootPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ShortcutManager.register(newVal,
                    this::playPauseToggle,
                    this::stepVisualization,
                    this::resetItems,
                    this::backToCategory
                );
            }
        });
    }

    private void setupDefaultItems() {
        items.clear();
        items.add(new Item("Diamond", 2, 3, Color.web("#38bdf8")));
        items.add(new Item("Gold", 3, 4, Color.web("#f59e0b")));
        items.add(new Item("Emerald", 4, 5, Color.web("#10b981")));
        renderShop();
    }

    @FXML
    private void addItem() {
        try {
            String name = itemName.getText().isEmpty() ? "Item " + (items.size() + 1) : itemName.getText();
            int w = Integer.parseInt(itemWeight.getText());
            int v = Integer.parseInt(itemValue.getText());
            
            Random rand = new Random();
            Color color = Color.hsb(rand.nextDouble() * 360, 0.7, 0.9);
            
            items.add(new Item(name, w, v, color));
            renderShop();
            
            itemName.clear();
            itemWeight.clear();
            itemValue.clear();
        } catch (NumberFormatException e) {
            // Silently fail or show alert
        }
    }

    private void removeItem(Item item) {
        stopAnimation();
        items.remove(item);
        renderShop();
        resetVisualization();
    }

    private void stopAnimation() {
        if (timeline != null) timeline.stop();
        isAnimating = false;
    }

    private void renderShop() {
        shopContainer.getChildren().clear();
        for (Item item : items) {
            shopContainer.getChildren().add(createItemCard(item));
        }
    }

    private VBox createItemCard(Item item) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #0f172a; -fx-padding: 10; -fx-border-color: #334155; -fx-border-radius: 8; -fx-background-radius: 8; -fx-position: relative;");
        
        // Delete button
        Button delBtn = new Button("×");
        delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 0; -fx-cursor: hand;");
        delBtn.setOnAction(e -> removeItem(item));
        
        StackPane header = new StackPane();
        Label name = new Label(item.name.toUpperCase());
        name.setTextFill(item.color);
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        
        header.getChildren().addAll(name, delBtn);
        StackPane.setAlignment(delBtn, Pos.TOP_RIGHT);

        HBox stats = new HBox(10);
        stats.setAlignment(Pos.CENTER);
        Label wLbl = new Label("W: " + item.weight); wLbl.setTextFill(Color.web("#94a3b8"));
        Label vLbl = new Label("V: " + item.value); vLbl.setTextFill(Color.web("#cbd5e1"));
        vLbl.setStyle("-fx-font-weight: bold;");
        stats.getChildren().addAll(wLbl, vLbl);
        
        card.getChildren().addAll(header, stats);
        return card;
    }

    private void setupPseudoCode() {
        pseudoCodeFlow.getChildren().clear();
        for (String line : pseudoCode) {
            Text text = new Text(line + "\n");
            text.setFill(Color.web("#94a3b8"));
            text.setFont(Font.font("Monospaced", 14));
            pseudoCodeFlow.getChildren().add(text);
        }
    }

    private void highlightPseudoCode(int lineIndex) {
        for (int i = 0; i < pseudoCodeFlow.getChildren().size(); i++) {
            Text text = (Text) pseudoCodeFlow.getChildren().get(i);
            if (i == lineIndex) {
                text.setFill(Color.web("#f59e0b"));
                text.setStyle("-fx-font-weight: bold;");
            } else {
                text.setFill(Color.web("#94a3b8"));
                text.setStyle("-fx-font-weight: normal;");
            }
        }
    }

    private void playPauseToggle() {
        if (isAnimating) {
            stopAnimation();
        } else {
            if (currItemIdx > items.size()) {
                startVisualization();
            } else {
                isAnimating = true;
                autoStep();
            }
        }
    }

    @FXML
    private void startVisualization() {
        stopAnimation();
        try {
            capacity = Integer.parseInt(inputCapacity.getText());
        } catch (NumberFormatException e) { capacity = 7; }
        resetVisualization();
        initializeGrid();
        isAnimating = true;
        autoStep();
    }

    private void initializeGrid() {
        dpGrid.getChildren().clear();
        int rows = items.size() + 1, cols = capacity + 1;
        dp = new int[rows][cols];
        cells = new StackPane[rows][cols];
        for (int j = 0; j < cols; j++) {
            Label l = new Label("W=" + j); l.setTextFill(Color.web("#94a3b8"));
            dpGrid.add(l, j + 1, 0);
        }
        for (int i = 0; i < rows; i++) {
            Label l = new Label(i == 0 ? "Empty" : items.get(i-1).name);
            l.setTextFill(i == 0 ? Color.web("#94a3b8") : items.get(i-1).color);
            dpGrid.add(l, 0, i + 1);
            for (int j = 0; j < cols; j++) {
                StackPane cell = createCell(0);
                dpGrid.add(cell, j + 1, i + 1);
                cells[i][j] = cell;
                if (i == 0) ((Label)cell.getChildren().get(1)).setText("0");
            }
        }
    }

    private StackPane createCell(int val) {
        StackPane cell = new StackPane(); cell.setPrefSize(50, 50);
        Rectangle r = new Rectangle(48, 48);
        r.setFill(Color.web("#0f172a")); r.setStroke(Color.web("#334155"));
        r.setArcHeight(5); r.setArcWidth(5);
        Label lbl = new Label(String.valueOf(val)); lbl.setTextFill(Color.WHITE);
        cell.getChildren().addAll(r, lbl);
        return cell;
    }

    @FXML
    private void stepVisualization() {
        if (dp == null || dpGrid.getChildren().isEmpty()) initializeGrid();
        if (currItemIdx <= items.size()) processNextCell();
        else {
            stopAnimation();
            updateResultLabel();
        }
    }

    private void processNextCell() {
        int i = currItemIdx, w = currWeight;
        Item item = items.get(i - 1);
        StackPane cell = cells[i][w];
        for (Node n : shopContainer.getChildren()) n.setStyle("-fx-background-color: #0f172a; -fx-padding: 10; -fx-border-color: #334155; -fx-border-radius: 8;");
        Node shopItemNode = shopContainer.getChildren().get(i - 1);
        shopItemNode.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); -fx-padding: 10; -fx-border-color: #f59e0b; -fx-border-radius: 8;");

        if (item.weight <= w) {
            highlightPseudoCode(6);
            int takeVal = item.value + dp[i - 1][w - item.weight], skipVal = dp[i - 1][w];
            dp[i][w] = Math.max(takeVal, skipVal);
            animateDecision(cell, takeVal, skipVal, item, dp[i][w]);
        } else {
            highlightPseudoCode(8);
            dp[i][w] = dp[i - 1][w];
            ((Label)cell.getChildren().get(1)).setText(String.valueOf(dp[i][w]));
        }
        currWeight++;
        if (currWeight > capacity) {
            currWeight = 0; currItemIdx++;
        }
    }

    private void animateDecision(StackPane cell, int take, int skip, Item item, int finalVal) {
        VBox decisionBox = new VBox(2); decisionBox.setAlignment(Pos.CENTER);
        Label takeLbl = new Label("T:" + take); takeLbl.setTextFill(Color.web("#10b981"));
        Label skipLbl = new Label("S:" + skip); skipLbl.setTextFill(Color.web("#94a3b8"));
        decisionBox.getChildren().addAll(takeLbl, skipLbl);
        cell.getChildren().add(decisionBox);
        
        PauseTransition p = new PauseTransition(Duration.millis(800 / animationSpeed));
        p.setOnFinished(e -> {
            cell.getChildren().remove(decisionBox);
            ((Label)cell.getChildren().get(1)).setText(String.valueOf(finalVal));
            if (take >= skip) ((Rectangle)cell.getChildren().get(0)).setFill(Color.web("#064e3b"));
            if (currItemIdx > items.size()) updateResultLabel();
        });
        p.play();
    }

    private void updateResultLabel() {
        if (dp != null && items.size() > 0) {
            statusLabel.setText("Maximum Value: " + dp[items.size()][capacity]);
        }
    }

    private void autoStep() {
        stopAnimation();
        timeline = new Timeline(new KeyFrame(Duration.millis(1200), e -> stepVisualization()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setRate(animationSpeed);
        timeline.play();
        isAnimating = true;
    }

    @FXML private void resetItems() {
        stopAnimation();
        items.clear();
        renderShop();
        resetVisualization();
    }

    @FXML
    private void resetVisualization() {
        stopAnimation();
        currItemIdx = 1; currWeight = 0;
        statusLabel.setText("Maximum Value: 0");
        highlightPseudoCode(-1);
        if (dpGrid != null) dpGrid.getChildren().clear();
        for (Node n : shopContainer.getChildren()) n.setStyle("-fx-background-color: #0f172a; -fx-padding: 10; -fx-border-color: #334155; -fx-border-radius: 8;");
    }

    @FXML
    private void backToCategory() {
        stopAnimation();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DP_Category.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
