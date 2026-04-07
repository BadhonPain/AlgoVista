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

public class RecursionStackController {
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

    // Hardcoded Python code
    private final String[] sourceCode = {
            "def factorial(n):",
            "    if n == 1:",
            "        return 1",
            "    return n * factorial(n - 1)",
            "",
            "print(factorial(5))"
    };

    // Frame object to represent stack state including return values
    private static class StackFrame {
        String callStr;
        boolean returning;
        String returnVal;

        StackFrame(String callStr, boolean returning, String returnVal) {
            this.callStr = callStr;
            this.returning = returning; // true when evaluating return value and popping back
            this.returnVal = returnVal;
        }
    }

    private static class SimStep {
        int lineIndex;
        List<StackFrame> stackFrames; // bottom to top
        String consoleText;

        SimStep(int lineIndex, List<StackFrame> stackFrames, String consoleText) {
            this.lineIndex = lineIndex;
            this.stackFrames = new ArrayList<>(stackFrames); // Create copy for state snapshot
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

        // Pushing factorial(5) to factorial(1)
        int maxN = 5;
        for (int n = maxN; n >= 1; n--) {
            // Line: enter function
            currentStack.add(new StackFrame("factorial(" + n + ")", false, ""));
            steps.add(new SimStep(0, currentStack, c));

            // Line: if n == 1
            steps.add(new SimStep(1, currentStack, c));

            if (n == 1) {
                // Line: return 1
                currentStack.set(currentStack.size() - 1, new StackFrame("factorial(1)", true, "1"));
                steps.add(new SimStep(2, currentStack, c));
            } else {
                // Line: return n * factorial(n-1)
                steps.add(new SimStep(3, currentStack, c));
            }
        }

        // Popping back up
        int currentVal = 1;
        for (int n = 1; n <= maxN; n++) {
            if (n == 1) {
                // Already did return 1 line
                currentStack.remove(currentStack.size() - 1);
            } else {
                currentVal = currentVal * n;
                // We're back at `return n * factorial(n - 1)`
                // Show returning state for this frame with new calculated value
                currentStack.set(currentStack.size() - 1,
                        new StackFrame("factorial(" + n + ")", true, String.valueOf(currentVal)));
                steps.add(new SimStep(3, currentStack, c));
                // Pop the frame
                currentStack.remove(currentStack.size() - 1);
            }
        }

        // Print the result on line 5
        steps.add(new SimStep(5, currentStack, c));
        c += "120\n";
        steps.add(new SimStep(5, currentStack, c)); // Finished
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

        // Render stack (bottom to top, visually)
        stackContainer.getChildren().clear();
        for (int i = step.stackFrames.size() - 1; i >= 0; i--) {
            StackFrame frameData = step.stackFrames.get(i);

            HBox frameBox = new HBox();
            frameBox.setAlignment(Pos.CENTER);
            frameBox.setMaxWidth(Double.MAX_VALUE);

            if (frameData.returning) {
                // Returning state: highlight specifically (e.g., green tint) and show return
                // value
                frameBox.setStyle(
                        "-fx-background-color: #059669; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #10b981; -fx-border-width: 2; -fx-border-radius: 5;");

                Label nameLabel = new Label(frameData.callStr);
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                Label returnLabel = new Label(" -> returns " + frameData.returnVal);
                returnLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;"); // Amber color for return

                frameBox.getChildren().addAll(nameLabel, returnLabel);
            } else {
                // Normal state
                frameBox.setStyle(
                        "-fx-background-color: #8b5cf6; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #a78bfa; -fx-border-width: 2; -fx-border-radius: 5;");

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
                        long delay = 800;
                        if (speedSlider != null) {
                            delay = (long)((800) * com.AlgoVista.utils.SettingsManager.getSleepMultiplier());
                        }
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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

    @FXML
    private void backToCategory() {
        isPlaying = false; // Stop auto-play thread if running
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
