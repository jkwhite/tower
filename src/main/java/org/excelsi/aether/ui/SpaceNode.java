package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import org.excelsi.aether.Item;
import org.excelsi.aether.NHSpace;


public class SpaceNode extends Node {
    private final NHSpace _space;


    public SpaceNode(final NHSpace space) {
        super(space.getId());
        _space = space;
    }

    public void attachItem(final SceneContext c, final Item item, final int idx, final boolean incremented) {
        final Spatial s = Spaces.createItem(c, item);
        s.setLocalTranslation(new Vector3f(0f, 0.2f*idx, 0f));
        attachChild(s);
    }

    public void detachItem(final SceneContext c, final Item item) {
        detachChildNamed(item.getId());
    }
}
