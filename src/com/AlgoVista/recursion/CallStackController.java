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
import java.util.Arrays;
import java.util.List;

public class CallStackController {
    @FXML
    private VBox codeContainer;
    @FXML
    private VBox stackContainer;
    @FXML
    private TextArea consoleArea;
    @FXML
    private Button stepBtn;
    @FXML private Button autoPlayBtn;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;

    private boolean isPlaying = false;
    private int currentStepIndex = 0;
    private List<Label> codeLines = new ArrayList<>();

    // Hardcoded Python code
    private final String[] sourceCode = {
            "def greet(name):",
            "    print(\"hello, \" + name + \"!\")",
            "    greet2(name)",
            "    print(\"getting ready to say bye...\")",
            "    bye()",
            "",
            "def greet2(name):",
            "    print(\"how are you, \" + name + \"?\")",
            "",
            "def bye():",
            "    print(\"ok bye!\")",
            "",
            "greet(\"maggie\")"
    };

    // Define simulation steps
    private static class SimStep {
        int lineIndex;
        List<String> stackFrames; // bottom to top
        String consoleText;

        SimStep(int lineIndex, List<String> stackFrames, String consoleText) {
            this.lineIndex = lineIndex;
            this.stackFrames = stackFrames;
            this.consoleText = consoleText;
        }
    }

    private List<SimStep> steps = new ArrayList<>();

    @FXML
    public void initialize() {
        if (speedSlider != null) {
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (speedLabel != null) {
                    speedLabel.setText(String.format("%.1fx", newVal.doubleValue()));
                }
            });
        }
        // Render source code
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
        // Step 0: start
        steps.add(new SimStep(12, Arrays.asList(), ""));

        // Step 1: calling greet
        steps.add(new SimStep(12, Arrays.asList("greet(\"maggie\")"), ""));

        // Step 2: enter greet
        steps.add(new SimStep(0, Arrays.asList("greet(name=\"maggie\")"), ""));

        // Step 3: print 1
        steps.add(new SimStep(1, Arrays.asList("greet(name=\"maggie\")"), ""));
        c += "hello, maggie!\n";
        steps.add(new SimStep(1, Arrays.asList("greet(name=\"maggie\")"), c));

        // Step 4: call greet2
        steps.add(new SimStep(2, Arrays.asList("greet(name=\"maggie\")"), c));
        steps.add(new SimStep(2, Arrays.asList("greet(name=\"maggie\")", "greet2(\"maggie\")"), c));

        // Step 5: enter greet2
        steps.add(new SimStep(6, Arrays.asList("greet(name=\"maggie\")", "greet2(name=\"maggie\")"), c));

        // Step 6: print 2
        steps.add(new SimStep(7, Arrays.asList("greet(name=\"maggie\")", "greet2(name=\"maggie\")"), c));
        c += "how are you, maggie?\n";
        steps.add(new SimStep(7, Arrays.asList("greet(name=\"maggie\")", "greet2(name=\"maggie\")"), c));

        // Step 7: return from greet2
        steps.add(new SimStep(7, Arrays.asList("greet(name=\"maggie\")"), c));

        // Step 8: print 3
        steps.add(new SimStep(3, Arrays.asList("greet(name=\"maggie\")"), c));
        c += "getting ready to say bye...\n";
        steps.add(new SimStep(3, Arrays.asList("greet(name=\"maggie\")"), c));

        // Step 9: call bye
        steps.add(new SimStep(4, Arrays.asList("greet(name=\"maggie\")"), c));
        steps.add(new SimStep(4, Arrays.asList("greet(name=\"maggie\")", "bye()"), c));

        // Step 10: enter bye
        steps.add(new SimStep(9, Arrays.asList("greet(name=\"maggie\")", "bye()"), c));

        // Step 11: print ok bye
        steps.add(new SimStep(10, Arrays.asList("greet(name=\"maggie\")", "bye()"), c));
        c += "ok bye!\n";
        steps.add(new SimStep(10, Arrays.asList("greet(name=\"maggie\")", "bye()"), c));

        // Step 12: return from bye
        steps.add(new SimStep(10, Arrays.asList("greet(name=\"maggie\")"), c));

        // Step 13: return from greet
        steps.add(new SimStep(4, Arrays.asList(), c));
        steps.add(new SimStep(12, Arrays.asList(), c)); // Finished
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

        // Render stack (bottom to top, so we reverse iteration or use VBox alignment
        // effectively)
        stackContainer.getChildren().clear();
        // Since VBox puts first item at top, to have bottom-to-top we iterate backwards
        for (int i = step.stackFrames.size() - 1; i >= 0; i--) {
            String frameName = step.stackFrames.get(i);
            Label frame = new Label(frameName);
            frame.setMaxWidth(Double.MAX_VALUE);
            frame.setAlignment(Pos.CENTER);
            frame.setStyle(
                    "-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-padding: 10; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #a78bfa; -fx-border-width: 2; -fx-border-radius: 5;");
            stackContainer.getChildren().add(frame);
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
                            delay = (long) (800 / speedSlider.getValue());
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
