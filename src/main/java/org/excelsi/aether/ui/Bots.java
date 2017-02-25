package org.excelsi.aether.ui;


import org.excelsi.aether.Armament;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.Item;
import org.excelsi.aether.SlotType;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.LightNode;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.math.FastMath;


public final class Bots {
    public static final Vector3f WEAPON_OFFSET = new Vector3f(-0.8f, 2f, 0.6f).multLocal(0.7f);
    public static final Vector3f MIS_WEAPON_OFFSET = new Vector3f(-0.2f, 2f, 1.0f).multLocal(0.7f);
    public static final float[] WEAPON_ROT = new float[]{FastMath.PI/2.5f, FastMath.PI, 0f};
    public static final float[] MIS_WEAPON_ROT = new float[]{FastMath.PI/2f, -FastMath.PI/2f, 0f};


    public static void attachBot(final SceneContext c, final Node lev, final NHBot b) {
        if(!c.containsNode(b.getId())) {
            final Spatial bot = c.getNodeFactory().createNode(b.getId(), b, c);
            final MatrixMSpace mms = (MatrixMSpace) b.getEnvironment().getMSpace();
            Spaces.translate(mms, bot);
            c.addNode(bot);
            lev.attachChild(bot);
            if(b.isPlayer()) {
                attachPatsy(lev, c, bot);
            }
        }
    }

    public static void detachBot(final SceneContext c, final Node lev, final NHBot b) {
        final Spatial s = c.getNode(b);
        if(s!=null) {
            lev.detachChild(s);
        }
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

    public static void takeOff(SlotNode n, NHBot b, Item i, SceneContext c) {
        //System.err.println("takeoff NODE "+n);
        SlotType st = i.getSlotType();
        String type = "wear-"+st.toString();
        Spatial old = n.getChild(type);
        if(old!=null) {
            n.detachChild(old);
            //n.updateGeometricState(0f, false);
            //n.updateRenderState();
        }
        n.removeSlotUI(type);
    }

    public static void wear(SlotNode n, NHBot b, Item i, SceneContext c) {
        //System.err.println("wear NODE "+n);
        //Thread.dumpStack();
        //System.err.println("WEAR: "+n.getName());
        SlotType st = i.getSlotType();
        String nodename = "wear-"+st.toString();
        String type = nodename;
        Spatial old = n.getChild(type);
        if(old!=null) {
            n.detachChild(old);
            //n.updateGeometricState(0f, false);
            //n.updateRenderState();
            n.removeSlotUI(type);
        }
        //final Node w = loadModel(i, type);
        final Spatial w = c.getNodeFactory().createNode(type, i, c);
        Vector3f offset = null;
        Quaternion rot = null;
        switch(st) {
            case hand:
                offset = new Vector3f(1f,2f,0.55f).multLocal(0.5f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,-FastMath.PI/8f,FastMath.PI/10f});
                break;
            case finger:
                break;
            case arm:
                break;
            case leg:
                break;
            case torso:
                offset = new Vector3f(0f,2f,0.45f).multLocal(0.5f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,FastMath.PI,-FastMath.PI/4f});
                break;
            case head:
                offset = new Vector3f(0f,3.7f,0.15f).multLocal(0.5f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,FastMath.PI,FastMath.PI/2f});
                break;
            case back:
                offset = new Vector3f(0f,2f,-0.5f).multLocal(0.5f);
                rot = new Quaternion(new float[]{-FastMath.PI/2.5f,0f,0f});
                break;
            case foot:
                offset = new Vector3f(0f,0.3f,-0.15f).multLocal(0.5f);
                //rot = new Quaternion(new float[]{FastMath.PI/2f,0f,FastMath.PI/2f});
                rot = new Quaternion(new float[]{FastMath.PI/2f,0f,-FastMath.PI/2f});
                break;
            case eyes:
                break;
            default:
                break;
        }
        SlotUI sui = SlotUI.create(st, b, w);
        if(sui!=null) {
            n.addSlotUI(nodename, sui);
        }

        if(offset!=null) {
            w.setLocalTranslation(offset);
        }
        if(rot!=null) {
            w.setLocalRotation(rot);
        }
        if(offset!=null||rot!=null) {
            n.attachChild(w);
            /*
            if(n.getParent()!=null) {
                Gravity g = new Gravity(w, new Vector3f(0,30,0), new Vector3f(w.getLocalTranslation()));
                w.addController(g);
            }
            */
            //n.updateGeometricState(0f, false);
            //n.updateRenderState();
        }
    }


    private static void attachPatsy(final Node parent, final SceneContext c, final Spatial patsy) {
        //c.<CloseView>getNode(View.NODE_CAMERA).setPlayer(patsy);
        c.<CloseView>getCameraNode().setPlayer(patsy);
        if(patsy instanceof Litten) {
            for(final Light light:((Litten)patsy).getAllLights()) {
                System.err.println("****************  ADDING LIGHT: "+light);
                parent.addLight(light);
            }
            //parent.addLight(((LightNode)patsy).getLight());;
        }
    }

    private Bots() {}
}
