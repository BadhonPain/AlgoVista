module com.AlgoVista {
    requires javafx.controls;
    requires javafx.fxml;

    // This allows JavaFX to load your screens
    opens com.AlgoVista.dashboard to javafx.fxml;
    exports com.AlgoVista.dashboard;
    opens com.AlgoVista.graphs to javafx.fxml;
    exports com.AlgoVista.graphs;

    opens com.AlgoVista.images to javafx.graphics, javafx.fxml;
}