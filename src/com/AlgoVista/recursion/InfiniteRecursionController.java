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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfiniteRecursionController {
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

    private boolean isPlaying = false;
    private int currentStepIndex = 0;
    private List<Label> codeLines = new ArrayList<>();

    // Hardcoded Python code
    private final String[] sourceCode = {
            "def print_natural(n=1):",
            "    print(n)",
            "    print_natural(n + 1)",
            "",
            "print_natural()"
    };

    private static class SimStep {
        int lineIndex;
        List<String> stackFrames; // bottom to top
        String consoleText;
        boolean isOverflow;

        SimStep(int lineIndex, List<String> stackFrames, String consoleText, boolean isOverflow) {
            this.lineIndex = lineIndex;
            this.stackFrames = new ArrayList<>(stackFrames);
            this.consoleText = consoleText;
            this.isOverflow = isOverflow;
        }
    }

    private List<SimStep> steps = new ArrayList<>();

    @FXML
    public void initialize() {
        for (int i = 0; i < sourceCode.length; i++) {
            Label lineLabel = new Label(sourceCode[i]);
            lineLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-padding: 2 10; -fx-background-radius: 4;");
            lineLabel.setMaxWidth(Double.MAX_VALUE);
            codeLines.add(lineLabel);
            codeContainer.getChildren().add(lineLabel);
        }

        buildSimulationSteps();
        renderStep(0);
    }

    private void buildSimulationSteps() {
        steps.clear();
        StringBuilder c = new StringBuilder();
        List<String> currentStack = new ArrayList<>();

        // Define max stack depth before we simulate an overflow
        int maxDepth = 12;

        // Step 0: start
        steps.add(new SimStep(4, currentStack, c.toString(), false));

        for (int n = 1; n <= maxDepth; n++) {
            // Line: enter function
            currentStack.add("print_natural(" + n + ")");
            steps.add(new SimStep(0, currentStack, c.toString(), false));

            // Line: print(n)
            steps.add(new SimStep(1, currentStack, c.toString(), false));
            c.append(n).append("\n"); // Console output
            steps.add(new SimStep(1, currentStack, c.toString(), false));

            if (n == maxDepth) {
                // Line: print_natural(n + 1) -> triggers overflow!
                steps.add(new SimStep(2, currentStack, c.toString(), false));
                currentStack.add("print_natural(" + (n + 1) + ")");
                c.append("\nRecursionError: maximum recursion depth exceeded!\n");

                // Final overflow step
                steps.add(new SimStep(2, currentStack, c.toString(), true));
                break;
            } else {
                // Line: print_natural(n + 1) goes to next iteration
                steps.add(new SimStep(2, currentStack, c.toString(), false));
            }
        }
    }

    private void renderStep(int index) {
        if (index < 0 || index >= steps.size())
            return;
        SimStep step = steps.get(index);

        // Highlight code
        for (int i = 0; i < codeLines.size(); i++) {
            if (step.isOverflow && i == step.lineIndex) {
                codeLines.get(i).setStyle(
                        "-fx-text-fill: white; -fx-background-color: #ef4444; -fx-padding: 2 10; -fx-background-radius: 4; -fx-font-weight: bold;"); // Red
                                                                                                                                                     // background
                                                                                                                                                     // for
                                                                                                                                                     // error
            } else if (i == step.lineIndex) {
                codeLines.get(i).setStyle(
                        "-fx-text-fill: white; -fx-background-color: #3b82f6; -fx-padding: 2 10; -fx-background-radius: 4; -fx-font-weight: bold;");
            } else {
                codeLines.get(i).setStyle(
                        "-fx-text-fill: #cbd5e1; -fx-background-color: transparent; -fx-padding: 2 10; -fx-background-radius: 4;");
            }
        }

        // Render stack (bottom to top visually)
        stackContainer.getChildren().clear();
        for (int i = step.stackFrames.size() - 1; i >= 0; i--) {
            String frameName = step.stackFrames.get(i);

            Label frame = new Label(frameName);
            frame.setMaxWidth(Double.MAX_VALUE);
            frame.setAlignment(Pos.CENTER);

            // If overflow, color the entire stack or heavily glitch the top
            if (step.isOverflow) {
                frame.setStyle(
                        "-fx-background-color: #991b1b; -fx-text-fill: #fca5a5; -fx-padding: 8; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #ef4444; -fx-border-width: 2; -fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.8), 10, 0, 0, 0);");
            } else {
                frame.setStyle(
                        "-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-padding: 10; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #a78bfa; -fx-border-width: 2; -fx-border-radius: 5;");
            }

            stackContainer.getChildren().add(frame);
        }

        // Console
        consoleArea.setText(step.consoleText);
        // Scroll to bottom of text area
        consoleArea.selectPositionCaret(consoleArea.getLength());
        consoleArea.deselect();

        if (step.isOverflow) {
            consoleArea.setStyle(
                    "-fx-control-inner-background: #450a0a; -fx-text-fill: #fca5a5; -fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        } else {
            consoleArea.setStyle(
                    "-fx-control-inner-background: #0f172a; -fx-text-fill: #10b981; -fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        }

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
                        long delay = (long) speedSlider.getValue();
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
            double width = stage.getWidth();
            double height = stage.getHeight();
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
