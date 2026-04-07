package com.AlgoVista.dashboard;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private StackPane rootPane;
    @FXML private TilePane linearContainer;
    @FXML private TilePane graphContainer;
    @FXML private TilePane algoSectionContainer; // Renamed from algoContainer
    @FXML private VBox linearSection;
    @FXML private VBox graphSection;
    @FXML private VBox algoSection;
    @FXML private TextField searchField;
    @FXML private StackPane overlayPane;
    @FXML private VBox modalContent;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox scrollContent;

    private final List<String> algorithms = Arrays.asList(
            "Array", "Linked List", "Stack", "Queue", "Sorting", "Graph",
            "BST", "Recursion", "Heap", "D & C", "DP");

    @FXML
    public void initialize() {
        loadCards(""); // Load all cards initially
        
        // Setup overlay interaction
        overlayPane.setOnMouseClicked(e -> closeModal());
        modalContent.setOnMouseClicked(e -> e.consume()); 

        // Robust automatic layout fix for ScrollPane & TilePane calculation bugs
        Platform.runLater(() -> {
            if (mainScrollPane != null && scrollContent != null) {
                mainScrollPane.viewportBoundsProperty().addListener((obs, oldV, newV) -> {
                    scrollContent.setPrefWidth(newV.getWidth());
                    scrollContent.requestLayout();
                });
            }
            
            if (rootPane.getScene() != null && rootPane.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                
                // Add listener to maximized property to force a layout recalculation
                stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
                    Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(150), evt -> {
                            rootPane.requestLayout();
                            rootPane.applyCss();
                        })
                    );
                    timeline.play();
                });
            }
        });
    }

    @FXML
    private void onSearch() {
        loadCards(searchField.getText().toLowerCase());
    }

    @FXML
    private void onSettings() {
        showModal(createSettingsContent());
    }

    @FXML
    private void onAbout() {
        showModal(createAboutContent());
    }

    private void showModal(Node content) {
        modalContent.getChildren().clear();
        modalContent.getChildren().add(content);
        
        overlayPane.setMouseTransparent(false);
        FadeTransition ft = new FadeTransition(Duration.millis(300 * com.AlgoVista.utils.SettingsManager.getSleepMultiplier()), overlayPane);
        ft.setFromValue(overlayPane.getOpacity());
        ft.setToValue(1.0);
        ft.play();
    }

    private StackPane createAboutContent() {
        StackPane root = new StackPane();
        root.setPrefSize(900, 600);
        root.setMaxSize(900, 600);
        
        // Layer 1: Background Animation (Splash-inspired)
        Pane bgPane = new Pane();
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(900, 600);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        bgPane.setClip(clip);
        setupAboutBackground(bgPane);
        
        // Layer 2: Content
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));

        Label title = new Label("DEVELOPMENT TEAM");
        title.getStyleClass().add("modal-title");
        title.setStyle("-fx-font-size: 24px; -fx-letter-spacing: 5px; -fx-text-fill: #38bdf8;");

        HBox teamBox = new HBox(40);
        teamBox.setAlignment(Pos.CENTER);
        
        // Left Card: Badhon Pain (Cyan) - with photo
        VBox badhonCard = createBadhonCard();
        
        // Right Card: Joyshree Mukharjee Joya (Purple)
        VBox joyaCard = createJoyaCard();

        teamBox.getChildren().addAll(badhonCard, joyaCard);

        content.getChildren().addAll(title, teamBox, createCloseButton());
        root.getChildren().addAll(bgPane, content);
        return root;
    }

    private void setupAboutBackground(Pane bg) {
        Random rand = new Random();
        String[] colors = {"#38bdf8", "#818cf8", "#c084fc", "#e879f9"}; // Cyan, Indigo, Purple, Pink
        
        // Premium large floating glowing orbs
        for (int i = 0; i < 15; i++) {
            double radius = 15 + rand.nextDouble() * 30; // 15px to 45px radius
            Color color = Color.web(colors[rand.nextInt(colors.length)], 0.12 + rand.nextDouble() * 0.18);
            Circle orb = new Circle(rand.nextDouble() * 900, rand.nextDouble() * 600, radius, color);
            
            // Soft blur effect native to JavaFX
            javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(radius * 0.6);
            orb.setEffect(blur);
            
            bg.getChildren().add(orb);
            animateFloating(orb, rand);
        }
    }

    private void animateFloating(Node node, Random rand) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(15 + rand.nextDouble() * 15), node);
        tt.setByX((rand.nextBoolean() ? 1 : -1) * (40 + rand.nextDouble() * 40));
        tt.setByY((rand.nextBoolean() ? 1 : -1) * (40 + rand.nextDouble() * 40));
        tt.setAutoReverse(true);
        tt.setCycleCount(Timeline.INDEFINITE);
        tt.play();
    }

    private VBox createBadhonCard() {
        String accentColor = "#38bdf8";
        VBox card = new VBox(12); // Reduced from 18 to fit better vertically
        card.getStyleClass().add("team-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(440);

        // Circular Avatar with glowing border — large headshot
        StackPane avatarPane = new StackPane();
        avatarPane.setAlignment(Pos.CENTER);

        Circle glowRing = new Circle(113);
        glowRing.setStyle("-fx-fill: transparent; -fx-stroke: #38bdf8; -fx-stroke-width: 3; -fx-effect: dropshadow(gaussian, #38bdf8, 22, 0.7, 0, 0);");

        Circle photoCircle = new Circle(105);
        try {
            Image img = new Image(getClass().getResourceAsStream("/com/AlgoVista/images/badhon.png"));
            double iw = img.getWidth();
            double ih = img.getHeight();
            // Very tight crop: 32% of width centered, start at 15% from top (skips sky+wire, shows face)
            double cropSize = iw * 0.40;
            // Shifted the starting X much further left (-30) to powerfully push the image RIGHT inside the circle
            double startX = ((iw - cropSize) / 2.0) - 30.0;
            double startY = ih * 0.25; 
            double px = -startX / cropSize;
            double py = -startY / cropSize;
            double pw = iw / cropSize;
            double ph = ih / cropSize;
            photoCircle.setFill(new ImagePattern(img, px, py, pw, ph, true));
        } catch (Exception e) {
            photoCircle.setFill(Color.web("#1e293b"));
        }
        photoCircle.setStyle("-fx-stroke: #38bdf8; -fx-stroke-width: 2;");
        avatarPane.getChildren().addAll(glowRing, photoCircle);


        Label nameLbl = new Label("BADHON PAIN");
        nameLbl.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 26px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");

        // Separator line
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setPrefWidth(200);
        sep.setStyle("-fx-background-color: linear-gradient(to right, transparent, #38bdf8, transparent);");

        VBox contribBox = new VBox(5);
        contribBox.setAlignment(Pos.CENTER);
        Label contribHeader = new Label("CONTRIBUTION");
        contribHeader.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold; -fx-letter-spacing: 1.5px;");
        Label contribText = new Label("Responsive UI, Aesthetic Design, Non-Linear DS's & Algorithms");
        contribText.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-family: 'Georgia', serif; -fx-font-style: italic; -fx-opacity: 0.95; -fx-text-alignment: center; -fx-line-spacing: 4px;");
        contribText.setWrapText(true);
        contribText.setMaxWidth(400);
        contribText.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        contribText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        contribText.setAlignment(Pos.CENTER);
        contribBox.getChildren().addAll(contribHeader, contribText);

        VBox academicBox = new VBox(6);
        academicBox.setAlignment(Pos.CENTER);
        Label idLbl = new Label("ID:  2405087");
        idLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label deptLbl = new Label("Department of CSE, BUET");
        deptLbl.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 14px; -fx-opacity: 0.9;");
        academicBox.getChildren().addAll(idLbl, deptLbl);

        card.getChildren().addAll(avatarPane, nameLbl, sep, contribBox, academicBox);
        return card;
    }

    private VBox createJoyaCard() {
        String accentColor = "#c084fc";
        VBox card = new VBox(12);
        card.getStyleClass().add("team-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(440);

        // Circular Avatar with glowing border — blank placeholder for now
        StackPane avatarPane = new StackPane();
        avatarPane.setAlignment(Pos.CENTER);

        Circle glowRing = new Circle(113);
        glowRing.setStyle("-fx-fill: transparent; -fx-stroke: " + accentColor + "; -fx-stroke-width: 3; -fx-effect: dropshadow(gaussian, " + accentColor + ", 22, 0.7, 0, 0);");

        Circle photoCircle = new Circle(105);
        try {
            Image img = new Image(getClass().getResourceAsStream("/com/AlgoVista/images/joya.jpeg"));
            if (img.isError()) {
                throw new Exception("Image load error: joya.jpeg could not be loaded");
            }
            double iw = img.getWidth();
            double ih = img.getHeight();
            // Zoom in slightly (80% of original view)
            double cropSize = Math.min(iw, ih) * 0.5;
            double startX = ((iw - cropSize) / 2.0) + 60.0; // shifted crop frame right => image shifts left
            double startY = (ih - cropSize) / 2.0 * 1.5; // shifted further up to re-center the face after zooming
            
            double px = -startX / cropSize;
            double py = -startY / cropSize;
            double pw = iw / cropSize;
            double ph = ih / cropSize;
            photoCircle.setFill(new ImagePattern(img, px, py, pw, ph, true));
        } catch (Exception e) {
            photoCircle.setFill(Color.web("#1e293b"));
            System.err.println("Failed to load Joya's profile image: " + e.getMessage());
        }
        photoCircle.setStyle("-fx-stroke: " + accentColor + "; -fx-stroke-width: 2;");

        avatarPane.getChildren().addAll(glowRing, photoCircle);


        Label nameLbl = new Label("JOYSHREE MUKHARJEE");
        nameLbl.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 26px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");

        // Separator line
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setPrefWidth(200);
        sep.setStyle("-fx-background-color: linear-gradient(to right, transparent, " + accentColor + ", transparent);");

        VBox contribBox = new VBox(5);
        contribBox.setAlignment(Pos.CENTER);
        Label contribHeader = new Label("CONTRIBUTION");
        contribHeader.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold; -fx-letter-spacing: 1.5px;");
        Label contribText = new Label("Linear Data Structures & Sorting Algorithm");
        contribText.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-family: 'Georgia', serif; -fx-font-style: italic; -fx-opacity: 0.95; -fx-text-alignment: center; -fx-line-spacing: 5px;");
        contribText.setWrapText(true);
        contribText.setMaxWidth(400);
        contribText.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        contribText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        contribText.setAlignment(Pos.CENTER);
        contribBox.getChildren().addAll(contribHeader, contribText);

        VBox academicBox = new VBox(6);
        academicBox.setAlignment(Pos.CENTER);
        Label idLbl = new Label("ID: 2405083");
        idLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label deptLbl = new Label("Department of CSE, BUET");
        deptLbl.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 14px; -fx-opacity: 0.9;");
        academicBox.getChildren().addAll(idLbl, deptLbl);

        card.getChildren().addAll(avatarPane, nameLbl, sep, contribBox, academicBox);
        return card;
    }

    private Region createSocialIcon(String type) {
        Region icon = new Region();
        icon.getStyleClass().add("social-icon-mini");
        // Simplified shapes for Github, LinkedIn
        return icon;
    }

    @FXML
    private void closeModal() {
        FadeTransition ft = new FadeTransition(Duration.millis(250), overlayPane);
        ft.setFromValue(overlayPane.getOpacity());
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            overlayPane.setMouseTransparent(true);
            modalContent.getChildren().clear();
        });
        ft.play();
    }

    private VBox createSettingsContent() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(10, 20, 10, 20));
        
        Label title = new Label("SETTINGS");
        title.getStyleClass().add("modal-title");
        title.setStyle("-fx-font-size: 28px; -fx-letter-spacing: 6px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Section 1: Preferences
        VBox prefsSection = new VBox(20);
        prefsSection.getStyleClass().add("settings-section");
        prefsSection.setAlignment(Pos.CENTER);
        
        Label prefsHeader = new Label("GLOBAL PREFERENCES");
        prefsHeader.getStyleClass().add("settings-header");

        // Audio Row
        HBox audioRow = new HBox(20);
        audioRow.setAlignment(Pos.CENTER);
        Label audioLbl = new Label("UI Sound Effects");
        audioLbl.getStyleClass().add("settings-label");
        StackPane toggleSwitch = buildMobileToggle();
        audioRow.getChildren().addAll(audioLbl, toggleSwitch);

        // Speed Row
        VBox speedRow = new VBox(10);
        speedRow.setAlignment(Pos.CENTER);
        Label speedLbl = new Label("Animation Speed");
        speedLbl.getStyleClass().add("settings-label");
        
        HBox sliderBox = new HBox(15);
        sliderBox.setAlignment(Pos.CENTER);
        Slider speedSlider = new Slider(0.25, 2.0, com.AlgoVista.utils.SettingsManager.getSpeed());
        speedSlider.setPrefWidth(220);
        
        Label speedValueLbl = new Label(String.format("%.2fx", speedSlider.getValue()));
        speedValueLbl.getStyleClass().add("value-label");
        
        speedSlider.valueProperty().addListener((obs, oldV, newV) -> {
            com.AlgoVista.utils.SettingsManager.setSpeed(newV.doubleValue());
            speedValueLbl.setText(String.format("%.2fx", newV.doubleValue()));
        });
        
        sliderBox.getChildren().addAll(speedSlider, speedValueLbl);
        speedRow.getChildren().addAll(speedLbl, sliderBox);

        prefsSection.getChildren().addAll(prefsHeader, audioRow, speedRow);

        // Section 2: Shortcuts
        VBox shortcutSection = new VBox(20);
        shortcutSection.getStyleClass().add("settings-section");
        shortcutSection.setAlignment(Pos.CENTER);
        
        Label shortcutHeader = new Label("KEYBOARD SHORTCUTS");
        shortcutHeader.getStyleClass().add("settings-header");

        GridPane shortcuts = new GridPane();
        shortcuts.setHgap(0);
        shortcuts.setVgap(15);
        shortcuts.setAlignment(Pos.CENTER);
        
        addShortcutRow(shortcuts, 0, "SPACE", "PAUSE / RESUME");
        addShortcutRow(shortcuts, 1, "R", "RESET ALGORITHM");
        addShortcutRow(shortcuts, 2, "RIGHT", "STEP FORWARD");
        addShortcutRow(shortcuts, 3, "ESC", "BACK TO HOME");

        shortcutSection.getChildren().addAll(shortcutHeader, shortcuts);

        content.getChildren().addAll(title, prefsSection, shortcutSection, createCloseButton());
        return content;
    }

    private StackPane buildMobileToggle() {
        StackPane toggle = new StackPane();
        toggle.setPrefSize(42, 22);
        toggle.setMaxSize(42, 22);
        
        javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle(42, 22);
        bg.setArcWidth(22);
        bg.setArcHeight(22);
        
        double thumbRadius = 8;
        javafx.scene.shape.Circle thumb = new javafx.scene.shape.Circle(thumbRadius);
        thumb.setFill(javafx.scene.paint.Color.WHITE);
        thumb.setEffect(new javafx.scene.effect.DropShadow(2, javafx.scene.paint.Color.rgb(0,0,0,0.4)));
        
        // Initial state
        boolean isEnabled = com.AlgoVista.utils.SettingsManager.isAudioEnabled();
        bg.setFill(isEnabled ? javafx.scene.paint.Color.web("#38bdf8") : javafx.scene.paint.Color.web("#475569"));
        toggle.setAlignment(isEnabled ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        StackPane.setMargin(thumb, new Insets(0, 3, 0, 3));
        
        toggle.getChildren().addAll(bg, thumb);
        toggle.setCursor(javafx.scene.Cursor.HAND);
        
        toggle.setOnMouseClicked(e -> {
            boolean newState = !com.AlgoVista.utils.SettingsManager.isAudioEnabled();
            com.AlgoVista.utils.SettingsManager.setAudioEnabled(newState);
            
            // Animate transition natively
            bg.setFill(newState ? javafx.scene.paint.Color.web("#38bdf8") : javafx.scene.paint.Color.web("#475569"));
            toggle.setAlignment(newState ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        });
        
        return toggle;
    }

    private void addShortcutRow(GridPane grid, int row, String key, String action) {
        Label actionLabel = new Label(action);
        actionLabel.getStyleClass().add("shortcut-action-label");
        
        Label dots = new Label("......................");
        dots.getStyleClass().add("shortcut-dots");
        
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("shortcut-key-pro");
        
        grid.add(actionLabel, 0, row);
        grid.add(dots, 1, row);
        grid.add(keyLabel, 2, row);
    }

    private Button createCloseButton() {
        Button closeBtn = new Button("DONE");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setOnAction(e -> closeModal());
        return closeBtn;
    }

    private void loadCards(String filter) {
        // Clear all containers
        linearContainer.getChildren().clear();
        graphContainer.getChildren().clear();
        algoSectionContainer.getChildren().clear();

        for (String name : algorithms) {
            String fullName = getFullName(name);
            if (name.toLowerCase().contains(filter) || fullName.toLowerCase().contains(filter)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AlgoCard.fxml"));
                    StackPane card = loader.load();
                    CardController controller = loader.getController();
                    controller.setData(name);

                    // Add to correct group
                    String category = getCategory(name);
                    switch (category) {
                        case "linear": linearContainer.getChildren().add(card); break;
                        case "graph": graphContainer.getChildren().add(card); break;
                        case "algo": algoSectionContainer.getChildren().add(card); break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Hide empty sections during search
        updateSectionVisibility();
    }

    private String getCategory(String algoName) {
        algoName = algoName.toLowerCase();
        if (Arrays.asList("array", "linked list", "stack", "queue").contains(algoName)) return "linear";
        if (Arrays.asList("graph", "bst", "heap").contains(algoName)) return "graph";
        return "algo"; // sorting, recursion, d&c, dp
    }

    private void updateSectionVisibility() {
        linearSection.setManaged(!linearContainer.getChildren().isEmpty());
        linearSection.setVisible(!linearContainer.getChildren().isEmpty());
        
        graphSection.setManaged(!graphContainer.getChildren().isEmpty());
        graphSection.setVisible(!graphContainer.getChildren().isEmpty());
        
        algoSection.setManaged(!algoSectionContainer.getChildren().isEmpty());
        algoSection.setVisible(!algoSectionContainer.getChildren().isEmpty());
    }

    private String getFullName(String algoName) {
        switch (algoName.toLowerCase()) {
            case "bst": return "Binary Search Tree";
            case "d & c": return "Divide and Conquer";
            case "dp": return "Dynamic Programming";
            default: return algoName;
        }
    }

    private String getStyleClass(String algoName) {
        switch (algoName.toLowerCase()) {
            case "d & c": return "divide-conquer-card";
            case "recursion": return "recursion-card";
            case "array": return "array-card";
            case "linked list": return "linked-list-card";
            case "stack": return "stack-card";
            case "queue": return "queue-card";
            case "graph": return "graph-card";
            case "bst": return "bst-card";
            case "heap": return "heap-card";
            case "sorting": return "sorting-card";
            case "dp": return "dp-card";
            default: return "";
        }
    }
}