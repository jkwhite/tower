package org.excelsi.aether.ui;


import java.util.HashMap;
import java.util.Map;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import org.excelsi.matrix.Typed;
import org.excelsi.aether.Context;


public final class SceneContext {
    private final NodeFactory _nfactory;
    private final Map<String,Spatial> _objects = new HashMap<>();
    private final Node _root;
    private final Context _ctx;
    private final Camera _camera;
    private CameraNode _cameraNode;


    public SceneContext(final Context ctx, final Camera camera, final Node root, final NodeFactory nfactory) {
        _ctx = ctx;
        _camera = camera;
        _nfactory = nfactory;
        addNode(root);
        _root = root;
    }

    public Context ctx() {
        return _ctx;
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

    public <T extends Node> T getNode(final Typed id) {
        return (T) _objects.get(id.getId());
    }

    public Spatial getSpatial(final String name) {
        return _objects.get(name);
    }

    public Spatial getSpatial(final Typed id) {
        return _objects.get(id.getId());
    }

    public void addNode(final Spatial node) {
        final String name = node.getName();
        if(_objects.containsKey(name)) {
            throw new IllegalArgumentException("already contains node named '"+name+"': "+_objects.get(name));
        }
        _objects.put(name, node);
    }

    public void setCameraNode(final CameraNode n) {
        _cameraNode = n;
    }

    public <T extends CameraNode> T getCameraNode() {
        return (T) _cameraNode;
    }

    public boolean containsNode(final String name) {
        return _objects.containsKey(name);
    }

    public boolean containsNode(final Typed id) {
        return _objects.containsKey(id.getId());
    }

    public void removeSpatial(final Typed id) {
        _objects.remove(id.getId());
    }

    public void removeAll() {
        _objects.clear();
        addNode(_root);
    }
}
