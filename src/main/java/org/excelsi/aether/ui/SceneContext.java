package org.excelsi.aether.ui;


import java.util.HashMap;
import java.util.Map;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.renderer.Camera;


public final class SceneContext {
    private final NodeFactory _nfactory;
    private final Map<String,Spatial> _objects = new HashMap<>();
    private final Node _root;
    private final Camera _camera;


    public SceneContext(final Camera camera, final Node root, final NodeFactory nfactory) {
        _camera = camera;
        _nfactory = nfactory;
        addNode(root);
        _root = root;
    }

    public Node getRoot() {
        return _root;
    }

    public Camera getCamera() {
        return _camera;
    }

    public NodeFactory getNodeFactory() {
        return _nfactory;
    }

    public <T extends Node> T getNode(final String name) {
        return (T) _objects.get(name);
    }

    public Spatial getSpatial(final String name) {
        return _objects.get(name);
    }

    public void addNode(final Spatial node) {
        final String name = node.getName();
        if(_objects.containsKey(name)) {
            throw new IllegalArgumentException("already contains node named '"+name+"': "+_objects.get(name));
        }
        _objects.put(name, node);
    }
}
