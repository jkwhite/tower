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


import com.jme.renderer.Camera;
import com.jme.scene.Node;
import org.excelsi.aether.Game;
import org.excelsi.aether.GameListener;
import org.excelsi.aether.EventQueue;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.state.FogState;
import com.jme.system.DisplaySystem;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.RenderState;
import java.util.HashMap;
import java.util.Map;


public class TowerNode extends Node implements GameListener {
    private Game _g;
    private MatrixNode _level;
    private Map<String, MatrixNode> _levels = new HashMap<String, MatrixNode>();
    private View _view;


    public TowerNode(Game g, MatrixNode lev, View view) {
        super("tower");
        if(g==null) {
            throw new IllegalArgumentException("null game");
        }
        _g = g;
        EventQueue.getEventQueue().addGameListener(_g, this);
        setView(view);
        setLevel(lev);
    }

    public void setView(View v) {
        _view = v;
        if(_level!=null) {
            _level.setView(v);
        }
    }

    public MatrixNode getLevel() {
        return _level;
    }

    public void ascended(Game g) {
        //Persistence.backup(g);

        Node player = _level.getPlayerNode();
        _level.deactivate();
        final MatrixNode oldLev = _level;

        int floor = g.getCurrentLevel().getFloor();
        String ln = "L"+floor;
        MatrixNode level = (MatrixNode) getChild(ln);
        if(level==null) {
            level = getLevel(ln);
            if(level==null) {
                level = new MatrixNode(ln, g.getCurrentLevel());
                cacheLevel(level);
            }
            else {
                level.activate();
            }
            attachChild(level);
        }
        else {
            level.activate();
        }
        addController(new LevelController(oldLev, level, Dir.up));
        _level = level;
        _level.setView(_view);
        _view.setPlayer(_level.getPlayerNode());
        _view.center(_level.getPlayerTranslation());
    }

    public static void print(Spatial n, String pre) {
        System.err.println(pre+n.getName());
        if(n instanceof Node && ((Node)n).getChildren()!=null) {
            for(Spatial s:((Node)n).getChildren()) {
                print(s, "  "+pre);
            }
        }
    }

    public void descended(Game g) {
        Node player = _level.getPlayerNode();
        _level.deactivate();
        final MatrixNode oldLev = _level;
        int floor = g.getCurrentLevel().getFloor();

        String ln = "L"+floor;
        MatrixNode level = (MatrixNode) getChild(ln);
        if(true||level==null) {
            level = getLevel(ln);
            if(level==null) {
                level = new MatrixNode(ln, g.getCurrentLevel());
                cacheLevel(level);
            }
            else {
                level.activate();
            }
            attachChild(level);
        }
        else {
            level.activate();
        }
        addController(new LevelController(oldLev, level, Dir.down));
        _level = level;
        /*
        _level.addController(new SlideInOutController(_level, new Vector3f(0, -LEVEL_INC, 0),
            new Vector3f(0, 0, 0), SlideInOutController.FAST_TO_SLOW, LEVEL_SPEED));
        */
        _level.setView(_view);
        _view.setPlayer(_level.getPlayerNode());
        _view.center(_level.getPlayerTranslation());
    }

    private void setLevel(MatrixNode lev) {
        if(lev==null) {
            throw new IllegalArgumentException();
        }
        _level = lev;
        if(_view!=null) {
            _level.setView(_view);
        }
        if(getLevel(lev.getName())==null) {
            cacheLevel(lev);
        }
        attachChild(lev);
    }

    private void cacheLevel(MatrixNode m) {
        //_levels.put(m.getName(), m);
    }

    private MatrixNode getLevel(String name) {
        //return _levels.get(name);
        return null;
    }

    private FogState createFadeState(int st, int en) {
        FogState fs = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.0f,0.0f,0.0f,1.0f));
        fs.setEnd(en);
        fs.setStart(st);
        fs.setDensityFunction(FogState.DF_LINEAR);
        fs.setApplyFunction(FogState.AF_PER_VERTEX);
        return fs;
    }

    static class FadeModulator implements FixedTimeController.Modulator {
        private Spatial _s;
        private float _stBeg;
        private float _stEnd;
        private float _enBeg;
        private float _enEnd;

        public FadeModulator(Spatial s, float[] start, float[] end) {
            _s = s;
            if(start.length!=2||end.length!=2) {
                throw new IllegalArgumentException("start and end must be length 2");
            }
            _stBeg = start[0];
            _stEnd = start[1];
            _enBeg = end[0];
            _enEnd = end[1];
        }

        public void update(float orig, float dest) {
            FogState fs = (FogState) _s.getRenderState(RenderState.RS_FOG);
            if(fs!=null) {
                fs.setStart(_stBeg*orig+_stEnd*dest);
                fs.setEnd(_enBeg*orig+_enEnd*dest);
                _s.setRenderState(fs);
                _s.updateRenderState();
            }
        }

        public void done() {
            _s = null;
        }
    }

    public static enum Dir { up, down };
    private static final int LEVEL_INC = 5;
    private static final int LEVEL_REM = 1;
    private static final float LEVEL_SPEED = 1.2f;
    private static final float[] FOG_START = new float[]{6, 81};
    private static final float[] FOG_END = new float[]{7, 102};
    class LevelController extends FixedTimeController {
        private MatrixNode _newLev;
        public LevelController(final MatrixNode oldLev, final MatrixNode newLev, Dir d) {
            super(new Modulator[]{
                new SlideInOutController.SlideModulator(oldLev, new Vector3f(0, 0, 0),
                    new Vector3f(0, d==Dir.up?-LEVEL_INC:LEVEL_INC, 0)) { public void done() { super.done(); oldLev.free(); detachChild(oldLev); } },
                /*
                new FadeModulator(oldLev, new float[]{FOG_START[1], FOG_START[0]}, new float[]{FOG_END[1], FOG_END[0]}) {
                    public void done() {
                        super.done();
                        //detachChild(oldLev);
                        oldLev.clearRenderState(RenderState.RS_FOG);
                        oldLev.updateRenderState();
                    }
                },*/
                new SlideInOutController.SlideModulator(newLev, new Vector3f(0, d==Dir.up?LEVEL_INC:-LEVEL_INC, 0),
                    new Vector3f(0, 0, 0))/*,
                new FadeModulator(newLev, FOG_START, FOG_END) {
                    public void done() {
                        super.done();
                        newLev.clearRenderState(RenderState.RS_FOG);
                        newLev.updateRenderState();
                    }
                }*/
            }, FixedTimeController.SLOW_TO_FAST, LEVEL_SPEED);
            //oldLev.setRenderState(createFadeState(31, 42));
            //newLev.setRenderState(createFadeState(0, 1));
            oldLev.recordLocks(true);
            newLev.recordLocks(true);
            _newLev = newLev;
        }

        protected void done() {
            removeController(this);
            _newLev.restoreLocks();
        }
    }
}
