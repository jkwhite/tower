package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Box;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.LightNode;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;

import org.excelsi.aether.Stage;
import org.excelsi.aether.Keys;


public class LevelNodeFactory implements NodeFactory<Stage> {
    @Override public Spatial createNode(final String name, final Stage s, final SceneContext c) {
        final DirectionalLight light = new DirectionalLight(new Vector3f(0, -1, 0));
        final float intensity = s.findFloat(Keys.LIGHT, 1f);
        final String color = s.findString(Keys.LIGHT_COLOR, "white");
        //final ColorRGBA col = new ColorRGBA(intensity, intensity, intensity, 1f);
        final ColorRGBA col = Materials.colorFor(color);
        light.setColor(col);
        //final Node n = new LightNode(name, light);
        final Node n = new Node(name);
        n.addLight(light);
        return n;
    }
}
