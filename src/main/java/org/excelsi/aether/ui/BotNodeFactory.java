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
import org.excelsi.aether.Architecture;
import org.excelsi.aether.Armament;
import org.excelsi.aether.Item;
import org.excelsi.aether.Orientation;
import java.io.File;


public class BotNodeFactory extends AssetNodeFactory<NHBot> {
    private static final Logger LOG = LoggerFactory.getLogger(BotNodeFactory.class);


    public BotNodeFactory(final AssetManager assets) {
        super(assets);
    }

    @Override public Spatial createNode(final String name, final NHBot s, final SceneContext sc) {
        try {
            final Spatial n = loadModel(s.getModel(), s.getColor(), Architecture.structural, Orientation.natural);
            n.setLocalRotation(new Quaternion(new float[]{FastMath.PI/2f, 0f, 0f}));
            float scale = 1f;
            switch(s.getSize()) {
                case tiny:
                    scale = 0.4f;
                    break;
                case small:
                    scale = 0.7f;
                    break;
                case medium:
                default:
                    break;
                case large:
                    scale = 1.3f;
                    break;
                case huge:
                    scale = 1.6f;
                    break;
            }
            n.setLocalScale(3.5f*scale);
            // duplicate center after scale/rot
            Nodes.centerAbove(n);

            Node localMove = new Node("localMove");
            localMove.attachChild(n);
            localMove = adorn(s, localMove, sc);

            final PointLight light = new PointLight();
            light.setColor(ColorRGBA.White);
            light.setRadius(10f);
            final LightNode ln = new LightNode("light", new LightControl(light));
            ln.setLocalTranslation(0f, 1f, 0f);

            final LittenNode parent = new LittenNode(name);
            parent.attachChild(ln);
            parent.attachChild(localMove);
            parent.addChildLight(light);

            final int rot = OrientController.rotFor(s.getEnvironment().getFacing());
            parent.setLocalRotation(new Quaternion(new float[]{0f, UIConstants.ROTATIONS[rot], 0f}));
            return parent;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return new Node("blot");
    }

    private Node adorn(final NHBot b, final Node n, final SceneContext c) {
        SlotNode p = new SlotNode("ornaments");
        p.attachChild(n);
        Bots.wield(p, b, c);
        for(Item i:b.getWearing()) {
            Bots.wear(p, b, i, c);
        }
        return p;
    }
}
