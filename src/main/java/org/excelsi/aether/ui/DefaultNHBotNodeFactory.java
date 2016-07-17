/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.aether.ui;


import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class DefaultNHBotNodeFactory extends NodeFactory {
    public void lock(Node n) {
    }

    public Node createNode(String name, Object s, Node parent) {
        DefaultNHBot bot = (DefaultNHBot) s;
        //Node n = loadModel(bot.getModel());
        Node n = loadModel(bot);
        switch(bot.getSize()) {
            case tiny:
                n.setLocalScale(0.4f);
                break;
            case small:
                n.setLocalScale(0.7f);
                break;
            case medium:
                break;
            case large:
                n.setLocalScale(1.3f);
                break;
            case huge:
                n.setLocalScale(1.6f);
                break;
        }
        SlotNode p = new SlotNode("superweapon");
        p.attachChild(n);
        Node weap = n;
        wield(p, bot);
        for(Item i:bot.getWearing()) {
            wear(p, bot, i);
        }
        return p;
    }

    public static final Vector3f WEAPON_OFFSET = new Vector3f(-0.8f, 2f, 0.6f);
    public static final Vector3f MIS_WEAPON_OFFSET = new Vector3f(-0.2f, 2f, 1.0f);
    public static final float[] WEAPON_ROT = new float[]{FastMath.PI/2.5f, FastMath.PI, 0f};
    public static final float[] MIS_WEAPON_ROT = new float[]{FastMath.PI/2f, -FastMath.PI/2f, 0f};

    public static void takeOff(SlotNode n, DefaultNHBot b, Item i) {
        //System.err.println("takeoff NODE "+n);
        SlotType st = i.getSlotType();
        String type = "wear-"+st.toString();
        Spatial old = n.getChild(type);
        if(old!=null) {
            n.detachChild(old);
            n.updateGeometricState(0f, false);
            n.updateRenderState();
        }
        n.removeSlotUI(type);
    }

    public static void wear(SlotNode n, DefaultNHBot b, Item i) {
        //System.err.println("wear NODE "+n);
        //Thread.dumpStack();
        //System.err.println("WEAR: "+n.getName());
        SlotType st = i.getSlotType();
        String nodename = "wear-"+st.toString();
        String type = nodename;
        Spatial old = n.getChild(type);
        if(old!=null) {
            n.detachChild(old);
            n.updateGeometricState(0f, false);
            n.updateRenderState();
            n.removeSlotUI(type);
        }
        final Node w = loadModel(i, type);
        Vector3f offset = null;
        Quaternion rot = null;
        switch(st) {
            case hand:
                offset = new Vector3f(1f,2f,0.55f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,-FastMath.PI/8f,FastMath.PI/10f});
                break;
            case finger:
                break;
            case arm:
                break;
            case leg:
                break;
            case torso:
                offset = new Vector3f(0f,2f,0.45f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,FastMath.PI,-FastMath.PI/4f});
                break;
            case head:
                offset = new Vector3f(0f,3.7f,0.15f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,FastMath.PI,FastMath.PI/2f});
                break;
            case back:
                offset = new Vector3f(0f,2f,-0.5f);
                rot = new Quaternion(new float[]{-FastMath.PI/2.5f,0f,0f});
                break;
            case foot:
                offset = new Vector3f(0f,0.3f,-0.15f);
                rot = new Quaternion(new float[]{FastMath.PI/2f,0f,FastMath.PI/2f});
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
            if(n.getParent()!=null) {
                Gravity g = new Gravity(w, new Vector3f(0,30,0), new Vector3f(w.getLocalTranslation()));
                w.addController(g);
            }
            n.updateGeometricState(0f, false);
            n.updateRenderState();
        }
    }

    public static void wield(SlotNode n, DefaultNHBot b) {
        //System.err.println("Node: "+n);
        Spatial old = n.getChild("wielded");
        if(old!=null) {
            n.detachChild(old);
            n.updateGeometricState(0f, false);
            n.updateRenderState();
            n.removeSlotUI("wielded");
            //System.err.println("OLD: "+old);
            //System.err.println("DETACHED");
        }
        else {
            //System.err.println("NO WEAPON");
        }
        Item i = b.getWielded();
        if(i!=null&&i.getModel()!=null&&i.getColor()!=null) {
            final Node w = loadModel(i, "wielded");
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
            //System.err.println("ATTACHED to: "+n);
            n.attachChild(w);
            if(n.getParent()!=null) {
                //swashbuckle(n, i);
                SpinModulator spin = new SpinModulator(w, new float[]{Rand.om.nextFloat(), Rand.om.nextFloat(), Rand.om.nextFloat()},
                    mis?MIS_WEAPON_ROT:WEAPON_ROT);
                FixedTimeController swash = new FixedTimeController(spin, FixedTimeController.CONSTANT, 0.2f, 0f) {
                    protected void done() {
                        w.removeController(this);
                        //weapon.setLocalRotation(new Quaternion(DefaultNHBotNodeFactory.WEAPON_ROT));
                    }
                };
                w.addController(swash);
            }
            SlotUI sui = SlotUI.create(i.getSlotType(), b, w);
            if(sui!=null) {
                n.addSlotUI("wielded", sui);
            }
        }
        n.updateGeometricState(0f, false);
        n.updateRenderState();
    }

    public static void swashbuckle(SlotNode n, Item i) {
        boolean mis = true;
        final Node w = (Node) n.getChild("wielded");
        if(((Armament)i).getType()==Armament.Type.missile) {
            w.setLocalTranslation(new Vector3f(MIS_WEAPON_OFFSET));
            w.setLocalRotation(new Quaternion(MIS_WEAPON_ROT));
        }
        else {
            w.setLocalTranslation(new Vector3f(WEAPON_OFFSET));
            w.setLocalRotation(new Quaternion(WEAPON_ROT));
            mis = false;
        }
        Gravity g = new Gravity(w, new Vector3f(0,30,0), new Vector3f(w.getLocalTranslation()));
        w.addController(g);
        SpinModulator spin = new SpinModulator(w, new float[]{Rand.om.nextFloat(), Rand.om.nextFloat(), Rand.om.nextFloat()},
            mis?MIS_WEAPON_ROT:WEAPON_ROT);
        FixedTimeController swash = new FixedTimeController(spin, FixedTimeController.CONSTANT, 0.2f, 0f) {
            protected void done() {
                w.removeController(this);
                //weapon.setLocalRotation(new Quaternion(DefaultNHBotNodeFactory.WEAPON_ROT));
            }
        };
        w.addController(swash);
        n.updateGeometricState(0f, false);
    }
}
