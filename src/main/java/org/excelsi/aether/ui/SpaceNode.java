package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.excelsi.aether.Item;
import org.excelsi.aether.NHSpace;


public class SpaceNode extends Node {
    private final NHSpace _space;


    public SpaceNode(final NHSpace space) {
        super(space.getId());
        _space = space;
    }

    public void attachItem(final SceneContext c, final Item item) {
        final Spatial s = Spaces.createItem(c, item);
        Spaces.attachItem(this, s);
    }
}
