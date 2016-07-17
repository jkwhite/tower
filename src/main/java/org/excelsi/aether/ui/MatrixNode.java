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


import com.jme.curve.BezierCurve;
import com.jme.curve.CurveController;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import com.jme.scene.state.MaterialState;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.light.PointLight;
import com.jme.light.LightNode;
import com.jme.scene.state.LightState;
import com.jme.light.Light;
import com.jme.scene.state.RenderState;
import com.jme.math.FastMath;
//import com.jmex.sound.fmod.SoundSystem;


public class MatrixNode extends Node implements ContainerListener, NHSpaceListener, MechanicsListener {
    public static final float SCALE = Float.parseFloat(System.getProperty("tower.scale", "2.1f"));
    public static final float VERT_MULT = Float.parseFloat(System.getProperty("tower.vertmult", "1.8f"));
    public static final float HORIZ_RATIO = SCALE;
    public static final float VERT_RATIO = VERT_MULT*SCALE;
    public static final float STACK_HEIGHT = 0.30f;
    public static final Vector3f UP = new Vector3f(0,1,0);
    public static final Vector3f ZERO = new Vector3f(0,0,0);

    private Level _level;
    private View _view;
    private boolean _hide;
    private Populator _populator;
    private Node _cachedPlayer = null;
    private Map<Attack, List<StoppableController>> _attacks = new HashMap<Attack, List<StoppableController>>();
    private Map<Bot, NodeUpdater> _updaters = new HashMap<Bot, NodeUpdater>();
    private Map _model2Nodes = new HashMap();


    public MatrixNode(String name, Matrix m) {
        this(name, m, true);
    }

    public MatrixNode(String name, Matrix m, Node player) {
        this(name, m);
        if(player==null) {
            throw new IllegalArgumentException("null player");
        }
        _cachedPlayer = player;
    }

    public MatrixNode(String name, Matrix m, boolean hide) {
        super(name);
        _level = (Level) m;
        _hide = hide;
        _view = new View() {
            public void activate() {
            }
            
            public void deactivate() {
            }
            
            public void setPlayer(Node player) {
            }

            public void center(Vector3f pos) {
            }

            public String next() {
                return null;
            }

            public boolean isOverhead() {
                return false;
            }

            public void zoomIn() {
            }

            public void zoomOut() {
            }
        };
        populate();
        EventQueue.getEventQueue().addMechanicsListener(NHEnvironment.getMechanics(), this);
    }

    private int[] _locks;
    public void recordLocks(boolean unlockTransform) {
        _locks = new int[getChildren().size()];
        for(int i=0;i<_locks.length;i++) {
            Spatial s = (Spatial) getChildren().get(i);
            _locks[i] = s.getLocks();
            if(unlockTransform) {
                s.unlockTransforms();
            }
        }
    }

    public void restoreLocks() {
        for(int i=0;i<_locks.length;i++) {
            ((Spatial)getChildren().get(i)).setLocks(_locks[i]);
        }
    }

    public Level getLevel() {
        return _level;
    }

    public long getCachedNodeCount() {
        return _model2Nodes.size();
    }

    /*
    private float _lastUpdate = 0f;
    public void updateWorldData(float dt) {
        super.updateWorldData(dt);
        if(_populator!=null) {
            _lastUpdate += dt;
            if(_lastUpdate>0.5f) {
                if(!_populator.step()) {
                    _populator = null;
                }
            }
        }
    }
    */

    private static final Vector3f ITEM_ADD = new Vector3f(0f, 20f, 0f);
    public void itemAdded(Container space, Item it, int idx, boolean incremented) {
        addItem(space, it, idx, ITEM_ADD, incremented, null, null);
    }

    private static final Vector3f ITEM_ADD2 = new Vector3f(0f, 30f, 0f);
    public void itemAdded(Container space, Item it, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        addItem(space, it, idx, ITEM_ADD2, incremented, adder, origin);
    }

    private static final Vector3f ITEM_DROP = new Vector3f(0f, 24f, 0f);
    public void itemDropped(Container space, Item it, int idx, boolean incremented) {
        addItem(space, it, idx, ITEM_DROP, incremented, null, null);
    }

    private void addItem(Container container, Item it, int idx, Vector3f vel, boolean incremented, NHBot adder, NHSpace origin) {
        NHSpace space = (NHSpace) container;
        final NHSpaceNode n = (NHSpaceNode) getNode(space);
        final Node item = NodeFactory.loadModel(it, "loot"+idx);
        float rot = (Rand.om.nextFloat()-0.5f)*4f*(float)Math.PI;
        if(idx==0) {
            rot = (Rand.om.nextFloat()-0.5f)*(float)Math.PI/2f; // be relatively clear about single items
        }
        item.setLocalRotation(new Quaternion(new float[]{(float)Math.PI, rot, (float)Math.PI}));
        Runnable r = null;
        if(adder!=null&&adder.isDead()) {
            r = new Runnable() {
                public void run() {
                    n.attachItem(item);
                    n.updateRenderState();
                }
            };
        }
        else {
            n.attachItem(item);
            n.updateRenderState();
        }
        NHSpace start;
        String audio = null;
        if(origin!=null) {
            start = origin;
            audio = "thrown";
        }
        else if(adder!=null&&!adder.isDead()) {
            start = adder.getEnvironment().getMSpace();
            audio = "thrown";
        }
        else {
            start = (NHSpace) container;
            audio = "dropped";
        }

        if(n.getParent()!=null) {
            addGravity(it, idx, item, start, (NHSpace) container, vel.y, incremented, audio, adder!=null&&adder.isDead()?0.25f:0f, r, n);
        }
        else {
            if(r!=null) {
                r.run();
            }
            item.setLocalTranslation(new Vector3f(tiny(), (idx-space.getModifiedDepth())*STACK_HEIGHT, tiny()));
        }
        updateOccupant(space);
    }

    private void addGravity(Item i, int idx, final Node item, NHSpace start, final NHSpace end, float upVel, boolean detachOnDone, final String audio) {
        addGravity(i, idx, item, start, end, upVel, detachOnDone, audio, 0f, null, null);
    }

    private void addGravity(Item i, int idx, final Node item, NHSpace start, final NHSpace end, float upVel, boolean detachOnDone, final String audio, float delay, Runnable r, Node parent) {
        Vector3f vel = new Vector3f(0f, upVel, 0f);
        //final float MULT = 2.8f+Rand.om.nextFloat()/2f;

        final float MULT = 1.3f+Rand.om.nextFloat()/3f;
        Vector3f init = getTranslation(start).subtract(getTranslation(end));
        //init.addLocal(new Vector3f(0, (5+idx)*STACK_HEIGHT, 0));
        init.y += (5+idx)*STACK_HEIGHT;
        vel.x = MULT*SCALE*-init.x;
        vel.z = MULT*SCALE*-init.z;
        item.setLocalTranslation(init);
        Gravity g;
        if(!detachOnDone) {
            g = new Gravity(item, vel, new Vector3f(tiny(), (idx-end.getModifiedDepth())*STACK_HEIGHT, tiny()), -120f, delay, r) {
                protected void done() {
                    if(audio!=null) {
                        Audio.getAudio().play(end, audio);
                    }
                }
            };
        }
        else {
            g = new Gravity(item, vel, new Vector3f(0, (idx-end.getModifiedDepth())*STACK_HEIGHT, 0), -120f, delay, r) {
                protected void done() {
                    item.getParent().detachChild(item);
                    if(audio!=null) {
                        Audio.getAudio().play(end, audio);
                    }
                }
            };
        }
        //item.addController(g);
        if(parent!=null) {
            parent.addController(g);
        }
        else {
            item.addController(g);
        }
    }

    private static float tiny() {
        return (Rand.om.nextFloat()-0.5f)/4f;
    }

    public void itemTaken(Container space, Item item, int idx) {
        removeItem(space, item, idx, false);
    }

    public void itemDestroyed(Container space, Item item, int idx) {
        removeItem(space, item, idx, true);
    }

    public void itemsDestroyed(Container container, Item[] items) {
        NHSpace space = (NHSpace) container;
        final NHSpaceNode n = (NHSpaceNode) getNode(space);
        for(int i=0;i<items.length;i++) {
            final Spatial doomed = n.removeItem(0);
            if(doomed==null) {
                throw new IllegalStateException("no child named loot0, children are "+n.getChildren());
            }
            doomed.setName("doomed");
            if(n.getParent()!=null) {
                n.addController(new SlideInOutController(doomed, doomed.getLocalTranslation(),
                            doomed.getLocalTranslation().add(new Vector3f(Rand.om.nextInt(40)-20, Rand.om.nextInt(40)-20, Rand.om.nextInt(40)-20).multLocal(0.2f)),
                            SlideInOutController.FAST_TO_SLOW, 0.3f) {
                        protected void done() {
                            n.detachChild(doomed);
                        }
                });
            }
            else {
                n.detachChild(doomed);
            }
        }
    }

    private static final Vector3f DOOMED = new Vector3f(0f, 3f, 0f);
    private static final Vector3f LOWER = new Vector3f(0, -STACK_HEIGHT, 0);
    private void removeItem(Container container, Item item, final int idx, boolean destroyed) {
        NHSpace space = (NHSpace) container;
        final NHSpaceNode n = (NHSpaceNode) getNode(space);
        if(destroyed) {
            final Spatial doomed = n.removeItem(idx);
            if(doomed==null) {
                Logger.global.severe("no child named loot"+idx+", children are "+n.getChildren());
                return;
            }
            doomed.setName("doomed");
            if(n.getParent()!=null) {
                n.addController(new SlideInOutController(doomed, doomed.getLocalTranslation(),
                            doomed.getLocalTranslation().add(new Vector3f(Rand.om.nextInt(40)-20, Rand.om.nextInt(40)-20, Rand.om.nextInt(40)-20).multLocal(0.2f)),
                            SlideInOutController.FAST_TO_SLOW, 0.3f) {
                        protected void done() {
                            n.detachChild(doomed);
                        }
                });
            }
            else {
                n.detachChild(doomed);
            }
        }
        else {
            final Spatial doomed = n.removeItem(idx);
            if(doomed==null) {
                Logger.global.severe("no child named loot"+idx+", children are "+n.getChildren());
                return;
            }
            doomed.setName("doomed");
            if(n.getParent()!=null) {
                n.addController(new SlideInOutController(doomed, doomed.getLocalTranslation(),
                            doomed.getLocalTranslation().add(DOOMED),
                            SlideInOutController.SLOW_TO_FAST, 0.3f) {
                        protected void done() {
                            n.detachChild(doomed);
                        }
                });
            }
            else {
                n.detachChild(doomed);
            }
        }
        List<Spatial> items = n.getItems();
        if(items!=null) {
            for(int i=idx+1;i<items.size();i++) {
                Spatial c = items.get(i);
                List conts = c.getControllers();
                for(int j=0;j<conts.size();j++) {
                    if(conts.get(j) instanceof SlideInOutController) {
                        c.removeController((Controller)conts.get(j));
                        j--;
                    }
                }
                //Vector3f end = c.getLocalTranslation().add(new Vector3f(0, -STACK_HEIGHT, 0));
                Vector3f end = c.getLocalTranslation().add(LOWER);
                if(n.getParent()!=null) {
                    c.addController(new SlideInOutController(c, c.getLocalTranslation(), end,
                        SlideInOutController.FAST_TO_SLOW, 0.5f));
                }
                else {
                    c.setLocalTranslation(end);
                }
            }
        }
        n.updateRenderState();
        updateOccupant(space);
    }

    private void updateOccupant(NHSpace space) {
        Bot occ = space.getOccupant();
        if(occ!=null) {
            MoveNode move = (MoveNode) getNode(occ);
            if(move!=null) {
                Vector3f loc = move.getTarget();
                if(loc==null) {
                    loc = move.getLocalTranslation();
                }
                try {
                    move.setTarget(new Vector3f(loc.x, -space.getOccupantDepth()*STACK_HEIGHT, loc.z), null);
                }
                catch(IllegalStateException e) {
                    // TODO: because bot can move while ui update is occurring
                }
            }
        }
    }

    private void populate() {
        Level m = _level;
        boolean hide = _hide;
        try {
            Populator p = new Populator(m);
            _populator = p;

            if(true) {
                while(p.step());
            }
            else {
                _populator = p;
            }
            EventQueue.getEventQueue().addMatrixListener(m, p);
            updateRenderState();
            updateGeometricState(0f, true);
        }
        catch(IllegalStateException e) {
            // player removed during update
        }
    }

    protected void detachNodeAt(int i, int j) {
        NHSpace s = _level.getSpace(i, j);
        if(s != null) {
            Node n = getNode(s);
            detachChild(n);
        }
    }

    class Populator implements MatrixListener {
        private int _i = 0;
        private int _j = 0;
        private Level _m;
        private MatrixNode _from;
        private Set _visible;

        public Populator(Level m) {
            _m = m;
            _visible = getPlayer().getEnvironment().getKnown();
        }

        public boolean step() {
            if(_i>=_m.width()) {
                throw new IllegalStateException("populator is overdone");
            }
            if(_j>=_m.height()) {
                _j = 0;
                if(++_i>=_m.width()) {
                    addPlayer();
                    addBots();
                    return false;
                }
            }
            NHSpace s = (NHSpace) _m.getSpace(_i, _j);
            if(s != null) {
                EventQueue.getEventQueue().addContainerListener(s, (ContainerListener) MatrixNode.this);
                Node n = createNode(s);
                if(!_hide || _visible.contains(s)) {
                    attachChild(n);
                }
            }
            _j++;
            return true;
        }

        public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
            Set vis = new HashSet();
            getPlayer().getEnvironment().getMSpace().visible(new HashSet(), vis, new HashSet<NHSpace>(), getPlayer().getVision());
            for(MSpace s:spaces) {
                NHSpace sp = (NHSpace)s;
                EventQueue.getEventQueue().addContainerListener(sp, (ContainerListener) MatrixNode.this);
                final Node n = createNode(sp);
                if(vis.contains(sp)) {
                    attachChild(n);
                    if(sp.isOccupied()) {
                        updateOccupant(sp);
                    }
                    final int locks = n.getLocks();
                    n.unlockTransforms();
                    n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                new ScaleModulator(n, true)}, FixedTimeController.FAST_TO_SLOW, 0.7f) {
                            protected void done() {
                                n.removeController(this);
                                n.setLocks(locks);
                            }
                    });
                }
            }
            updateRenderState();
        }

        public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
            for(MSpace s:spaces) {
                try {
                    EventQueue.getEventQueue().removeContainerListener((NHSpace)s, (ContainerListener) MatrixNode.this);
                    EventQueue.getEventQueue().removeMSpaceListener(s, (NHSpaceListener) MatrixNode.this);
                }
                catch(IllegalArgumentException e) {
                    Logger.global.severe(e.toString());
                }
                final Node n = getNode(s);
                if(n!=null) {
                    _model2Nodes.remove(s);
                    n.unlockTransforms();
                    /*
                    n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                new ScaleModulator(n, false)}, FixedTimeController.FAST_TO_SLOW, 0.7f) {
                            protected void done() {
                                detachChild(n);
                            }
                    });
                    */
                    Vector3f vel = new Vector3f(n.getLocalTranslation());
                    Node bot = getNode(b);
                    if(bot!=null) {
                        vel.set(n.getLocalTranslation().subtract(bot.getLocalTranslation()));
                        vel.multLocal(3f);
                        vel.addLocal(n.getLocalTranslation());
                        vel = n.getLocalTranslation().add(n.getLocalTranslation().subtract(bot.getLocalTranslation()));
                    }
                    /*
                    n.addController(new SlideInOutController(n, new Vector3f(n.getLocalTranslation()), vel,
                        SlideInOutController.FAST_TO_SLOW, 0.3f) {
                            protected void done() {
                                detachChild(n);
                            }
                        });
                    */
                    n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                        new SlideInOutController.SlideModulator(n, new Vector3f(n.getLocalTranslation()), vel),
                        new SpinModulator(n, new float[]{0f,0f,0f}, new float[]{Rand.om.nextFloat(),Rand.om.nextFloat(),Rand.om.nextFloat()})},
                        FixedTimeController.FAST_TO_SLOW, 0.3f) {
                            protected void done() {
                                detachChild(n);
                            }
                        });
                    /*
                    n.addController(new SlideInOutController(n, new Vector3f(n.getLocalTranslation()), n.getLocalTranslation().add(new Vector3f(0, -Rand.om.nextInt(20)-5, 0)),
                        SlideInOutController.SLOW_TO_FAST, 0.8f) {
                            protected void done() {
                                detachChild(n);
                            }
                        });
                        */
                }
                //Audio.getAudio().play((NHSpace)s, "hit_crushing");
            }
            updateGeometricState(0f, true);
        }

        public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
            if("light".equals(attr)) {
                LightState ls = (LightState) getParent().getParent().getRenderState(LightState.RS_LIGHT);
                Light light = ls.get(0);
                float val = ((Float)newValue).floatValue();
                val *= 4f;
                //light.setDiffuse(new ColorRGBA(0.3f*val, 0.3f*val, 0.3f*val, 1.0f));
                //light.setSpecular(new ColorRGBA(0.2f*val, 0.2f*val, 0.2f*val, 1.0f));
                light.setDiffuse(new ColorRGBA(0.05f*val, 0.05f*val, 0.05f*val, 1.0f));
                light.setSpecular(new ColorRGBA(0.1f*val, 0.1f*val, 0.1f*val, 1.0f));
                getParent().updateRenderState();
            }
        }
    }

    private Node createNode(NHSpace s) {
        NodeFactory nf = getFactory(s);
        if(_model2Nodes.containsKey(s)) {
            throw new IllegalStateException("space "+s+" already in matrix");
        }
        Node n = nf.createNode("n", s, MatrixNode.this);
        putNode(s, n);
        n.setLocalTranslation(getTranslation((NHSpace)s));
        //System.err.println("TRANS: "+n.getLocalTranslation());
        nf.lock(n);
        if(s.numItems()>0) {
            Item[] its = s.getItem();
            for(int i=0;i<its.length;i++) {
                itemDropped(s, its[i], i, false);
            }
        }
        for(Parasite p:s.getParasites()) {
            parasiteAdded(s, p);
        }
        EventQueue.getEventQueue().addMSpaceListener(s, (NHSpaceListener)this);
        return n;
    }

    public void occupied(MSpace m, Bot b) {
        addBot((MatrixEnvironment)b.getEnvironment());
    }

    public void unoccupied(MSpace m, Bot b) {
        if(_updaters==null) {
            return; // in the middle of level switch
        }
        NHBot nb = (NHBot) b;
        NodeUpdater nu = (NodeUpdater) _updaters.get(b);
        if(nu!=null) {
            EventQueue.getEventQueue().removeNHEnvironmentListener(nb, nu);
            _updaters.remove(b);
        }
        if(!nb.isDead()) {
            Node n = getNode(b);
            if(n!=null) {
                if(nb.isPlayer()) {
                    _cachedPlayer = n;
                }
                detachChild(n);
            }
        }
    }

    public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
    }

    private Node _overlay = null;
    private OverlayListener _olistener = new OverlayListener() {
        public void overlayMoved(Overlay o, NHSpace from, NHSpace to) {
            _overlay.addController(new SlideInOutController(_overlay, new Vector3f(_overlay.getLocalTranslation()),
                getTranslation(to), SlideInOutController.FAST_TO_SLOW, 0.2f) {
                    protected void done() {
                        _overlay.removeController(this);
                    }
                });
        }

        public void overlayRemoved(Overlay o) {
            EventQueue.getEventQueue().removeOverlayListener(o, _olistener);
            detachChild(_overlay);
            if(_oldState!=null) {
                setRenderState(_oldState);
                updateRenderState();
            }
            //System.err.println("RESTORED STATE: "+_oldState);
            _overlay = null;
        }
    };

    private LightState _oldState;
    public void overlayAdded(NHSpace s, Overlay o) {
        if(_overlay==null) {
            _overlay = new Node("o");
            Box box = new Box("overlay", new Vector3f(0.0f, -0.3f, 0.0f), HORIZ_RATIO-0.2f, 0.2f, (VERT_RATIO-0.2f)/2f);
            MaterialState m = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            m.setEnabled(true);
            m.setDiffuse(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            //m.setDiffuse(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
            m.setSpecular(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));
            m.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
            m.setShininess(0.8f);
            //m.setAlpha(0.7f);
            box.setRenderState(m);
            /* the goggles--they do .. something?!
            {
                _oldState = (LightState) getRenderState(RenderState.RS_LIGHT);
                //System.err.println("SAVED OLD STATE: "+_oldState);
                LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
                ls.setTwoSidedLighting(true);
                final PointLight p = new PointLight();
                p.setEnabled(true);
                p.setDiffuse(new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f));
                p.setSpecular(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
                p.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
                p.setAttenuate(true);
                p.setQuadratic(0.9f);
                p.setLinear(0.9f);
                p.setConstant(0f);
                LightNode ln = new LightNode("light", ls);
                //ln.setLocalTranslation(new Vector3f(1.0f, 3.0f, 2.5f));
                ln.setLocalTranslation(new Vector3f(0.0f, 2.0f, 0.0f));
                ln.setLight(p);
                _overlay.setRenderState(ls);

                ln.setTarget(this);
                _overlay.attachChild(ln);
            }
            */
            _overlay.setLocalTranslation(getTranslation(s));
            _overlay.attachChild(box);
            attachChild(_overlay);
            updateGeometricState(0f, true);
            updateRenderState();
            EventQueue.getEventQueue().addOverlayListener(o, _olistener);
        }
    }

    public void overlayRemoved(NHSpace s, Overlay o) {
        //setRenderState(_oldState);
        //System.err.println("RESTORED STATE: "+_oldState);
    }

    public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
        Set vis = new HashSet();
        getPlayer().getEnvironment().getMSpace().visible(new HashSet(), vis, new HashSet<NHSpace>(), getPlayer().getVision());
        if("open".equals(attr)||"model".equals(attr)||"color".equals(attr)||"altitude".equals(attr)) {
            Node old = getNode(s);
            if(old!=null) {
                detachChild(old);
                removeNode(s);
                EventQueue.getEventQueue().removeContainerListener(s, (ContainerListener) this);
                EventQueue.getEventQueue().removeMSpaceListener(s, this);
            }
            if(vis.contains(s)||getPlayer().getEnvironment().getKnown().contains(s)) {
                EventQueue.getEventQueue().addContainerListener(s, (ContainerListener) this);
                Node n = createNode(s);
                attachChild(n);
                n.updateRenderState();
            }
            if("open".equals(attr)) {
                Audio.getAudio().play(s, Boolean.TRUE.equals(newValue)?"open":"close");
            }
        }
    }

    public void parasiteAdded(NHSpace s, Parasite p) {
        if(!p.isHidden()) {
            NHSpaceNode n = (NHSpaceNode) getNode(s);
            if(n!=null) {
                NodeFactory nf = NodeFactory.getFactory(p);
                if(nf!=null) {
                    Node c = nf.createNode("p", p, n);
                    n.attachParasite(p, c);
                    n.updateRenderState();
                }
            }
        }
    }

    public void parasiteRemoved(NHSpace s, Parasite p) {
        NHSpaceNode n = (NHSpaceNode) getNode(s);
        if(n!=null) {
            Spatial sp = n.removeParasite(p);
            if(sp!=null) {
                n.detachChild(sp);
            }
        }
    }

    public void parasiteMoved(NHSpace s, NHSpace to, Parasite p) {
        NHSpaceNode n = (NHSpaceNode) getNode(s);
        if(n!=null) {
            Spatial sp = n.removeParasite(p);
            if(sp!=null) {
                n.detachChild(sp);
            }
            else {
                parasiteAdded(to, p);
                return;
            }
            NHSpaceNode t = (NHSpaceNode) getNode(to);
            if(t!=null) {
                t.attachParasite(p, sp);
                final Spatial spa = sp;
                sp.setLocalTranslation(n.getLocalTranslation().subtract(t.getLocalTranslation()));
                final FixedTimeController c = new SlideInOutController(sp,
                    n.getLocalTranslation().subtract(t.getLocalTranslation()),
                    ZERO, FixedTimeController.FAST_TO_SLOW, 0.5f, 0.1f) {
                    protected void done() {
                        spa.removeController(this);
                    } };
                sp.addController(c);
            }
        }
        Audio.getAudio().play(s, "slide");
    }

    public void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue) {
        if(attr.equals("hidden")) {
            parasiteAdded(s, p);
        }
        else if("color".equals(attr)) {
            // replace without detaching
            NHSpaceNode n = (NHSpaceNode) getNode(s);
            if(n!=null) {
                NodeFactory nf = NodeFactory.getFactory(p);
                Spatial ps = n.getParasite(p);
                if(ps!=null) {
                    nf.updateColor((Node)ps, p);
                }
            }
        }
    }

    public void reveal() {
        MatrixEnvironment[] n = _level.getBots();
        List<Bot> bots = new ArrayList<Bot>();
        for(MatrixEnvironment me:n) {
            bots.add(me.getBot());
        }
        for(Iterator i=_updaters.values().iterator();i.hasNext();) {
            NodeUpdater nu = (NodeUpdater) i.next();
            nu.noticed(null, bots);
        }
    }

    public Vector3f getTranslation(NHSpace s) {
        MatrixMSpace m = (MatrixMSpace) s;
        //return new Vector3f(HORIZ_RATIO*m.getI(), -MatrixNode.STACK_HEIGHT*s.getDepth(), VERT_RATIO*m.getJ());
        //return new Vector3f(HORIZ_RATIO*m.getI(), 0, VERT_RATIO*m.getJ());
        return new Vector3f(HORIZ_RATIO*m.getI(), MatrixNode.STACK_HEIGHT*s.getAltitude(), VERT_RATIO*m.getJ());
    }

    public void addBots() {
        MatrixEnvironment[] bots = _level.getBots();
        for(int i=0;i<bots.length;i++) {
            if(!(bots[i].getBot() instanceof Patsy)) {
                addBot(bots[i]);
            }
        }
        NHEnvironment b = getPlayer().getEnvironment();
        List<Bot> n = new ArrayList<Bot>(b.getVisibleBots());
        for(Iterator i=_updaters.values().iterator();i.hasNext();) {
            NodeUpdater nu = (NodeUpdater) i.next();
            nu.noticed(null, n);
        }
    }

    public void addPlayer() {
        MatrixEnvironment[] bots = _level.getBots();
        for(int i=0;i<bots.length;i++) {
            if(bots[i].getBot() instanceof Patsy) {
                addBot(bots[i]);
            }
        }
    }

    private NHBot _cached;
    public NHBot getPlayer() {
        if(_cached!=null) {
            return _cached;
        }
        else {
            NHBot b = _level.getPlayer();
            if(b==null) {
                throw new IllegalStateException("no player");
            }
            _cached = b;
        }
        return _cached;
    }

    public Vector3f getPlayerTranslation() {
        return getTranslation(getPlayer().getEnvironment().getMSpace());
    }

    public void addBot(MatrixEnvironment me) {
        Bot b = me.getBot();
        if(_updaters.containsKey(b)) {
            //throw new IllegalArgumentException("already added bot "+b);
            Logger.global.warning("already added bot "+b);
            return;
        }
        attachBot(b);
        NodeUpdater nu = null;
        if(((NHBot)b).isPlayer()) {
            nu = new PlayerUpdater((NHEnvironment)me);
        }
        else {
            nu = new NodeUpdater((NHEnvironment)me);
        }
        _updaters.put(b, nu);
        EventQueue.getEventQueue().addNHEnvironmentListener((NHBot)b, nu);
    }

    protected void attachBot(Bot b) {
        NHBot nb = (NHBot) b;
        MatrixEnvironment me = (MatrixEnvironment) b.getEnvironment();
        Node mn;
        if(((NHBot)b).isPlayer()&&_cachedPlayer!=null) {
            mn = _cachedPlayer;
        }
        else {
            NodeFactory nf = getFactory(b);
            Node n = nf.createNode("b", b, this);
            mn = new MoveNode((NHBot)b);
            Node combatHit = new Node("combatHit");
            combatHit.attachChild(n);
            Node combatMiss = new Node("combatMiss");
            combatMiss.attachChild(combatHit);
            mn.attachChild(combatMiss);
        }
        mn.setLocalTranslation(getTranslation((NHSpace)me.getMSpace()));
        try {
            mn.getLocalTranslation().y -= ((NHSpace)me.getMSpace()).getOccupantDepth()*STACK_HEIGHT;
        }
        catch(IllegalStateException e) {
            // bot has moved on
        }
        mn.updateGeometricState(0f, false);
        attachChild(mn);
        if(!nb.isPlayer()) {
            //if(!nb.getEnvironment().getMSpace().visibleFrom(getPlayer().getEnvironment().getMSpace())) {
            //if(!nb.getEnvironment().getVisible().contains(getPlayer().getEnvironment().getMSpace())) {
            if(!getPlayer().getEnvironment().getVisible().contains(nb.getEnvironment().getMSpace())||getPlayer().isBlind()) {
                detachChild(mn);
            }
            else {
                final Node n = mn;
                final int locks = n.getLocks();
                n.unlockTransforms();
                n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                            new ScaleModulator(n, true)}, FixedTimeController.FAST_TO_SLOW, 0.7f) {
                        protected void done() {
                            n.removeController(this);
                            n.setLocks(locks);
                        }
                });
                mn.updateRenderState();
            }
        }
        else {
            //_cachedPlayer = mn;
            _view.setPlayer(mn);
        }
        putNode(b, mn);
    }

    public void deactivate() {
        EventQueue.getEventQueue().removeLevelListeners();
    }

    public void free() {
        _level = null;
        _view = null;
        _populator = null;
        _cachedPlayer = null;
        _updaters = null;
        _attacks = null;
        if(_model2Nodes!=null) {
            _model2Nodes.clear();
            _model2Nodes = null;
        }
        // recover from mem leak in JME
        for(Spatial s:getChildren()) {
            freeChild(s);
        }
        detachAllChildren();
        /*
        for(Map.Entry e:_updaters.entrySet()) {
            EventQueue.getEventQueue().removeNHEnvironmentListener((NHBot)e.getKey(), (NHEnvironmentListener)e.getValue());
        }
        EventQueue.getEventQueue().removeMechanicsListener(NHEnvironment.getMechanics(), this);
        EventQueue.getEventQueue().removeMatrixListener(_level, _populator);
        for(MSpace space:_level.spaces()) {
            if(space!=null) {
                EventQueue.getEventQueue().removeContainerListener((NHSpace)space, this);
                EventQueue.getEventQueue().removeMSpaceListener(space, this);
            }
        }
        */
        //System.err.println("DEAC: "+EventQueue.getEventQueue());
    }

    private static final Quaternion QNULL = new Quaternion(new float[]{0f,0f,0f});
    private static final Vector3f NULL = ZERO;
    private static final TriMesh TNULL = new TriMesh("");
    public static void freeChild(Spatial s) {
        s.setLocalRotation(QNULL);
        s.setLocalScale(NULL);
        s.setLocalTranslation(NULL);
        s.clearRenderState(RenderState.RS_ALPHA);
        s.clearRenderState(RenderState.RS_LIGHT);
        s.clearRenderState(RenderState.RS_MATERIAL);
        s.clearRenderState(RenderState.RS_FOG);
        s.clearRenderState(RenderState.RS_SHADE);
        s.clearRenderState(RenderState.RS_ALPHA);
        s.clearRenderState(RenderState.RS_CULL);
        s.clearRenderState(RenderState.RS_TEXTURE);
        s.clearRenderState(RenderState.RS_ZBUFFER);
        if(s instanceof SharedMesh) {
            SharedMesh sm = (SharedMesh) s;
            //TriMesh targ = sm.getTarget();
            sm.setTarget(TNULL);
        }
        else if(s instanceof NHDiscreteLodNode) {
            ((NHDiscreteLodNode)s).setActiveChild(-1);
        }
        if(s instanceof Node && ((Node)s).getChildren()!=null) {
            for(Spatial c:((Node)s).getChildren()) {
                freeChild(c);
            }
            ((Node)s).detachAllChildren();
        }
    }

    public void activate() {
        for(Map.Entry e:_updaters.entrySet()) {
            EventQueue.getEventQueue().addNHEnvironmentListener((NHBot)e.getKey(), (NHEnvironmentListener)e.getValue());
            ((NodeUpdater)e.getValue()).activate((Bot)e.getKey());
        }
        EventQueue.getEventQueue().addMechanicsListener(NHEnvironment.getMechanics(), this);
        EventQueue.getEventQueue().addMatrixListener(_level, _populator);
        for(MSpace space:_level.spaces()) {
            if(space!=null) {
                EventQueue.getEventQueue().addContainerListener((NHSpace)space, this);
                EventQueue.getEventQueue().addMSpaceListener(space, this);
            }
        }
    }

    public Node getPlayerNode() {
        if(_cachedPlayer!=null) {
            return _cachedPlayer;
        }
        else {
            return (Node) getChild("player");
            //return getNode(getPlayer());
        }
    }

    public void setView(View view) {
        _view = view;
        for(Iterator i=_updaters.values().iterator();i.hasNext();) {
            NodeUpdater nu = (NodeUpdater) i.next();
            nu.switchedView(view);
            nu.update();
        }
        if(getParent()!=null&&getParent().getParent()!=null) {
            _populator.attributeChanged(_level, "light", null, new Float(_level.getLight()));
        }
        updateRenderState();
    }

    private static final Vector3f ATTACK_STARTED = new Vector3f(0f, 0f, 1.3f).multLocal(2.5f);
    private static final BezierCurve A_S = new BezierCurve("attack", new Vector3f[]{
                        ZERO, ATTACK_STARTED, ZERO});
    public static final BezierCurve W_S = new BezierCurve("w-attack", new Vector3f[]{
                new Vector3f(DefaultNHBotNodeFactory.WEAPON_OFFSET), DefaultNHBotNodeFactory.WEAPON_OFFSET.add(new Vector3f(0f,0f,3f)),
                new Vector3f(DefaultNHBotNodeFactory.WEAPON_OFFSET)});
    public static final BezierCurve M_W_S = new BezierCurve("w-attack", new Vector3f[]{
                new Vector3f(DefaultNHBotNodeFactory.MIS_WEAPON_OFFSET), DefaultNHBotNodeFactory.WEAPON_OFFSET.add(new Vector3f(0f,0f,3f)),
                new Vector3f(DefaultNHBotNodeFactory.MIS_WEAPON_OFFSET)});
    public void attackStarted(Mechanics m, final Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
        Set<Bot> vis = getPlayer().getEnvironment().getVisibleBots();
        if(attacker!=null) {
            MoveNode mn = (MoveNode) getNode(attacker);
            if(mn!=null) {
                mn.unidle();
            }
        }
        if(defender!=null) {
            MoveNode mn = (MoveNode) getNode(defender);
            if(mn!=null) {
                mn.unidle();
            }
        }
        if(vis.contains(attacker)) {
            Spatial n = getAttackSpatial(attacker);
            if(n!=null) {
                //Vector3f mid = new Vector3f(0f, 0f, 1.3f).multLocal(2.5f);
                Vector3f mid = ATTACK_STARTED;
                Vector3f loc = n.getLocalTranslation();
                /*
                BezierCurve c = new BezierCurve("attack", new Vector3f[]{
                        new Vector3f(0, 0, 0), new Vector3f(mid),
                        new Vector3f(0, 0, 0)});
                        */
                CurveController cc = new RemovingCurveController(A_S, n);
                cc.setSpeed(5);
                cc.setUpVector(UP);
                n.addController(cc);

                // animate weapon
                Node w = (Node) ((Node)n).getChild(0);
                final Spatial weapon = w.getChild("wielded");
                if(weapon!=null) {
                    if(attack.getType()==Attack.Type.melee) {
                        CurveController cw = new RemovingCurveController(attack.getWeapon().getType()==Armament.Type.melee?W_S:M_W_S, weapon) {
                            protected void done() {
                                super.done();
                                weapon.setLocalRotation(new Quaternion(DefaultNHBotNodeFactory.WEAPON_ROT));
                            }
                        };
                        cw.setSpeed(5);
                        cw.setAutoRotation(true);
                        //cw.setUpVector(weapon.getLocalRotation().getRotationColumn(0));
                        weapon.addController(cw);
                    }
                    SpinModulator spin = new SpinModulator(weapon, DefaultNHBotNodeFactory.WEAPON_ROT,
                        new float[]{Rand.om.nextFloat(), Rand.om.nextFloat(), Rand.om.nextFloat()});
                    FixedTimeController slash = new FixedTimeController(spin, FixedTimeController.CONSTANT, 0.2f, 0f) {
                        protected void done() {
                            weapon.removeController(this);
                            weapon.setLocalRotation(new Quaternion(attack.getWeapon().getType()==Armament.Type.melee?DefaultNHBotNodeFactory.WEAPON_ROT:DefaultNHBotNodeFactory.MIS_WEAPON_ROT));
                        }
                    };
                    weapon.addController(slash);
                }
            }
        }
        {
            if(attack.getType()==Attack.Type.bolt||attack.getType()==Attack.Type.ball) {
                List<StoppableController> contrs = _attacks.get(attack);
                if(contrs==null) {
                    contrs = new ArrayList<StoppableController>();
                    _attacks.put(attack, contrs);
                }
                float delay = 0;
                boolean first = true;
                Direction dir = attacker.getEnvironment()!=null?attacker.getEnvironment().getFacing():Direction.north;
                Armament arm = attack.getWeapon();
                for(int i=attack.getType()==Attack.Type.bolt?1:0;i<path.length;i++) {
                    NHSpace s = path[i];
                    if(i<path.length-1) {
                        NHSpace next = path[i+1];
                        dir = s.directionTo(next);
                    }
                    Node parent = getNode(s);
                    if(parent==null||parent.getParent()==null) {
                        // no known node for this space or space is not yet known by player,
                        // skip it.
                        continue;
                    }
                    final Node n = NodeFactory.loadModel(arm.getModel(), arm.getColor(), 0f);
                    n.setLocalScale(new Vector3f(1f, 1f, 0f));
                    if(attack.getType()==Attack.Type.bolt) {
                        float rotx = 0f;
                        float rotz = 0f;
                        switch(dir) {
                            case north:
                            case south:
                                rotx = (float) Math.PI/2f;
                                rotz = (float)Math.PI/2f;
                                break;
                            case east:
                            case west:
                                break;
                            case northeast:
                            case southwest:
                                rotx = (float) Math.PI/2f;
                                rotz = (float)Math.PI/4f;
                                break;
                            case northwest:
                            case southeast:
                                rotx = (float) Math.PI/2f;
                                rotz = 3f*(float)Math.PI/4f;
                                break;
                        }
                        n.setLocalRotation(new Quaternion(new float[]{rotx, (float)Math.PI, rotz}));
                    }
                    n.updateGeometricState(0f, false);
                    parent.attachChild(n);
                    parent.updateRenderState();
                    BoltController bc = new BoltController(n, delay, false);
                    contrs.add(bc);
                    n.addController(bc);
                    delay += 0.05f;
                }
                if(attack.getType()==Attack.Type.bolt) {
                    Audio.getAudio().play(path[path.length-1], attack.getWeapon().getAudio());
                }
            }
        }
    }

    public void attackEnded(Mechanics m, Attack a, NHBot attacker, NHBot defender, Outcome outcome) {
        Set<Bot> vis = getPlayer().getEnvironment().getVisibleBots();
        {
            if(a.getType()==Attack.Type.bolt||(a.getType()==Attack.Type.ball&&outcome==null)) {
                List<StoppableController> contrs = _attacks.get(a);
                if(contrs!=null) {
                    for(StoppableController c:contrs) {
                        c.stop();
                    }
                    _attacks.remove(a);
                }
                else {
                    //System.err.println("*** NO CONTROLLER FOR: "+a);
                }
            }
        }
        if(outcome==null) {
            return;
        }
        if(outcome.getResult()==Outcome.Result.hit||outcome.getResult()==Outcome.Result.intercept) {
            String audio = a.getWeapon().getAudio();
            if(outcome.getResult()==Outcome.Result.intercept) {
                audio = "intercept";
            }
            else if(outcome.isBlocked()) {
                audio = "block";
            }
            if(audio!=null) {
                NHBot source = attacker;
                if(defender.isPlayer()||source.getEnvironment()==null) {
                    source = defender;
                }
                Audio.getAudio().play(source, audio);
            }
            if(outcome.getAttack().getType()==Attack.Type.missile) {
                Armament w = outcome.getAttack().getWeapon();
                Item it = w.toItem();
                if(it!=null) {
                    Node item = NodeFactory.loadModel(it, "projectile");
                    NHSpace start = outcome.getStartSpace();
                    NHSpace end = outcome.getEndSpace();
                    Node n = getNode(end);
                    if(n!=null) {
                        n.attachChild(item);
                        n.updateRenderState();
                        addGravity(it, 0, item, start, end, 30f, true, "thrown");
                    }
                }
            }
            else if(outcome.getAttack().getType()==Attack.Type.melee) {
                NHBot def = outcome.getDefender();
                final Spatial n = getDefendSpatial(def);
                if(n!=null&&vis.contains(def)) {
                    if(!def.isDead()) {
                        // otherwise death controller will handle
                        if(outcome.getResult()!=Outcome.Result.intercept) {
                            switch(Rand.om.nextInt(1)) {
                                case 0:
                                    n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                                new SpinModulator(n, SpinModulator.Y)}, FixedTimeController.FAST_TO_SLOW, 0.5f, 0.1f /*Rand.om.nextFloat()*0.5f*/) {
                                        protected void done() {
                                            n.removeController(this);
                                        }
                                    });
                                    break;
                                case 1:
                                    n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                                new SlideInOutController.SlideModulator(n, new Vector3f(0,0,0), new Vector3f(0,0,-1))}, FixedTimeController.CONSTANT, 0.2f, 0.1f /*Rand.om.nextFloat()*0.5f*/) {
                                        protected void done() {
                                            n.removeController(this);
                                            n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                                        new SlideInOutController.SlideModulator(n, new Vector3f(0,0,-1), new Vector3f(0,0,0))}, FixedTimeController.FAST_TO_SLOW, 0.3f, 0.1f /*Rand.om.nextFloat()*0.5f*/) {
                                                protected void done() {
                                                    n.removeController(this);
                                                }
                                            });
                                        }
                                    });
                                    break;
                            }
                        }
                    }
                    else {
                        MSpace dest = def.getEnvironment().getMSpace();
                        Direction d = dest.directionTo(outcome.getAttacker().getEnvironment().getMSpace());
                        //System.err.println("dest: "+dest);
                        //System.err.println("n.getLocalTranslation(): "+n.getWorldTranslation());
                        //System.err.println("getTranslation(dest): "+getTranslation((NHSpace)dest));
                        Vector3f vec = getTranslation((NHSpace)dest).subtract(n.getWorldTranslation()).multLocal(4).addLocal(
                                    new Vector3f(0, 14, /*Rand.om.nextInt(28)+1,*/ 0));
                        //System.err.println("vec: "+vec);
                        Gravity g = new Gravity(getNode(def), vec,
                                new Vector3f(0f, -0.125f, 0f), -50, 0f);
                        g.setMinTime(0.1f);

                        /*
                        Gravity g = new Gravity(n, new Vector3f((Rand.om.nextFloat()-0.5f)*5f,
                                    Rand.om.nextInt(28)+1, -Rand.om.nextFloat()*10),
                                new Vector3f(0f, -0.125f, 0f), -50);
                        g.setMinTime(0.1f);
                        */
                        getNode(def).addController(g);
                    }
                }
            }
        }
        else {
            NHBot def = outcome.getDefender();
            if(def!=null) {
                NHBot source = def;
                if(def.getEnvironment()==null) {
                    source = outcome.getAttacker();
                }
                Audio.getAudio().play(source, "miss");
                Spatial n = getDefendSpatial(def);
                if(n!=null) {
                    Vector3f mid = new Vector3f((Rand.om.nextBoolean()?1:-1)/0.8f,
                            2.5f*Rand.om.nextFloat()-0.5f, (Rand.om.nextFloat()-0.5f)/1.7f);
                    Vector3f loc = n.getLocalTranslation();
                    //BezierCurve c = new BezierCurve("dodge", new Vector3f[]{
                            //new Vector3f(0, 0, 0), new Vector3f(mid),
                            //new Vector3f(0, 0, 0)});
                    BezierCurve c = new BezierCurve("dodge", new Vector3f[]{
                            ZERO, new Vector3f(mid), ZERO});
                    CurveController cc = new RemovingCurveController(c, n);
                    cc.setSpeed(5);
                    cc.setUpVector(UP);
                    n.addController(cc);
                }
            }
        }
    }

    public Node getNode(Object o) {
        return (Node) _model2Nodes.get(o);
    }

    public void putNode(Object o, Node n) {
        _model2Nodes.put(o, n);
    }

    public void removeNode(Object o) {
        _model2Nodes.remove(o);
    }

    public Spatial getAttackSpatial(NHBot b) {
        Node n = getNode(b);
        if(n!=null) {
            return ((Node)n.getChild(0)).getChild(0);
        }
        return null;
    }

    public Spatial getDefendSpatial(NHBot b) {
        Node n = getNode(b);
        if(n!=null) {
            return n.getChild(0);
        }
        return null;
    }

    public Spatial getAfflictionSpatial(NHBot b) {
        Node n = getNode(b);
        if(n!=null) {
            Node c = (Node) ((Node)((Node)n.getChild(0)).getChild(0)).getChild(0);
            //return c;
            return ((Node)c.getChild(0)).getChild(0);
        }
        return null;
    }

    public Spatial getWeaponSpatial(NHBot b) {
        Node n = getNode(b);
        if(n!=null) {
            Node c = (Node) ((Node)((Node)n.getChild(0)).getChild(0)).getChild(0);
            return c;
            //return c.getChild(0);
        }
        return null;
    }

    public Spatial getActionSpatial(NHBot b) {
        Node n = getNode(b);
        return n;
    }

    public String toString() {
        return "MatrixNode@"+getLevel();
    }

    static NodeFactory getFactory(Object s) {
        return NodeFactory.getFactory(s);
    }

    class NodeUpdater extends NHEnvironmentAdapter {
        private NHEnvironment _me;
        private boolean _upright = false;
        private final float INC = (float) Math.PI/4f;
        private final float UPRIGHT = (float) Math.PI/2f;
        private final float DAMNSTRAIGHT = (float) -Math.PI;
        // rotations are stored here to prevent floating-point accumulation errors
        private final float[] ROTATIONS = {0f, INC, 2f*INC, 3f*INC, 4f*INC, 5f*INC, 6f*INC, 7f*INC};
        private int _rot = 4;
        private boolean _overhead = false;
        private Controller _airborn;
        private Controller _levitating;
        private Direction _curDir;


        public NodeUpdater(NHEnvironment me) {
            _me = me;
            NHBot b = (NHBot) _me.getBot();
            Node n = getNode(b);
            _rot = rotFor(me.getFacing());
            n.setLocalRotation(new Quaternion(new float[]{0f, ROTATIONS[_rot], 0f}));
            n.updateGeometricState(0, false);
            refreshAirborn();
            refreshLevitating();
            attributeChanged(b, "hp", null);
            for(Affliction a:b.getAfflictions()) {
                afflicted(b, a);
            }
        }

        public void updateFacing() {
            _curDir = _me.getFacing();
            _rot = rotFor(_curDir);
            Node n = getNode(_me.getBot());
            n.setLocalRotation(new Quaternion(new float[]{0f, ROTATIONS[_rot], 0f}));
        }

        private int rotFor(Direction d) {
            if(d==null) {
                return 0;
            }
            switch(d) {
                case southwest:
                    return 7;
                case west:
                    return 6;
                case northwest:
                    return 5;
                case north:
                    return 4;
                case northeast:
                    return 3;
                case east:
                    return 2;
                case southeast:
                    return 1;
                case south:
                    return 0;
                default:
                    //throw new IllegalArgumentException("unknown direction '"+d+"'");
                    return 0;
            }
        }

        public void activate(Bot b) {
            _rot = rotFor(_me.getFacing());
            Node n = getNode(b);
            if(n!=null) {
                n.setLocalRotation(new Quaternion(new float[]{0f, ROTATIONS[_rot], 0f}));
                n.updateGeometricState(0, false);
            }
        }

        private Map<Affliction,StoppableController> _afflictions;
        public void afflicted(NHBot b, Affliction a) {
            final Node n = (Node) getAfflictionSpatial(b);
            //System.err.println("AFFL: "+n.getName());
            try {
                if(_afflictions==null) {
                    _afflictions = new HashMap<Affliction,StoppableController>();
                }
                StoppableController c = ControllerFactory.createController(b, a, n);
                _afflictions.put(a, c);
            }
            catch(ClassNotFoundException e) {
                Logger.global.fine("no ui for "+a.getName());
            }
        }

        public void cured(NHBot b, Affliction a) {
            if(_afflictions!=null) {
                StoppableController c = _afflictions.get(a);
                if(c!=null) {
                    c.stop();
                    return;
                }
            }
            Logger.global.fine("no ui for "+a.getName());
        }

        public void actionPerformed(NHBot b, InstantaneousAction action) {
            final Node n = (Node) getActionSpatial(b);
            try {
                ControllerFactory.createController(b, action, n);
                //refreshAirborn();
            }
            catch(ClassNotFoundException e) {
                Logger.global.info("no ui for "+action);
            }
        }

        private StoppableController _progressiveAction;
        public void actionStarted(NHBot b, ProgressiveAction action) {
            final Node n = (Node) getActionSpatial(b);
            try {
                _progressiveAction = ControllerFactory.createController(b, action, n);
                refreshAirborn();
            }
            catch(ClassNotFoundException e) {
                Logger.global.info("no ui for "+action);
            }
        }

        public void actionStopped(NHBot b, ProgressiveAction action) {
            if(_progressiveAction!=null) {
                _progressiveAction.stop();
                refreshAirborn();
            }
        }

        public void faced(Bot b, Direction old, Direction d) {
            _curDir = d;
            float oldRot = ROTATIONS[_rot];
            int r = rotFor(d);
            if(r==-1) {
                return;
            }
            _rot = r;
            float newRot = ROTATIONS[_rot];
            if(Math.abs(oldRot-newRot)>Math.PI) {
                if(oldRot<newRot) {
                    oldRot += (float)Math.PI*2f;
                }
                else {
                    newRot += (float)Math.PI*2f;
                }
            }
            final Node n = getNode(b);
            if(n!=null&&n.getParent()!=null) {
                if(_overhead) {
                    n.setLocalRotation(new Quaternion(new float[]{(float) Math.PI, ROTATIONS[4], (float) Math.PI}));
                }
                else {
                    n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                new SpinModulator(n, SpinModulator.Y, oldRot, newRot)}, FixedTimeController.CONSTANT,
                                0.1f) {
                            protected void done() {
                                n.removeController(this);
                            }
                    });
                }
                update(n, null);
            }
        }

        public void moved(Bot b, MSpace from, MSpace to) {
            //new Exception().printStackTrace();
            if(((NHBot)b).isDead()) { return; }
            MoveNode n = (MoveNode) getNode(b);
            if(n!=null) {
                {
                    // TODO: sometimes rot event doesn't get noticed,
                    // correct here
                    Direction d = b.getEnvironment().getFacing();
                    if(d!=_curDir) {
                        faced(b, _curDir, d);
                    }
                }
                NHSpace s = (NHSpace) _me.getMSpace();
                if(!s.isOccupied()) {
                    return; // TODO: if patsy dies while key events are still being processed,
                            //       we may get the move event after the space is cleared.
                }
                Vector3f move = getTranslation((NHSpace)_me.getMSpace());
                //move.addLocal(new Vector3f(0f, -s.getOccupantDepth()*STACK_HEIGHT, 0f));
                try {
                    move.y -= s.getOccupantDepth()*STACK_HEIGHT;
                }
                catch(IllegalStateException e) {
                    // TODO: bot may have already moved on
                }
                catch(NullPointerException e) {
                    // TODO: bot may have already moved on
                }
                n.setTarget(move, from.directionTo(to));
                update(n, move);
            }
        }

        public void forgot(Bot b, List<MSpace> spaces) {
        }

        public void discovered(Bot b, List<MSpace> spaces) {
        }

        public void seen(Bot b, List<MSpace> spaces) {
        }

        public void obscured(Bot b, List<MSpace> spaces) {
        }

        public void noticed(Bot b, List<Bot> bots) {
        }

        public void missed(Bot b, List<Bot> bots) {
        }

        public void attributeChanged(final Bot b, String attribute, Object oldValue) {
            if("hp".equals(attribute)) {
                NHBot nb = (NHBot) b;
                Node n = (Node) getAttackSpatial(nb);
                if(n!=null) {
                    Spatial nd = ((Node)n.getChild(0)).getChild(0);
                    nd = ((Node)nd).getChild(0);
                    if(nd instanceof SwitchNode) {
                        SwitchNode dmg = (SwitchNode) nd;
                        float pct = nb.getHp()/(float)nb.getMaxHp();
                        int idx = 2;
                        if(pct>0.66) {
                            idx = 0;
                        }
                        else if(pct>0.33) {
                            idx = 1;
                        }
                        dmg.setActiveChild(idx);
                    }
                }
            }
            else if("wielded".equals(attribute)) {
                //Node n = (Node) getAttackSpatial((NHBot)b);
                SlotNode n = (SlotNode) getWeaponSpatial((NHBot)b);
                //n = (Node) n.getChild(0);
                DefaultNHBotNodeFactory.wield(n, (DefaultNHBot)b);
                Audio.getAudio().play((NHBot)b, "wield");
            }
            else if("worn".equals(attribute)) {
                //Node n = (Node) getAttackSpatial((NHBot)b);
                SlotNode n = (SlotNode) getWeaponSpatial((NHBot)b);
                //n = (Node) n.getChild(0);
                DefaultNHBotNodeFactory.wear(n, (DefaultNHBot)b, (Item)oldValue);
                Audio.getAudio().play((NHBot)b, "wield");
            }
            else if("tookOff".equals(attribute)) {
                //Node n = (Node) getAttackSpatial((NHBot)b);
                SlotNode n = (SlotNode) getWeaponSpatial((NHBot)b);
                //n = (Node) n.getChild(0);
                DefaultNHBotNodeFactory.takeOff(n, (DefaultNHBot)b, (Item)oldValue);
                Audio.getAudio().play((NHBot)b, "wield");
            }
            else if("color".equals(attribute)) {
                // replace without detaching
                Node n = getNode(b);
                if(n!=null) {
                    NodeFactory f = NodeFactory.getFactory(b);
                    f.updateColor(n, b);
                }
                final Spatial an = getDefendSpatial((NHBot)b);
                if(((NHBot)b).changesNoticably()) {
                    an.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                new SpinModulator(an, SpinModulator.Y, 0f, 8f*(float)Math.PI)}, FixedTimeController.FAST_TO_SLOW, 0.5f, 0f /*Rand.om.nextFloat()*0.5f*/) {
                        protected void done() {
                            an.removeController(this);
                        }
                    });
                }
            }
            else if("model".equals(attribute)||"color".equals(attribute)||"size".equals(attribute)) {
                // reload model
                detachChild(getNode(b));
                attachBot(b);
                if(getNode(b).getParent()!=null) {
                    getNode(b).getParent().updateRenderState();
                }
                refreshAirborn();
                _updaters.get(b).activate(b);
                final Spatial an = getDefendSpatial((NHBot)b);
                if(((NHBot)b).changesNoticably()) {
                    an.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                                new SpinModulator(an, SpinModulator.Y, 0f, 8f*(float)Math.PI)}, FixedTimeController.FAST_TO_SLOW, 0.5f, 0f /*Rand.om.nextFloat()*0.5f*/) {
                        protected void done() {
                            an.removeController(this);
                        }
                    });
                }
            }
            else if("airborn".equals(attribute)) {
                refreshAirborn();
            }
            else if("levitating".equals(attribute)) {
                refreshLevitating();
            }
        }

        public void itemModified(NHBot bot, Item i) {
            NHBot b = (NHBot) _me.getBot();
            if(i==b.getWielded()) {
                Node n = (Node) getAttackSpatial((NHBot)b);
                n = (Node) n.getChild(0);
                //System.err.println("Node: "+n);
                DefaultNHBotNodeFactory.wield((SlotNode)n, (DefaultNHBot)b);
            }
        }

        private void refreshAirborn() {
            NHBot b = _me.getBot();
            final Spatial n = getDefendSpatial(b);
            if(_airborn!=null) {
                n.removeController(_airborn);
                n.getLocalTranslation().y -= 2.0f;
                _airborn = null;
            }
            if(b.isAirborn()&&!b.isOccupied()) {
                final float gy = 0.3f;
                //final float alt = 0.7f;
                float alt = 0f;
                try {
                    alt = STACK_HEIGHT*-b.getEnvironment().getMSpace().getOccupantDepth();
                }
                catch(IllegalStateException e) {
                    // TODO: bot may have moved on
                }
                //final float alt = 16f;
                final Vector3f[] archetype = new Vector3f[]{new Vector3f(-gy, alt, -gy),
                        new Vector3f(-gy, alt, gy), new Vector3f(-gy, alt, gy), new Vector3f(gy, alt, -gy)};
                // give each flyer a random starting point so they're not all
                // moving the exact same way.
                int split = Rand.om.nextInt(archetype.length);
                Vector3f[] points = new Vector3f[archetype.length+1]; // +1 to wrap around
                for(int i=0;i<points.length;i++) {
                    points[i] = archetype[split++];
                    if(split==archetype.length) {
                        split = 0;
                    }
                }
                BezierCurve bc = new BezierCurve("airborn", points);
                CurveController cc = new CurveController(bc, n);
                cc.setRepeatType(Controller.RT_WRAP);
                cc.setSpeed(0.7f);
                cc.setUpVector(UP);
                n.addController(cc);
                _airborn = cc;
            }
        }

        private void refreshLevitating() {
            NHBot b = _me.getBot();
            //final Spatial n = getAfflictionSpatial(b);
            final Spatial n = getDefendSpatial(b);
            if(_levitating!=null) {
                n.removeController(_levitating);
                _levitating = null;
            }
            final float alt = 0.7f;
            if(b.isLevitating()) {
                Vector3f cur = n.getLocalTranslation();
                n.addController(new SlideInOutController(n, cur, new Vector3f(cur.x, alt, cur.z),
                    SlideInOutController.FAST_TO_SLOW, 1f) {
                    protected void done() {
                        n.removeController(this);
                    }
                });

                final Vector3f[] archetype = new Vector3f[]{new Vector3f(0, alt*1.3f, 0),
                    new Vector3f(0, alt*0.7f, 0)};
                // give each flyer a random starting point so they're not all
                // moving the exact same way.
                int split = Rand.om.nextInt(archetype.length);
                Vector3f[] points = new Vector3f[archetype.length+1]; // +1 to wrap around
                for(int i=0;i<points.length;i++) {
                    points[i] = archetype[split++];
                    if(split==archetype.length) {
                        split = 0;
                    }
                }
                BezierCurve bc = new BezierCurve("levitating", points);
                CurveController cc = new CurveController(bc, n);
                cc.setRepeatType(Controller.RT_WRAP);
                cc.setSpeed(0.7f);
                cc.setUpVector(UP);
                n.addController(cc);
                _levitating = cc;
            }
            else {
                Vector3f cur = n.getLocalTranslation();
                n.addController(new SlideInOutController(n, cur, new Vector3f(cur.x, 0f, cur.z),
                    SlideInOutController.FAST_TO_SLOW, 1f) {
                    protected void done() {
                        n.removeController(this);
                    }
                });
            }
        }

        public void died(final Bot b, MSource source) {
            NHBot nb = (NHBot) b;
            if(nb.isPlayer()) {
                Audio.getAudio().play(nb, "hit_medium");
            }
            final Spatial s = getDefendSpatial((NHBot)b);
            if(s!=null) {
                SpinModulator spin = new SpinModulator(s,
                        new float[]{0f, 0f, 0f},
                        new float[]{(float) -Math.PI/2f, (Rand.om.nextFloat()-0.5f)*(float)Math.PI*2f, 0f});

                s.addController(new FixedTimeController(new FixedTimeController.Modulator[]{spin},
                            FixedTimeController.FAST_TO_SLOW, 0.3f, 0.1f) {
                    protected void done() {
                        s.removeController(this);
                        MatrixNode.this.detachChild(getNode(b));
                        removeNode(b);
                        _updaters.remove(b);
                    }
                });
            }
        }

        public void attacked(NHBot b, Outcome outcome) {
        }

        public void update() {
            update(getNode(_me.getBot()), null);
        }

        public String toString() {
            return MatrixNode.this.toString()+" (Updater)";
        }

        protected void update(Node n, Vector3f moved) {
            if(n!=null) {
                n.updateGeometricState(0, false);
            }
            else {
                Logger.global.warning("no node for "+_me.getBot());
            }
        }

        public void switchedView(View newView) {
            _overhead = newView.isOverhead();
        }
    }

    private static final ColorRGBA BLACK = new ColorRGBA(0f, 0f, 0f, 1f);
    class PlayerUpdater extends NodeUpdater {
        public PlayerUpdater(NHEnvironment me) {
            super(me);
        }

        public void discovered(Bot b, List<MSpace> spaces) {
            //long st = System.currentTimeMillis();
            for(int i=0;i<spaces.size();i++) {
            //for(MSpace space:spaces) {
                MSpace space = spaces.get(i);
                if(space != null) {
                    final Node n = getNode(space);
                    if(n!=null&&n.getParent()==null) {
                        //n.getLocalTranslation().y = 0f;
                        MatrixNode.this.attachChild(n);

                        FogState fs = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
                        fs.setDensity(0.5f);
                        fs.setEnabled(true);
                        fs.setColor(BLACK);
                        fs.setEnd(10);
                        fs.setStart(0);
                        fs.setDensityFunction(FogState.DF_LINEAR);
                        fs.setApplyFunction(FogState.AF_PER_VERTEX);
                        n.setRenderState(fs);

                        FogController fg = new FogController(n, new FogController.Modulator() {
                            public boolean modulate(FogState fs, float time) {
                                fs.setStart(time);
                                fs.setEnd(2*time);
                                return time<100;
                            }
                        }, 200f) {
                            protected void done() {
                                n.clearRenderState(RenderState.RS_FOG);
                                n.removeController(this);
                                n.updateRenderState();
                            }
                        };
                        n.addController(fg);
                        n.updateRenderState();
                    }
                }
            }
            //long end = System.currentTimeMillis();
            //System.err.println("*************** discover took "+(end-st));
        }

        public void forgot(Bot b, List<MSpace> spaces) {
            for(MSpace s:spaces) {
                if(s!=null) {
                    Node n = getNode(s);
                    if(n!=null) {
                        MatrixNode.this.detachChild(n);
                    }
                }
            }
        }

        public void noticed(Bot b, List<Bot> bots) {
            for(Bot bot:bots) {
                Node n = getNode(bot);
                if(n!=null) {
                    MatrixNode.this.attachChild(n);
                    // otherwise bots will swoop in from (0, 0, 0) or last known location
                    ((MoveNode)n).moveToTarget(getTranslation(((NHEnvironment)bot.getEnvironment()).getMSpace()));
                    n.updateRenderState();
                    NodeUpdater nu = (NodeUpdater) _updaters.get(b);
                    if(nu!=null) {
                        nu.updateFacing();
                    }
                }
            }
        }

        public void missed(Bot b, List<Bot> bots) {
            for(Bot bot:bots) {
                Node n = getNode(bot);
                if(n!=null) {
                    if(!((NHBot)bot).isDead()) {
                        // if dead, death controller will handle detach
                        MatrixNode.this.detachChild(n);
                    }
                }
            }
        }

        protected void update(Node n, Vector3f moved) {
            if(moved!=null) {
                _view.center(moved);
                //_view.center();
            }
        }
    }

    public class MoveNode extends Node {
        private float[] _angles = new float[3];
        private Vector3f _target;
        private NHBot _b;
        private CurveController _mover;
        private CurveController _side;
        private BezierCurve _sideCurve = new BezierCurve("s", new Vector3f[]{
            new Vector3f(0, 0, 0), new Vector3f(-1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0)});


        public MoveNode(NHBot b) {
            super(b.isPlayer()?"player":"bot");
            _b = b;
        }

        public void setAngles(float[] angles) {
        }

        private float _cur = 0f;
        private float _next = 16f;
        private float _idleRot = 0f;
        private Controller _idle;
        public void updateWorldData(float dt) {
            super.updateWorldData(dt);
            _cur += dt;
            //System.err.println("cur: "+_cur+"; next: "+_next);
            if(_cur>_next) {
                idle();
            }
        }

        public void idle() {
            if(_b.getAction()!=null) {
                return;
            }
            switch(Rand.om.nextInt(4)) {
                case 3:
                    SlotNode n = (SlotNode) getWeaponSpatial(_b);
                    Item i = _b.getWielded();
                    if(n!=null&&i!=null) {
                        DefaultNHBotNodeFactory.swashbuckle(n, i);
                    }
                    break;
                default:
                    lookAround(Rand.om.nextFloat()+0.3f);
                    break;
            }
            _next = _cur+4f+Rand.om.nextFloat()*8f;
        }

        public void lookAround(float time) {
            if(_idle==null) {
                final Spatial child = getChild(0);
                _next = _cur+4f+Rand.om.nextFloat()*8f;
                float lastRot = _idleRot;
                _idleRot = Rand.om.nextFloat()-0.5f;
                SpinModulator sm = new SpinModulator(child, SpinModulator.Y, lastRot, _idleRot);
                FixedTimeController c = new FixedTimeController(sm, FixedTimeController.FAST_TO_SLOW, time) {
                    protected void done() {
                        child.removeController(this);
                        _idle = null;
                    }
                };
                child.addController(c);
                _idle = c;
            }
        }

        public void unidle() {
            final Spatial child = getChild(0);
            _next = _cur + 4f;
            if(_idleRot!=0f) {
                if(_idle!=null) {
                    child.removeController(_idle);
                }
                child.setLocalRotation(new Quaternion(new float[]{0f,0f,0f}));
                _idleRot = 0f;
            }
        }

        public void moveToTarget(Vector3f target) {
            unidle();
            if(_mover!=null) {
                removeController(_mover);
                _mover = null;
            }
            try {
                target.y = -_b.getEnvironment().getMSpace().getOccupantDepth()*STACK_HEIGHT;
            }
            catch(IllegalStateException e) {
                // occupant has moved on
            }
            setLocalTranslation(target);
        }

        public Vector3f getTarget() {
            return _target;
        }

        public void setTarget(Vector3f loc, Direction d) {
            unidle();
            _target = loc;
            Vector3f target = loc;
            Vector3f now = getLocalTranslation();
            Vector3f mid = new Vector3f((target.x+now.x)/2f, (target.y+now.y)/2f+1f, (target.z+now.z)/2f);
            BezierCurve curve;

            // setting a slight random delay seems to make movement more lifelike
            float delay = 0f;
            if(!_b.isPlayer()) {
                delay = Rand.om.nextFloat();
            }
            if(_b.isSlithering()&&!_b.isLevitating()) {
                mid.y -= 1f;
                if(_side!=null) {
                    getChild(0).removeController(_side);
                    _side = null;
                }
                _side = new CurveController(_sideCurve, getChild(0));
                _side.setSpeed(5f);
                _side.setMinTime(delay);
                _side.setUpVector(UP);
                getChild(0).addController(_side);
            }
            if(_b.isLevitating()||_b.isAirborn()) {
                mid.y -= 1.50f;
            }
            else {
                NHSpace s = _b.getEnvironment().getMSpace();
            }
            curve = new BezierCurve("mover", new Vector3f[]{now, mid, target});
            if(_mover!=null) {
                removeController(_mover);
            }
            _mover = new RemovingCurveController(curve, this) {
                boolean step = false;
                protected void done() {
                    if(!step) {
                        String aud = "walk1";
                        if(_b.getSize()==Size.small||_b.getSize()==Size.tiny) {
                            aud = "walk2";
                        }
                        if(_b.isLevitating()||_b.isAirborn()) {
                            aud = null;
                        }
                        else if(_b.isSlithering()) {
                            aud = null;
                        }
                        if(aud!=null) {
                            Audio.getAudio().play(_b, aud);
                        }
                        step = true;
                    }
                }
            };

            _mover.setSpeed(5f);
            if(_b.isRolling()) {
                float init = 0f;
                float fin = FastMath.PI/2f;
                int axis = SpinModulator.Z;
                if(d!=null) {
                    switch(d) {
                        case south:
                            axis = SpinModulator.X;
                            break;
                        case north:
                            axis = SpinModulator.X;
                        case east:
                            init = FastMath.PI/2f;
                            fin = 0f;
                    }
                }

                FixedTimeController ft = new FixedTimeController(new SpinModulator(this, axis, init, fin),
                    FixedTimeController.CONSTANT, 0.2f) {
                    protected void done() {
                        removeController(this);
                    }
                };
                addController(ft);
            }
            _mover.setUpVector(UP);
            if(!_b.isPlayer()) {
                _mover.setMinTime(delay);
            }
            addController(_mover);
            if(_b.isSlithering()) {
                Audio.getAudio().play(_b, "slither");
            }

            //SlotNode sn = (SlotNode) getWeaponSpatial(_b);
            SlotNode sn = (SlotNode) ((Node)((Node)getChild(0)).getChild(0)).getChild(0);
            sn.onMove();
        }
    }

    static class RotationController extends Controller {
        private Quaternion _begin;
        private Quaternion _end;
        private Quaternion _now;
        private Quaternion _reset;
        private Spatial _s;
        private float _time = 0f;


        public RotationController(Spatial s, Quaternion begin, Quaternion end, Quaternion reset) {
            _s = s;
            _begin = begin;
            _end = end;
            _now = new Quaternion();
            _reset = reset;
            setMinTime(0f);
            setMaxTime(Float.MAX_VALUE);
            setSpeed(2f);
        }

        public void update(float dt) {
            if(isActive()) {
                _time += getSpeed()*dt;
                if(_time>=getMinTime()&&_time<=getMaxTime()) {
                    _now = interp(_begin, _end, _time);
                    _s.setLocalRotation(_now);
                    if(_time>1||_now.equals(_end)) {
                        setActive(false);
                        if(_reset!=null) {
                            _s.setLocalRotation(_reset);
                        }
                    }
                }
            }
        }

        public Quaternion interp(Quaternion a, Quaternion b, float t) {
            float u = 1f-t;
            return new Quaternion(a.x*t+b.x*u, a.y*t+b.y*u, a.z*t+b.z*u, a.w*t+b.w*u);
        }
    }
}
