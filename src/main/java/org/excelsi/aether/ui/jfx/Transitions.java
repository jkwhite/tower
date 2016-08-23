package org.excelsi.aether.ui.jfx;



import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;


public final class Transitions {
    public static void transition(final String name, final Region n) {
        transition(name, n, null);
    }

    public static void transition(final String name, final Region n, EventHandler<ActionEvent> h) {
        switch(name) {
            case "fadeIn":
                fadeIn(n, h);
                break;
            case "fadeOut":
                fadeOut(n, h);
                break;
            case "slideInoutNorth":
            default:
                slideInoutNorth(n, h);
                break;
        }
    }

    public static void slideInoutNorth(final Region n, final EventHandler<ActionEvent> h) {
        n.setTranslateY(-n.getPrefHeight());
        final TranslateTransition in = new TranslateTransition(Duration.millis(1000), n);
        in.setByY(n.getPrefHeight());
        in.setInterpolator(Interpolator.EASE_IN);
        final TranslateTransition out = new TranslateTransition(Duration.millis(1000), n);
        out.setByY(-n.getPrefHeight());
        out.setInterpolator(Interpolator.EASE_IN);
        final Transition tt = new SequentialTransition(
            in,
            new PauseTransition(Duration.millis(1000)),
            out);
        tt.setOnFinished(h);
        tt.play();
    }

    public static void fadeIn(final Node n, final EventHandler<ActionEvent> h) {
        final FadeTransition ft = new FadeTransition(Duration.millis(250), n);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setOnFinished(h);
        ft.play();
    }

    public static void fadeOut(final Node n, final EventHandler<ActionEvent> h) {
        final FadeTransition ft = new FadeTransition(Duration.millis(250), n);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(h);
        ft.play();
    }

    private Transitions() {}
}
