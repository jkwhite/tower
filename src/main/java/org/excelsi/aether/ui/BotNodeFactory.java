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
import org.excelsi.aether.Armament;
import org.excelsi.aether.Item;
import java.io.File;


public class BotNodeFactory extends AssetNodeFactory<NHBot> {
    public static final Vector3f WEAPON_OFFSET = new Vector3f(-0.8f, 2f, 0.6f).multLocal(0.7f);
    public static final Vector3f MIS_WEAPON_OFFSET = new Vector3f(-0.2f, 2f, 1.0f).multLocal(0.7f);
    public static final float[] WEAPON_ROT = new float[]{FastMath.PI/2.5f, FastMath.PI, 0f};
    public static final float[] MIS_WEAPON_ROT = new float[]{FastMath.PI/2f, -FastMath.PI/2f, 0f};

    private static final Logger LOG = LoggerFactory.getLogger(BotNodeFactory.class);


    public BotNodeFactory(final AssetManager assets) {
        super(assets);
    }

    @Override public Spatial createNode(final String name, final NHBot s, final SceneContext sc) {
        try {
            final Spatial n = loadModel(s.getModel(), s.getColor(), Display.single);
            n.setLocalRotation(new Quaternion(new float[]{FastMath.PI/2f, 0f, 0f}));
            n.setLocalScale(3.0f);
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
        wield(p, b, c);
        //for(Item i:bot.getWearing()) {
            //wear(p, bot, i);
        //}
        return p;
    }

    public static void wield(SlotNode n, NHBot b, SceneContext c) {
        Spatial old = n.getChild("wielded");
        if(old!=null) {
            n.detachChild(old);
            n.removeSlotUI("wielded");
        }
        Item i = b.getWielded();
        if(i!=null&&i.getModel()!=null&&i.getColor()!=null) {
            final Spatial w = c.getNodeFactory().createNode("wielded", i, c);
            w.setName("wielded");
            boolean mis = true;
            if(((Armament)i).getType()==Armament.Type.missile) {
                w.setLocalTranslation(new Vector3f(MIS_WEAPON_OFFSET));
                w.setLocalRotation(new Quaternion(MIS_WEAPON_ROT));
            }
            else {
                w.setLocalTranslation(new Vector3f(WEAPON_OFFSET));
                w.setLocalRotation(new Quaternion(WEAPON_ROT));
                mis = false;
            }
            n.attachChild(w);
            if(n.getParent()!=null) {
                /*
                SpinModulator spin = new SpinModulator(w, new float[]{Rand.om.nextFloat(), Rand.om.nextFloat(), Rand.om.nextFloat()},
                    mis?MIS_WEAPON_ROT:WEAPON_ROT);
                FixedTimeController swash = new FixedTimeController(spin, FixedTimeController.CONSTANT, 0.2f, 0f) {
                    protected void done() {
                        w.removeController(this);
                    }
                };
                w.addController(swash);
                */
            }
            SlotUI sui = SlotUI.create(i.getSlotType(), b, w);
            if(sui!=null) {
                n.addSlotUI("wielded", sui);
            }
        }
    }
}
