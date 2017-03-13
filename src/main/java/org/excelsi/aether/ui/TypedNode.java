package org.excelsi.aether.ui;


import com.jme3.scene.Node;

import org.excelsi.matrix.Typed;


public class TypedNode extends Node {
    private final Typed _t;


    public TypedNode(final Typed t) {
        super(t.getId());
        _t = t;
    }

    public Typed getTyped() {
        return _t;
    }
}
