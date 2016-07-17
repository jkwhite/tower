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


import com.jme.app.AbstractGame;
import com.jme.input.*;
import com.jme.light.*;
import com.jme.math.Vector3f;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.Timer;
import com.jmex.bui.*;

import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class DungeonState implements State, View {
    private Node _root;
    private Camera _cam;
    private transient Timer _timer;
    private Node _player;
    private transient DisplaySystem _display;
    private boolean _initialized;
    private HUD _hud;
    private PolledRootNode _proot;
    private Game _g;
    private TowerNode _tower;


    public DungeonState(Game g, Node root, TowerNode tower, PolledRootNode proot, HUD hud) {
        _g = g;
        _tower = tower;
        _proot = proot;
        _root = root;
        _hud = hud;
    }

    public HUD getHud() {
        return _hud;
    }

    public Vector3f getBotTranslation(NHBot b) {
        Node n = _tower.getLevel().getNode(b);
        if(n!=null) {
            return n.getWorldTranslation();
        }
        return null;
    }

    public Vector3f getSpaceTranslation(NHSpace s) {
        Node n = _tower.getLevel().getNode(s);
        if(n!=null) {
            return n.getWorldTranslation();
        }
        return null;
    }

    public void init(AbstractGame app, Timer timer, Camera camera) {
        _timer = timer;
        _cam = camera;
        _display = DisplaySystem.getDisplaySystem();

        Level m = _tower.getLevel().getLevel();
        m.isGenerated();

        if(_initialized) {
            throw new IllegalStateException("already initialized");
        }
        if(Boolean.getBoolean("tower.noui")) {
            HUD.setWindowState(false, false, false, false);
        }
        _hud.enable();

        _tower.setView(this);
        _root.updateRenderState();
        _root.updateWorldBound();
        _initialized = true;
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public Node getRoot() {
        return _root;
    }
    

    private long _lastFPS = 0;
    public void update(float interpolation) {
        //long st = System.currentTimeMillis();
        EventQueue.getEventQueue().play();
        _timer.update();
        float tpf = _timer.getTimePerFrame();
        //long st2 = System.currentTimeMillis();
        _root.updateWorldData(tpf);
        if(++_lastFPS>1000) {
            Logger.global.info("fps: "+_timer.getFrameRate());
            CleanTexture.cleanTexture(_root);
            if(NH._uistats) {
                Node spatial = _root;
                while(spatial.getParent()!=null) {
                    spatial = spatial.getParent();
                }
                //System.err.println("Total spatials:                   "+countSpatials(spatial));
                //System.err.println("Total cached level nodes:         "+_tower.getLevel().getCachedNodeCount());
                //System.err.println("Total statically cached types:    "+NodeFactory.totalCachedTypes());
                //System.err.println("Total statically cached spatials: "+NodeFactory.totalCachedSpatials());
            }
            _lastFPS=0;
        }
        //long end = System.currentTimeMillis();
        //if(end-st>250f) {
            //System.err.println("update took "+(end-st));
            //System.err.println("updateWorldData was "+(end-st2));
        //}
    }

    public static long countSpatials(Spatial s) {
        long t = 1;
        if(s instanceof Node&&((Node)s).getChildren()!=null) {
            for(Spatial c:((Node)s).getChildren()) {
                t += countSpatials(c);
            }
        }
        return t;
    }

    public void render(float interpolation) {
        _display.getRenderer().clearBuffers();
        _display.getRenderer().draw(_root);
    }

    private View _currentView;
    public void setView(View v) {
        if(_currentView != null) {
            _currentView.deactivate();
        }
        _currentView = v;
        _currentView.activate();
        _root.updateRenderState();
        _root.updateGeometricState(0f, true);
    }

    public boolean isOverhead() {
        return _currentView.isOverhead();
    }

    public void setPlayer(Node player) {
        if(_currentView!=null) {
            _currentView.setPlayer(player);
        }
    }

    public void activate() {
        if(_currentView!=null) {
            _currentView.activate();
        }
    }

    public void deactivate() {
        if(_currentView != null) {
            _currentView.deactivate();
        }
    }

    public void center(Vector3f location) {
        if(_currentView != null) {
            _currentView.center(location);
        }
    }

    public String next() {
        if(_currentView!=null) {
            return _currentView.next();
        }
        return null;
    }

    public void zoomIn() {
        if(_currentView != null) {
            _currentView.zoomIn();
        }
    }

    public void zoomOut() {
        if(_currentView != null) {
            _currentView.zoomOut();
        }
    }

    private void moveUp(MatrixNode m) {
    }

    private void moveDown(MatrixNode m) {
    }
}
