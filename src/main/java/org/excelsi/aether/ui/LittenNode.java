package org.excelsi.aether.ui;


import java.util.List;
import java.util.ArrayList;
import com.jme3.light.Light;
import com.jme3.scene.Node;


public class LittenNode extends Node implements Litten {
    private List<Light> _lights = new ArrayList<>();


    public LittenNode() {
    }

    public LittenNode(final String name) {
        super(name);
    }

    public void addChildLight(final Light light) {
        _lights.add(light);
    }

    @Override public List<Light> getAllLights() {
        return _lights;
    }
}
