module com.AlgoVista {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    // This allows JavaFX to load your screens and controllers
    opens com.AlgoVista.dashboard to javafx.fxml;
    exports com.AlgoVista.dashboard;

    opens com.AlgoVista.graphs;
    exports com.AlgoVista.graphs;

    opens com.AlgoVista.heap;
    exports com.AlgoVista.heap;

    opens com.AlgoVista.bst;
    exports com.AlgoVista.bst;

    opens com.AlgoVista.recursion;
    exports com.AlgoVista.recursion;

    opens com.AlgoVista.trees;
    exports com.AlgoVista.trees;

    opens com.AlgoVista.dnc;
    exports com.AlgoVista.dnc;

    // Explicitly opening and exporting the DP module
    opens com.AlgoVista.dp;
    exports com.AlgoVista.dp;

    // newly added modules from joya's branch
    opens com.AlgoVista.array to javafx.fxml;
    exports com.AlgoVista.array;
    opens com.AlgoVista.linkedlist to javafx.fxml;
    exports com.AlgoVista.linkedlist;
    opens com.AlgoVista.stack to javafx.fxml;
    exports com.AlgoVista.stack;
    opens com.AlgoVista.queue to javafx.fxml;
    exports com.AlgoVista.queue;
    opens com.AlgoVista.sorting to javafx.fxml;
    exports com.AlgoVista.sorting;

    // Resources and Utilities
    opens com.AlgoVista.images;
    opens com.AlgoVista.utils;
    exports com.AlgoVista.utils;
}
