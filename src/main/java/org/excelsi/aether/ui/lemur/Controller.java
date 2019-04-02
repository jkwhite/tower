package org.excelsi.aether.ui.lemur;


import com.jme3.scene.Node;
import org.excelsi.aether.ui.SceneContext;
import org.excelsi.aether.Event;


public interface Controller {
    boolean consume(Event e, Node gui, SceneContext c);
}
