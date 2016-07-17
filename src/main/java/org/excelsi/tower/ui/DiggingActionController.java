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
package org.excelsi.tower.ui;


import org.excelsi.tower.*;
import org.excelsi.aether.ui.*;
import com.jme.scene.*;
import com.jme.curve.BezierCurve;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.excelsi.aether.NHBot;
import com.jme.math.FastMath;


/**
 * Digging controller.
 */
public class DiggingActionController extends FixedTimeController implements StoppableController {
    private static final Vector3f RAISE = new Vector3f(0f, 1.5f, 0f);
    private static final Vector3f LENGTH = new Vector3f(0, 2, 0);
    private static final Quaternion CHILD_ROT = new Quaternion(new float[]{FastMath.PI/2f,0f,0f});
    private static final float[] BEGIN = new float[]{-FastMath.PI/4f, 0f, 0f};
    private static final float[] END = new float[]{FastMath.PI/3f, 0f, 0f};
    private Vector3f _oldTrans;
    private Quaternion _oldRot;
    private String _audio;
    private NHBot _b;

    public DiggingActionController(NHBot b, Object a, Node n) {
        /*
        super(new SpinModulator(createNode(NodeFactory.loadModel(((DiggingAction)a).getInstrument(), "ee")),
                    BEGIN, END),
                SLOW_TO_FAST, 0.3f);
                */
        super(new SpinModulator(getWeapon(n),
                    BEGIN, END),
                SLOW_TO_FAST, 0.4f);
        _audio = ((DiggingAction)a).getInstrument().getAudio();
        _b = b;
        setRepeatType(RT_WRAP);
        setEndDelay(0.4f);
        //setUpVector(MatrixNode.UP);
        getSpatial().setLocalTranslation(RAISE);
        Node sp = (Node) getSpatial();
        sp = (Node) sp.getChild(0);
        _oldTrans = sp.getLocalTranslation();
        _oldRot = sp.getLocalRotation();

        sp.setLocalTranslation(LENGTH);
        sp.setLocalRotation(CHILD_ROT);
        //((Node)getSpatial()).getChild(0).setLocalTranslation(LENGTH);
        //((Node)getSpatial()).getChild(0).setLocalRotation(CHILD_ROT);

        //getSpatial().setLocalTranslation(new Vector3f(0,2,0));
        getSpatial().addController(this);
        //n.attachChild(getSpatial());
        n.updateRenderState();
    }

    public void stop() {
        setRepeatType(RT_CLAMP);
    }

    protected void done() {
        super.done();
        //if(getSpatial().getParent()!=null) {
            //getSpatial().getParent().detachChild(getSpatial());
        //}
        getSpatial().setLocalTranslation(new Vector3f(DefaultNHBotNodeFactory.WEAPON_OFFSET));
        getSpatial().setLocalRotation(new Quaternion(DefaultNHBotNodeFactory.WEAPON_ROT));
        Node sp = (Node) getSpatial();
        sp = (Node) sp.getChild(0);
        sp.setLocalRotation(_oldRot);
        sp.setLocalTranslation(_oldTrans);
        Audio.getAudio().play(_b, _audio);
    }

    protected void wrapped() {
        Audio.getAudio().play(_b, _audio);
    }

    private static Spatial getWeapon(Node n) {
        //System.err.println("top: "+n);
        Node c = (Node) n.getChild(0);
        //System.err.println("c: "+c);
        c = (Node) c.getChild(0);
        //System.err.println("c: "+c);
        c = (Node) c.getChild(0);
        //System.err.println("c: "+c);
        c = (Node) c.getChild("wielded");
        //System.err.println("c: "+c);
        return c;
        //return c.getChild(0);
    }

    private static Node createNode(Node n) {
        Node p = new Node("p");
        p.attachChild(n);
        return p;
    }

    private Spatial getSpatial() {
        return ((SpinModulator)getModulators()[0]).getSpatial();
    }
}
