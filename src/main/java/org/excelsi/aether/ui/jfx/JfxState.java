package org.excelsi.aether.ui.jfx;


import java.net.URL;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.fxml.FXMLLoader;

import org.excelsi.aether.StateChangeEvent;
import java.util.ResourceBundle;

import org.lwjgl.opengl.Display;


public class JfxState extends HudNode {
    private final FXMLLoader _loader = new FXMLLoader();


    public JfxState() {
        addLogicHandler((le)->{
            if(le.e() instanceof StateChangeEvent) {
                final StateChangeEvent se = (StateChangeEvent) le.e();
                if(!getChildren().isEmpty()) {
                    final Region doomed = (Region) getChildren().get(0);
                    transition(doomed, (e)->{ getChildren().remove(doomed); });
                }
                final String urlName = String.format("/ui/state-%s.fxml", se.getNewValue().getName());
                final URL url = getClass().getResource(urlName);
                if(url!=null) {
                    try {
                        final Node stateRoot = _loader.load(url, Resources.jfxResources());
                        getChildren().add(0, stateRoot);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
