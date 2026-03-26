package com.AlgoVista.dp;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FibonacciController {
    @FXML private HBox rootPane;
    @FXML private HBox cellContainer;
    @FXML private Pane animationPane;
    @FXML private TextField inputN;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;
    @FXML private ToggleButton spaceToggle;
    @FXML private TextFlow pseudoCodeFlow;

    private int targetN = 0;
    private int currentIndex = 1;
    private List<Long> fibValues = new ArrayList<>();
    private List<StackPane> cellNodes = new ArrayList<>();
    private boolean isConstantSpace = false;
    private double animationSpeed = 1.0;

    private final String[] pseudoCodeNormal = {
        "long fib(int n) {",
        "  if (n <= 1) return n;",
        "  long[] dp = new long[n + 1];",
        "  dp[0] = 0; dp[1] = 1;",
        "  for (int i = 2; i <= n; i++) {",
        "    dp[i] = dp[i-1] + dp[i-2];",
        "  }",
        "  return dp[n];",
        "}"
    };

    private final String[] pseudoCodeOptimized = {
        "long fibonacci(long n) {",
        "  if (n <= 1) return n;",
        "  long prev = 0;",
        "  long curr = 1;",
        "  for (long i = 2; i <= n; i++) {",
        "    long next = prev + curr;",
        "    prev = curr;",
        "    curr = next;",
        "  }",
        "  return curr;",
        "}"
    };

    @FXML
    public void initialize() {
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationSpeed = newVal.doubleValue();
            speedLabel.setText(String.format("%.1fx", animationSpeed));
        });
        setupPseudoCode();
        resetVisualization();
    }

    private void setupPseudoCode() {
        pseudoCodeFlow.getChildren().clear();
        String[] currentCode = isConstantSpace ? pseudoCodeOptimized : pseudoCodeNormal;
        for (String line : currentCode) {
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
                text.setFill(Color.web("#38bdf8"));
                text.setStyle("-fx-font-weight: bold;");
            } else {
                text.setFill(Color.web("#94a3b8"));
                text.setStyle("-fx-font-weight: normal;");
            }
        }
    }

    @FXML
    private void startVisualization() {
        try {
            targetN = Integer.parseInt(inputN.getText());
            if (targetN < 0) return;
            resetVisualization();
            runFullAnimation();
        } catch (NumberFormatException e) {
            // Show alert or ignore
        }
    }

    @FXML
    private void stepVisualization() {
        if (fibValues.isEmpty()) {
            try {
                targetN = Integer.parseInt(inputN.getText());
                if (targetN < 0) return;
            } catch (NumberFormatException e) {
                return;
            }
            // First step: F(0)
            highlightPseudoCode(3);
            addCell(0, 0);
            fibValues.add(0L);
            return;
        }

        if (fibValues.size() == 1) {
            // Second step: F(1)
            highlightPseudoCode(3);
            addCell(1, 1);
            fibValues.add(1L);
            currentIndex = 2;
            return;
        }

        if (currentIndex <= targetN) {
            calculateNextStep();
        }
    }

    private void calculateNextStep() {
        highlightPseudoCode(5);
        long nextVal = fibValues.get(currentIndex - 1) + fibValues.get(currentIndex - 2);
        
        StackPane cell1 = cellNodes.get(cellNodes.size() - 2);
        StackPane cell2 = cellNodes.get(cellNodes.size() - 1);
        
        drawGlowingArcs(cell1, cell2, nextVal);
    }

    private void drawGlowingArcs(StackPane start1, StackPane start2, long result) {
        // Create end position (where the new cell will be)
        double endX = cellContainer.getChildren().isEmpty() ? 50 : 
                      cellContainer.getBoundsInParent().getMaxX() + 15; // spacing
        
        // Actually, we'll add the cell first but make it invisible/ghosted
        StackPane newCell = createCell(currentIndex, result);
        newCell.setOpacity(0);
        cellContainer.getChildren().add(newCell);
        
        // Arc 1 from F(i-2)
        QuadCurve arc1 = createArc(start1, newCell, Color.web("#38bdf8"));
        // Arc 2 from F(i-1)
        QuadCurve arc2 = createArc(start2, newCell, Color.web("#38bdf8"));

        animationPane.getChildren().addAll(arc1, arc2);

        // Animate the "glowing" path
        animatePath(arc1, arc2, () -> {
            newCell.setOpacity(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(300 / animationSpeed), newCell);
            st.setFromX(0.5); st.setFromY(0.5);
            st.setToX(1.0); st.setToY(1.0);
            st.setOnFinished(e -> {
                animationPane.getChildren().removeAll(arc1, arc2);
                fibValues.add(result);
                cellNodes.add(newCell);

                // Highlight final result
                if (currentIndex == targetN) {
                    highlightFinalResult(newCell);
                }

                currentIndex++;
                
                if (isConstantSpace && cellNodes.size() > 2) {
                    shiftCells();
                }
            });
            st.play();
        });
    }

    private void highlightFinalResult(StackPane cell) {
        Rectangle rect = (Rectangle) cell.getChildren().get(0);
        rect.setStroke(Color.web("#fbbf24")); // Gold border
        rect.setStrokeWidth(4);
        
        FillTransition ft = new FillTransition(Duration.millis(500), rect, Color.web("#1e293b"), Color.web("#064e3b"));
        ScaleTransition st = new ScaleTransition(Duration.millis(500), cell);
        st.setToX(1.3); st.setToY(1.3);
        
        ParallelTransition pt = new ParallelTransition(ft, st);
        pt.play();
    }

    private QuadCurve createArc(StackPane startNode, StackPane endNode, Color color) {
        // Arcs are calculated relative to the animationPane
        // "Slight right" adjustment: move start and end points slightly right to center them better
        double offsetRight = 100.0; 
        
        double sX = startNode.localToScene(startNode.getWidth() / 2, 0).getX() - animationPane.localToScene(0, 0).getX() + offsetRight;
        double sY = startNode.localToScene(startNode.getWidth() / 2, 0).getY() - animationPane.localToScene(0, 0).getY();
        double eX = endNode.localToScene(endNode.getWidth() / 2, 0).getX() - animationPane.localToScene(0, 0).getX() + offsetRight;
        double eY = endNode.localToScene(endNode.getWidth() / 2, 0).getY() - animationPane.localToScene(0, 0).getY();

        QuadCurve arc = new QuadCurve(sX, sY, (sX + eX) / 2, sY - 80, eX, eY);
        arc.setStroke(color);
        arc.setFill(null);
        arc.setStrokeWidth(3);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(20);
        arc.setEffect(glow);
        
        return arc;
    }

    private void animatePath(QuadCurve arc1, QuadCurve arc2, Runnable onFinished) {
        // We'll use a simple reveal animation for the arcs
        double length1 = arc1.getBoundsInLocal().getWidth() * 2;
        arc1.getStrokeDashArray().setAll(length1);
        arc1.setStrokeDashOffset(length1);
        
        double length2 = arc2.getBoundsInLocal().getWidth() * 2;
        arc2.getStrokeDashArray().setAll(length2);
        arc2.setStrokeDashOffset(length2);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(arc1.strokeDashOffsetProperty(), length1),
                new KeyValue(arc2.strokeDashOffsetProperty(), length2)
            ),
            new KeyFrame(Duration.millis(800 / animationSpeed),
                new KeyValue(arc1.strokeDashOffsetProperty(), 0),
                new KeyValue(arc2.strokeDashOffsetProperty(), 0)
            )
        );
        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
    }

    private void addCell(int index, long value) {
        StackPane cell = createCell(index, value);
        cellContainer.getChildren().add(cell);
        cellNodes.add(cell);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(500 / animationSpeed), cell);
        tt.setFromX(200);
        tt.setToX(0);
        tt.play();
    }

    private StackPane createCell(int index, long value) {
        StackPane cell = new StackPane();
        cell.setPrefSize(70, 70);
        
        Rectangle rect = new Rectangle(70, 70);
        rect.setArcHeight(15); rect.setArcWidth(15);
        rect.setFill(Color.web("#1e293b"));
        rect.setStroke(Color.web("#334155"));
        rect.setStrokeWidth(2);
        
        VBox content = new VBox(2);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label idxLabel = new Label("F(" + index + ")");
        idxLabel.setTextFill(Color.web("#64748b"));
        idxLabel.setFont(Font.font(10));
        
        Label valLabel = new Label(String.valueOf(value));
        valLabel.setTextFill(Color.web("#f8fafc"));
        valLabel.setFont(Font.font("System Bold", 16));
        
        content.getChildren().addAll(idxLabel, valLabel);
        cell.getChildren().addAll(rect, content);

        Tooltip tip = new Tooltip("Fibonacci " + index + " = " + value);
        if (index >= 2) {
            tip.setText("Computed because F(" + index + ") = F(" + (index-1) + ") + F(" + (index-2) + ")\n" +
                        "Which is " + fibValues.get(index-1) + " + " + fibValues.get(index-2));
        }
        Tooltip.install(cell, tip);
        
        return cell;
    }

    private void shiftCells() {
        // Shift values between the two visible cells
        StackPane first = (StackPane) cellContainer.getChildren().remove(0);
        cellNodes.remove(first);
        
        // Animate the shift
        Duration d = Duration.millis(400 / animationSpeed);
        for (int i = 0; i < cellContainer.getChildren().size(); i++) {
             StackPane node = (StackPane) cellContainer.getChildren().get(i);
             TranslateTransition tt = new TranslateTransition(d, node);
             tt.setFromX(85); // width + spacing
             tt.setToX(0);
             tt.play();
        }
    }

    @FXML
    private void resetVisualization() {
        cellContainer.getChildren().clear();
        animationPane.getChildren().clear();
        fibValues.clear();
        cellNodes.clear();
        currentIndex = 0;
        highlightPseudoCode(-1);
    }

    @FXML
    private void toggleSpaceOptimization() {
        isConstantSpace = !isConstantSpace;
        spaceToggle.setText("Constant Space Mode: " + (isConstantSpace ? "ON" : "OFF"));
        setupPseudoCode(); // Refresh pseudo-code text
        resetVisualization();
    }

    private void runFullAnimation() {
        Timeline fullAnim = new Timeline();
        // This is a bit tricky to implement fully in one Timeline due to dynamic path creation
        // but we can use a loop or a recursive pulse.
        // For now, I'll implement a simple recursive caller.
        autoStep();
    }
    
    private void autoStep() {
        if (currentIndex <= targetN) {
            stepVisualization();
            Timeline nextCall = new Timeline(new KeyFrame(Duration.millis(1500 / animationSpeed), e -> autoStep()));
            nextCall.play();
        }
    }

    @FXML
    private void backToCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DP_Category.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
