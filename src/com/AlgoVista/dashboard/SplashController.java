package com.AlgoVista.dashboard;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashController {

    @FXML private StackPane rootPane;
    @FXML private Pane gridPane;
    @FXML private Pane particlePane;
    @FXML private StackPane logoContainer;
    @FXML private Group hexagonFrame;
    @FXML private Group logoGraph;
    @FXML private Canvas binaryCanvas;
    @FXML private Circle bloomCircle;
    
    @FXML private Line seg1, seg2, seg3, seg4, seg5, seg6;
    
    @FXML private Label titleLabel;
    @FXML private Label taglineLabel;
    @FXML private VBox progressArea;
    @FXML private Rectangle progressFill;
    @FXML private Circle dataPulse;
    @FXML private Label loadingLabel;

    private final Random random = new Random();
    private AnimationTimer binaryRainTimer;
    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        playSplashSound();
        setupBackgroundGrid();
        createParticles();
        setupDataPulse();
        playCinematicSequence();
    }

    private void playSplashSound() {
        try {
            URL resource = getClass().getResource("/com/AlgoVista/sounds/helicopter.mp3");
            if (resource != null) {
                Media media = new Media(resource.toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnError(() -> {
                    // Silently ignore audio errors in production
                });
                mediaPlayer.setVolume(1.0);  // setting full volume
                mediaPlayer.play();
            }
        } catch (Exception e) {
            // Silently ignore audio errors
        }
    }

    private void setupBackgroundGrid() {
        for (int i = 0; i < 40; i++) {
            Circle point = new Circle(random.nextDouble() * 900, random.nextDouble() * 700, 1.2, Color.web("#38bdf8", 0.15));
            gridPane.getChildren().add(point);
            animateGridPoint(point);
        }
    }

    private void animateGridPoint(Circle point) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(10 + random.nextDouble() * 10), point);
        tt.setByX(random.nextDouble() * 40 - 20);
        tt.setByY(random.nextDouble() * 40 - 20);
        tt.setAutoReverse(true);
        tt.setCycleCount(Timeline.INDEFINITE);
        tt.play();
    }

    private void createParticles() {
        for (int i = 0; i < 15; i++) {
            Circle p = new Circle(random.nextDouble() * 900, random.nextDouble() * 700, 1 + random.nextDouble() * 2, Color.web("#818cf8", 0.1));
            particlePane.getChildren().add(p);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(5 + random.nextDouble() * 5), p);
            tt.setByY(-100 - random.nextDouble() * 100);
            tt.setCycleCount(Timeline.INDEFINITE);
            tt.play();
        }
    }

    private void setupDataPulse() {
        progressFill.widthProperty().addListener((obs, oldVal, newVal) -> {
            dataPulse.setTranslateX(newVal.doubleValue());
            if (newVal.doubleValue() > 0) dataPulse.setOpacity(1.0);
        });
    }

    private void playCinematicSequence() {
        // hexagone logo creation
        ParallelTransition hexAssembly = new ParallelTransition();
        Line[] segs = {seg1, seg2, seg3, seg4, seg5, seg6};
        double[][] offsets = {{0, -100}, {100, -50}, {100, 50}, {0, 100}, {-100, 50}, {-100, -50}};

        for (int i = 0; i < segs.length; i++) {
            segs[i].setTranslateX(offsets[i][0]);
            segs[i].setTranslateY(offsets[i][1]);
            
            TranslateTransition tt = new TranslateTransition(Duration.seconds(1.2), segs[i]);
            tt.setToX(0);
            tt.setToY(0);
            tt.setInterpolator(Interpolator.EASE_OUT);
            
            FadeTransition ft = new FadeTransition(Duration.seconds(0.8), segs[i]);
            ft.setToValue(1.0);
            
            hexAssembly.getChildren().addAll(tt, ft);
        }

        // the rain like effect with binary numbers
        startBinaryRain();

        // graph like bloom in logo
        FadeTransition graphFade = new FadeTransition(Duration.seconds(1), logoGraph);
        graphFade.setDelay(Duration.seconds(1));
        graphFade.setToValue(1.0);

        FadeTransition bloomFade = new FadeTransition(Duration.seconds(1.5), bloomCircle);
        bloomFade.setDelay(Duration.seconds(1.2));
        bloomFade.setFromValue(0);
        bloomFade.setToValue(0.4);
        bloomFade.setAutoReverse(true);
        bloomFade.setCycleCount(Timeline.INDEFINITE);
        bloomFade.play();

        // labels and loaders
        FadeTransition titleFade = new FadeTransition(Duration.seconds(1), titleLabel);
        titleFade.setDelay(Duration.seconds(1.5));
        titleFade.setToValue(1.0);

        FadeTransition taglineFade = new FadeTransition(Duration.seconds(1), taglineLabel);
        taglineFade.setDelay(Duration.seconds(1.8));
        taglineFade.setToValue(0.7);

        FadeTransition loaderFade = new FadeTransition(Duration.seconds(1), progressArea);
        loaderFade.setDelay(Duration.seconds(2.2));
        loaderFade.setToValue(1.0);

        ParallelTransition sequence = new ParallelTransition(
            hexAssembly, graphFade, titleFade, taglineFade, loaderFade
        );
        
        sequence.setOnFinished(e -> startLoadingProgress());
        sequence.play();
    }

    private void startBinaryRain() {
        GraphicsContext gc = binaryCanvas.getGraphicsContext2D();
        String[] codes = {"0", "1", "dp[i]", "node", "->", "++"};
        
        binaryRainTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) { // Every 100ms
                    gc.clearRect(0, 0, 150, 150);
                    gc.setFill(Color.web("#38bdf8", 0.6));
                    gc.setFont(new javafx.scene.text.Font("Monospace", 10));
                    for (int i = 0; i < 15; i++) {
                        gc.fillText(codes[random.nextInt(codes.length)], random.nextDouble() * 150, random.nextDouble() * 150);
                    }
                    lastUpdate = now;
                }
            }
        };
        binaryRainTimer.start();
    }

    private void startLoadingProgress() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressFill.widthProperty(), 0)),
            new KeyFrame(Duration.seconds(1.5), e -> loadingLabel.setText("Optimizing Heuristics...")),
            new KeyFrame(Duration.seconds(3), e -> loadingLabel.setText("Allocating Memory Grids...")),
            new KeyFrame(Duration.seconds(4.5), new KeyValue(progressFill.widthProperty(), 400))
        );

        timeline.setOnFinished(event -> transitionToDashboard());
        timeline.play();
    }

    private void transitionToDashboard() {
        binaryRainTimer.stop();
        
        //  fade out audio matching the graphical fade
        if (mediaPlayer != null) {
            Timeline fadeAudio = new Timeline(
                new KeyFrame(Duration.millis(1000), new KeyValue(mediaPlayer.volumeProperty(), 0.0))
            );
            fadeAudio.setOnFinished(e -> mediaPlayer.stop());
            fadeAudio.play();
        }
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), rootPane);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
                root.setOpacity(0);
                stage.setScene(scene);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), root);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }
}
