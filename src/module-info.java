module com.AlgoVista {
    requires javafx.controls;
    requires javafx.fxml;

    // This allows JavaFX to load your screens
    opens com.AlgoVista.dashboard to javafx.fxml;

    exports com.AlgoVista.dashboard;

    opens com.AlgoVista.graphs to javafx.fxml;

    exports com.AlgoVista.graphs;

    opens com.AlgoVista.heap to javafx.fxml;

    exports com.AlgoVista.heap;

    opens com.AlgoVista.bst to javafx.fxml;

    exports com.AlgoVista.bst;

    opens com.AlgoVista.recursion to javafx.fxml;

    exports com.AlgoVista.recursion;

    opens com.AlgoVista.images to javafx.graphics, javafx.fxml;

    opens com.AlgoVista.trees to javafx.fxml;
    exports com.AlgoVista.trees;

    opens com.AlgoVista.dnc to javafx.fxml;
    exports com.AlgoVista.dnc;

    opens com.AlgoVista.dp to javafx.fxml;
    exports com.AlgoVista.dp;
}