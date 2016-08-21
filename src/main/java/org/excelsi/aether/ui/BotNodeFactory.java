package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.scene.LightNode;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Box;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.math.FastMath;
import com.jme3.light.PointLight;
import org.excelsi.aether.Patsy;
import com.jme3.scene.control.LightControl;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.bounding.BoundingBox;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.excelsi.aether.NHBot;
import java.io.File;


public class BotNodeFactory extends AssetNodeFactory<NHBot> {
    private static final Logger LOG = LoggerFactory.getLogger(BotNodeFactory.class);


    public BotNodeFactory(final AssetManager assets) {
        super(assets);
    }

    @Override public Spatial createNode(final String name, final NHBot s) {
        try {
            final Spatial n = loadModel(s.getModel(), s.getColor(), Display.single);
            n.setLocalRotation(new Quaternion(new float[]{FastMath.PI/2f, 0f, 0f}));
            n.setLocalScale(2.0f);
            // duplicate center after scale/rot
            Nodes.center(n);

            final PointLight light = new PointLight();
            light.setColor(ColorRGBA.Red);
            light.setRadius(10f);
            final LightNode ln = new LightNode("light", new LightControl(light));
            ln.setLocalTranslation(0f, 1f, 0f);

            final LittenNode parent = new LittenNode(name);
            parent.attachChild(ln);
            parent.attachChild(n);
            parent.addChildLight(light);

            return parent;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return new Node("blot");
    }
}
