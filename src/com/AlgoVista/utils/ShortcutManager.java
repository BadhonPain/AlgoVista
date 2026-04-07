package com.AlgoVista.utils;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextInputControl;

public class ShortcutManager {

    public interface ShortcutAction {
        void execute();
    }

    /**
     * Registers common shortcuts on a scene.
     * 
     * @param scene The scene to attach listeners to.
     * @param play Toggle play/pause action.
     * @param step Step forward action.
     * @param reset Reset/Clear action.
     * @param back Navigate back action.
     */
    public static void register(Scene scene, 
                                ShortcutAction play, 
                                ShortcutAction step, 
                                ShortcutAction reset, 
                                ShortcutAction back) {
        if (scene == null) return;

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Ignore shortcuts if the user is typing in a TextField or TextArea
            if (event.getTarget() instanceof javafx.scene.control.TextInputControl) {
                return;
            }

            KeyCode code = event.getCode();

            if (code == KeyCode.SPACE) {
                if (play != null) play.execute();
                event.consume();
            } else if (code == KeyCode.RIGHT) {
                if (step != null) step.execute();
                event.consume();
            } else if (code == KeyCode.R) {
                if (reset != null) reset.execute();
                event.consume();
            } else if (code == KeyCode.ESCAPE) {
                if (back != null) back.execute();
                event.consume();
            }
        });
    }
}
