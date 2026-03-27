package com.AlgoVista.dashboard;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ShortcutManager {

    public interface ShortcutAction {
        void execute();
    }

    public static void registerStandardShortcuts(Scene scene, 
                                               ShortcutAction onPlayPause,
                                               ShortcutAction onStepForward,
                                               ShortcutAction onReset,
                                               ShortcutAction onBack) {
        if (scene == null) return;

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (onPlayPause != null) onPlayPause.execute();
                event.consume();
            } else if (event.getCode() == KeyCode.RIGHT) {
                if (onStepForward != null) onStepForward.execute();
                event.consume();
            } else if (event.getCode() == KeyCode.R) {
                if (onReset != null) onReset.execute();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                if (onBack != null) onBack.execute();
                event.consume();
            }
        });
    }
}
