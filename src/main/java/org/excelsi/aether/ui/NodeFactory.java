package org.excelsi.aether.ui;


import com.jme3.scene.Spatial;
import org.excelsi.matrix.Typed;


public interface NodeFactory<E extends Typed> {
    Spatial createNode(String name, E s, SceneContext c);
}
