package com.AlgoVista.dp;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

public class LCSController {
    @FXML private HBox rootPane;
    @FXML private GridPane matrixGrid;
    @FXML private Pane pathOverlay;
    @FXML private TextField inputA, inputB;
    @FXML private Label resultLabel, speedLabel;
    @FXML private Slider speedSlider;
    @FXML private TextFlow pseudoCodeFlow;

    private String strA = "", strB = "";
    private int[][] dp;
    private StackPane[][] cells;
    private int currI = 1, currJ = 1;
    private double animationSpeed = 1.0;
    private boolean isAnimating = false;

    private final String[] pseudoCode = {
        "int lcs(String A, String B) {",
        "  int m = A.length(), n = B.length();",
        "  int[][] dp = new int[m+1][n+1];",
        "  for (int i=1; i<=m; i++) {",
        "    for (int j=1; j<=n; j++) {",
        "      if (A[i-1] == B[j-1])",
        "        dp[i][j] = dp[i-1][j-1] + 1;",
        "      else",
        "        dp[i][j] = max(dp[i-1][j], dp[i][j-1]);",
        "    }",
        "  }",
        "  return dp[m][n];",
        "}"
    };

    @FXML
    public void initialize() {
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationSpeed = newVal.doubleValue();
            speedLabel.setText(String.format("%.1fx", animationSpeed));
        });
        setupPseudoCode();

        rootPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ShortcutManager.register(newVal,
                    this::playPauseToggle,
                    this::stepVisualization,
                    this::resetVisualization,
                    this::backToCategory
                );
            }
        });
    }

    private void playPauseToggle() {
        if (isAnimating) {
            isAnimating = false;
        } else {
            if (currI > strA.length()) {
                startVisualization();
            } else {
                isAnimating = true;
                autoStep();
            }
        }
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
                text.setFill(Color.web("#10b981"));
                text.setStyle("-fx-font-weight: bold;");
            } else {
                text.setFill(Color.web("#94a3b8"));
                text.setStyle("-fx-font-weight: normal;");
            }
        }
    }

    @FXML
    private void startVisualization() {
        if (isAnimating) return;
        strA = inputA.getText().toUpperCase();
        strB = inputB.getText().toUpperCase();
        if (strA.isEmpty() || strB.isEmpty()) return;

        resetVisualization();
        initializeGrid();
        isAnimating = true;
        autoStep();
    }

    private void initializeGrid() {
        matrixGrid.getChildren().clear();
        int rows = strA.length() + 1;
        int cols = strB.length() + 1;
        dp = new int[rows + 1][cols + 1];
        cells = new StackPane[rows + 1][cols + 1];

        // Add header labels
        for (int j = 0; j < strB.length(); j++) {
            Label l = createHeaderLabel(String.valueOf(strB.charAt(j)));
            matrixGrid.add(l, j + 2, 0);
        }
        for (int i = 0; i < strA.length(); i++) {
            Label l = createHeaderLabel(String.valueOf(strA.charAt(i)));
            matrixGrid.add(l, 0, i + 2);
        }

        // Initialize cells
        for (int i = 0; i <= strA.length(); i++) {
            for (int k = 0; k <= strB.length(); k++) {
                StackPane cell = createCell(0);
                matrixGrid.add(cell, k + 1, i + 1);
                cells[i + 1][k + 1] = cell;
                if (i == 0 || k == 0) {
                    ((Label)cell.getChildren().get(1)).setText("0");
                } else {
                    ((Label)cell.getChildren().get(1)).setText("");
                }
            }
        }
    }

    private Label createHeaderLabel(String val) {
        Label l = new Label(val);
        l.setTextFill(Color.web("#38bdf8"));
        l.setFont(Font.font("System Bold", 18));
        l.setMinWidth(40);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private StackPane createCell(int val) {
        StackPane cell = new StackPane();
        cell.setPrefSize(45, 45);
        Rectangle r = new Rectangle(43, 43);
        r.setFill(Color.web("#1e293b"));
        r.setStroke(Color.web("#334155"));
        r.setArcHeight(5); r.setArcWidth(5);
        Label l = new Label(val == 0 ? "0" : String.valueOf(val));
        l.setTextFill(Color.WHITE);
        cell.getChildren().addAll(r, l);
        return cell;
    }

    @FXML
    private void stepVisualization() {
        if (strA.isEmpty()) {
            strA = inputA.getText().toUpperCase();
            strB = inputB.getText().toUpperCase();
            if (strA.isEmpty()) return;
            resetVisualization();
            initializeGrid();
        }
        if (currI <= strA.length()) {
            processNextCell();
        } else {
            startBacktracking();
        }
    }

    private void processNextCell() {
        int i = currI;
        int j = currJ;
        StackPane cell = cells[i + 1][j + 1];
        Rectangle rect = (Rectangle) cell.getChildren().get(0);
        Label lbl = (Label) cell.getChildren().get(1);

        highlightPseudoCode(5);
        
        if (strA.charAt(i - 1) == strB.charAt(j - 1)) {
            // Match
            highlightPseudoCode(6);
            dp[i][j] = dp[i - 1][j - 1] + 1;
            rect.setFill(Color.web("#065f46"));
            lbl.setText(String.valueOf(dp[i][j]));
            drawDiagonalArrow(i + 1, j + 1);
            
            Tooltip.install(cell, new Tooltip("Match: " + strA.charAt(i-1) + " == " + strB.charAt(j-1) + "\nValue = dp[i-1][j-1] + 1 = " + dp[i][j]));
        } else {
            // Mismatch
            highlightPseudoCode(8);
            dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            lbl.setText(String.valueOf(dp[i][j]));
            pulseNeighbors(i, j);
            
            Tooltip.install(cell, new Tooltip("Mismatch: " + strA.charAt(i-1) + " != " + strB.charAt(j-1) + "\nValue = max(top, left) = " + dp[i][j]));
        }

        // Advance indices
        currJ++;
        if (currJ > strB.length()) {
            currJ = 1;
            currI++;
        }
    }

    private void drawDiagonalArrow(int gridI, int gridJ) {
        // Logic to draw arrow in pathOverlay
        // (Simplified for now - just UI highlighting)
    }

    private void pulseNeighbors(int i, int j) {
        StackPane top = cells[i][j + 1];
        StackPane left = cells[i + 1][j];
        
        ScaleTransition st = new ScaleTransition(Duration.millis(300 / animationSpeed));
        if (dp[i - 1][j] >= dp[i][j - 1]) {
            st.setNode(top);
        } else {
            st.setNode(left);
        }
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.2); st.setToY(1.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private void startBacktracking() {
        highlightPseudoCode(11);
        int i = strA.length();
        int j = strB.length();
        StringBuilder lcs = new StringBuilder();
        
        SequentialTransition seq = new SequentialTransition();
        
        while (i > 0 && j > 0) {
            StackPane cell = cells[i + 1][j + 1];
            Rectangle rect = (Rectangle) cell.getChildren().get(0);
            
            final int fi = i, fj = j;
            PauseTransition p = new PauseTransition(Duration.millis(500 / animationSpeed));
            p.setOnFinished(e -> {
                rect.setStroke(Color.web("#fbbf24"));
                rect.setStrokeWidth(3);
            });
            seq.getChildren().add(p);

            if (strA.charAt(i - 1) == strB.charAt(j - 1)) {
                lcs.insert(0, strA.charAt(i - 1));
                i--; j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        
        seq.setOnFinished(e -> {
            resultLabel.setText(lcs.toString());
            isAnimating = false;
        });
        seq.play();
    }

    private void autoStep() {
        if (!isAnimating) return;
        if (currI <= strA.length()) {
            processNextCell();
            Timeline next = new Timeline(new KeyFrame(Duration.millis(1000 / animationSpeed), e -> autoStep()));
            next.play();
        } else {
            startBacktracking();
        }
    }

    @FXML
    private void resetVisualization() {
        currI = 1; currJ = 1;
        resultLabel.setText("-");
        pathOverlay.getChildren().clear();
        highlightPseudoCode(-1);
    }

    @FXML
    private void backToCategory() {
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
