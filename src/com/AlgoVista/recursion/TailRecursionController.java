package com.AlgoVista.recursion;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.AlgoVista.utils.ShortcutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TailRecursionController {
    @FXML
    private VBox codeContainer;
    @FXML
    private VBox stackContainer;
    @FXML
    private TextArea consoleArea;
    @FXML
    private Button stepBtn;
    @FXML
    private Button autoPlayBtn;
    @FXML
    private Slider speedSlider;
    @FXML
    private Label speedLabel;

    private boolean isPlaying = false;
    private int currentStepIndex = 0;
    private List<Label> codeLines = new ArrayList<>();

    // Hardcoded Python code for Tail Recursion
    private final String[] sourceCode = {
            "def factorial_tail(n, acc=1):",
            "    if n == 0 or n == 1:",
            "        return acc",
            "    return factorial_tail(n - 1, n * acc)",
            "",
            "print(factorial_tail(5))"
    };

    private static class StackFrame {
        String callStr;
        boolean returning;
        String returnVal;

        StackFrame(String callStr, boolean returning, String returnVal) {
            this.callStr = callStr;
            this.returning = returning;
            this.returnVal = returnVal;
        }
    }

    private static class SimStep {
        int lineIndex;
        List<StackFrame> stackFrames;
        String consoleText;

        SimStep(int lineIndex, List<StackFrame> stackFrames, String consoleText) {
            this.lineIndex = lineIndex;
            this.stackFrames = new ArrayList<>(stackFrames);
            this.consoleText = consoleText;
        }
    }

    private List<SimStep> steps = new ArrayList<>();

    @FXML
    public void initialize() {
        if (speedSlider != null) {
            double initSpeed = com.AlgoVista.utils.SettingsManager.getSpeed();
            speedSlider.setValue(initSpeed);
            if (speedLabel != null) { speedLabel.setText(String.format("%.2fx", initSpeed)); }
            
        }
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            
            if (speedLabel != null) {
                speedLabel.setText(String.format("%.2fx", newVal.doubleValue()));
            }
        });
        for (int i = 0; i < sourceCode.length; i++) {
            Label lineLabel = new Label(sourceCode[i]);
            lineLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-padding: 2 10; -fx-background-radius: 4;");
            lineLabel.setMaxWidth(Double.MAX_VALUE);
            codeLines.add(lineLabel);
            codeContainer.getChildren().add(lineLabel);
        }

        buildSimulationSteps();
        renderStep(0);

        stackContainer.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ShortcutManager.register(newVal,
                    this::toggleAutoPlay,
                    this::stepForward,
                    this::resetSimulation,
                    this::backToCategory
                );
            }
        });
    }

    private void buildSimulationSteps() {
        steps.clear();
        String c = "";
        List<StackFrame> currentStack = new ArrayList<>();

        // Step 0: start
        steps.add(new SimStep(5, currentStack, c));

        // Tail optimization means we ONLY EVER HAVE ONE FRAME on the stack
        int n = 5;
        int acc = 1;

        while (true) {
            // Line: enter function (replaces previous frame)
            currentStack.clear();
            currentStack.add(new StackFrame("factorial_tail(" + n + ", " + acc + ")", false, ""));
            steps.add(new SimStep(0, currentStack, c));

            // Line: if n == 0 or n == 1
            steps.add(new SimStep(1, currentStack, c));

            if (n == 0 || n == 1) {
                // Base case: return acc
                currentStack.clear();
                currentStack.add(new StackFrame("factorial_tail(" + n + ", " + acc + ")", true, String.valueOf(acc)));
                steps.add(new SimStep(2, currentStack, c));
                break;
            } else {
                // Recursive step: return factorial_tail(n - 1, n * acc)
                steps.add(new SimStep(3, currentStack, c));
                acc = n * acc;
                n = n - 1;
            }
        }

        // Return to caller
        currentStack.clear();
        steps.add(new SimStep(5, currentStack, c));

        // Print result
        c += "120\n";
        steps.add(new SimStep(5, currentStack, c));
    }

    private void renderStep(int index) {
        if (index < 0 || index >= steps.size())
            return;
        SimStep step = steps.get(index);

        // Highlight code
        for (int i = 0; i < codeLines.size(); i++) {
            if (i == step.lineIndex) {
                codeLines.get(i).setStyle(
                        "-fx-text-fill: white; -fx-background-color: #3b82f6; -fx-padding: 2 10; -fx-background-radius: 4; -fx-font-weight: bold;");
            } else {
                codeLines.get(i).setStyle(
                        "-fx-text-fill: #cbd5e1; -fx-background-color: transparent; -fx-padding: 2 10; -fx-background-radius: 4;");
            }
        }

        // Render stack
        stackContainer.getChildren().clear();
        for (int i = step.stackFrames.size() - 1; i >= 0; i--) {
            StackFrame frameData = step.stackFrames.get(i);

            HBox frameBox = new HBox();
            frameBox.setAlignment(Pos.CENTER);
            frameBox.setMaxWidth(Double.MAX_VALUE);

            if (frameData.returning) {
                // Returning state
                frameBox.setStyle(
                        "-fx-background-color: #059669; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #10b981; -fx-border-width: 2; -fx-border-radius: 5;");

                Label nameLabel = new Label(frameData.callStr);
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                Label returnLabel = new Label(" -> returns " + frameData.returnVal);
                returnLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;");

                frameBox.getChildren().addAll(nameLabel, returnLabel);
            } else {
                // Normal state - specifically colored to represent it's optimized
                frameBox.setStyle(
                        "-fx-background-color: #0d9488; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #14b8a6; -fx-border-width: 2; -fx-border-radius: 5;");

                Label nameLabel = new Label(frameData.callStr);
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                frameBox.getChildren().add(nameLabel);
            }

            stackContainer.getChildren().add(frameBox);
        }

        // Console
        consoleArea.setText(step.consoleText);

        // Update buttons
        stepBtn.setDisable(index == steps.size() - 1);
        if (index == steps.size() - 1) {
            isPlaying = false;
            autoPlayBtn.setText("Auto Play");
        }
    }

    @FXML
    private void stepForward() {
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            renderStep(currentStepIndex);
        }
    }

    @FXML
    private void resetSimulation() {
        isPlaying = false;
        autoPlayBtn.setText("Auto Play");
        currentStepIndex = 0;
        renderStep(currentStepIndex);
    }

    @FXML
    private void toggleAutoPlay() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            autoPlayBtn.setText("Pause");
            new Thread(() -> {
                while (isPlaying && currentStepIndex < steps.size() - 1) {
                    try {
                        responsiveSleep();
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (isPlaying) {
                        Platform.runLater(() -> {
                            currentStepIndex++;
                            renderStep(currentStepIndex);
                        });
                    }
                }
            }).start();
        } else {
            autoPlayBtn.setText("Auto Play");
        }
    }

    /** Reads local slider first; falls back to global. Does NOT write to SettingsManager. */
    private long getDelay() {
        double speed = (speedSlider != null) ? speedSlider.getValue()
                                             : com.AlgoVista.utils.SettingsManager.getSpeed();
        return (long)(800.0 / Math.max(speed, 0.01));
    }

    /** Sleeps in 50ms chunks, re-reading delay each chunk for live responsiveness. */
    private void responsiveSleep() throws InterruptedException {
        long target = getDelay();
        long elapsed = 0;
        final long chunk = 50;
        while (elapsed < target && isPlaying) {
            Thread.sleep(Math.min(chunk, target - elapsed));
            elapsed += chunk;
            target = getDelay();
        }
    }

    @FXML
    private void backToCategory() {
        isPlaying = false;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RecursionCategory.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) stackContainer.getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            double x = stage.getX();
            double y = stage.getY();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setX(x);
            stage.setY(y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
