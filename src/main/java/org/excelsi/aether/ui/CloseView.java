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
import com.jme.input.action.*;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.Node;
import com.jme.renderer.Camera;
import com.jme.input.*;
import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import com.jme.math.Quaternion;
import com.jme.scene.CameraNode;
import java.util.List;
import java.util.ArrayList;


class CloseView extends CameraNode implements View {
    private static final float ZOOM_FACTOR = 0.8f;
    private Vector3f _target;
    private Vector3f _move = new Vector3f(0, 0, 0);
    private Node _root;
    private Node _player;
    private Camera _camera;
    private float _speed = 1000f;
    private float _zoom = 1f;
    private float _tolerance;
    private float _rotx = 0f;
    private float _roty = (float)Math.PI/2f;
    private float _rotz = 0f;
    private float _trotx;
    private float _troty;
    private float _trotz;
    private int _v = -1;
    private Angle[] _angles;

    private Angle _angle;
    private Vector3f _position;


    public CloseView(String name, Node root, Node player, Camera camera) {
        this(name, root, player, camera, new Vector3f(0.0f, 10.0f, 10.0f).mult(2f));
    }

    public CloseView(String name, Node root, Node player, Camera camera, Vector3f zoomVector) {
        super(name, camera);
        _root = root;
        _root.attachChild(this);
        _player = player;
        _camera = camera;
        List<Angle> filtered = new ArrayList<Angle>();
        for(String a:System.getProperty("tower.views", "follow,follow-left,above,first-person").split("[ ,]")) {
            Angle angle = null;
            if("follow".equals(a)) {
                angle = new Angle("follow", 5f, new float[]{0f, 1f}, new float[]{1f, (float)Math.PI, 0f}, 40f, new Vector3f(0f, 0f, -9f), false, false);
            }
            else if("follow-left".equals(a)) {
                angle = new Angle("follow-left", 5f, new float[]{-0.6f, 1f}, new float[]{1f, 7f*(float)Math.PI/8f, 0f}, 40f, new Vector3f(10f, 0f, -9f), false, false);
            }
            else if("follow-right".equals(a)) {
                angle = new Angle("follow-right", 5f, new float[]{0.6f, 1f}, new float[]{1f, 8f*(float)Math.PI/7f, 0f}, 40f, new Vector3f(-10f, 0f, -9f), false, false);
            }
            else if("above".equals(a)) {
                angle = new Angle("above", Float.MAX_VALUE, new float[]{0f, 0f}, new float[]{(float)Math.PI/2f, (float)Math.PI, 0f}, 0f, new Vector3f(MatrixNode.HORIZ_RATIO*Game.LEVEL_WIDTH/2f, 250f, MatrixNode.VERT_RATIO*Game.LEVEL_HEIGHT/2f), true, false);
            }
            else if("first-person".equals(a)) {
                angle = new Angle("first-person", 0f, new float[]{0f, 0f}, new float[]{0.1f, 0f, 0f}, 0f, new Vector3f(0f,2.0f,-0.4f), false, true);
            }
            if(angle!=null) {
                filtered.add(angle);
            }
        }
        _angles = (Angle[]) filtered.toArray(new Angle[filtered.size()]);

        setAngle(0);
    }

    public String next() {
        int v = _v;
        if(++v==_angles.length) {
            v = 0;
        }
        setAngle(v);
        _target = null;
        center(_player.getLocalTranslation());
        return _angle.getName();
    }

    public void setAngle(int a) {
        _tolerance = _angles[a].getTolerance();
        _angle = _angles[a];
        float[] trot = _angle.getCameraRotation();
        _trotx = trot[0];
        _troty = trot[1];
        _trotz = trot[2];
        if(_v==-1||_angle.isAttached()!=_angles[_v].isAttached()) {
            if(_angle.isAttached()) {
                _root.detachChild(this);
                getNthChild(_player,3).attachChild(this);
                setLocalTranslation(getLocalTranslation().subtract(_player.getLocalTranslation()));
                setLocalRotation(getLocalRotation().subtract(_player.getLocalRotation()));
            }
            else {
                getNthChild(_player,3).detachChild(this);
                _root.attachChild(this);
                setLocalTranslation(getLocalTranslation().add(_player.getLocalTranslation()));
                setLocalRotation(getLocalRotation().add(_player.getLocalRotation()));
            }
        }
        _v = a;
    }

    private static Node getNthChild(Node n, int depth) {
        while(depth-->0) {
            n = (Node) n.getChild(0);
        }
        return n;
    }

    public void setSpeed(float speed) {
        _speed = speed;
    }

    public float getSpeed() {
        return _speed;
    }

    public boolean isOverhead() {
        return false;
    }

    public void activate() {
        _root.attachChild(this);
        setLocalTranslation(_camera.getLocation());
        _position = _player.getLocalTranslation();
        center(_player.getLocalTranslation());
    }

    public void setPlayer(Node player) {
        if(player==null) {
            return;
        }
        if(_angle.isAttached()) {
            getNthChild(_player,3).detachChild(this);
            getNthChild(player,3).attachChild(this);
        }
        _player = player;
        _position = _player.getLocalTranslation();
        center(_position);
    }

    public void deactivate() {
        _root.detachChild(this);
    }

    public void updateWorldData(float dt) {
        Vector3f pos = getLocalTranslation();
        if(_rotx!=_trotx||_roty!=_troty||_rotz!=_trotz) {
            float rx = _rotx+(_trotx-_rotx)*dt;
            float ry = _roty+(_troty-_roty)*dt;
            float rz = _rotz+(_trotz-_rotz)*dt;
            setLocalRotation(new Quaternion(new float[]{rx, ry, rz}));
            _rotx = rx;
            _roty = ry;
            _rotz = rz;
        }
        _move.set(_target.x-pos.x, _target.y-pos.y, _target.z-pos.z);
        _move.multLocal(1.5f*dt);
        if(_move.length()>_speed) {
            _move.normalizeLocal().multLocal(_speed);
        }
        pos.addLocal(_move);
        super.updateWorldData(dt);
    }

    private static final Vector3f ZERO = new Vector3f(0,0,0);
    public void center(Vector3f position) {
        if(!_angle.isAttached()) {
            float[] rot = _angle.getRotation();
            float hyp = _zoom*_angle.getDistance();
            float x = (float)Math.sin(rot[0])*hyp;
            float z = (float)Math.cos(rot[0])*hyp;
            float y = (float)Math.sin(rot[1])*hyp;
            Vector3f off = _angle.getOffset();
            Vector3f pos = new Vector3f(x+off.x, y+off.y, z+off.z);
            if(!_angle.isFixed()) {
                pos.addLocal(position);
            }
            if(_target==null||pos.distance(getLocalTranslation())>_tolerance) {
                _target = new Vector3f(pos);
                updateGeometricState(0f, true);
            }
        }
        else {
            _target = ZERO.add(_angle.getOffset());
            //setLocalTranslation(_target);
            //setLocalRotation(new Quaternion(new float[]{0f,0f,0f}));
            updateGeometricState(0f, true);
        }
    }

    public void zoomIn() {
        _zoom *= ZOOM_FACTOR;
        _tolerance *= ZOOM_FACTOR;
        center(_position);
    }

    public void zoomOut() {
        _zoom /= ZOOM_FACTOR;
        _tolerance /= ZOOM_FACTOR;
        center(_position);
    }

    static final class Angle {
        private final float _tol;
        private final float[] _rot;
        private final float[] _camRot;
        private final float _dist;
        private final Vector3f _offset;
        private final boolean _fixed;
        private final boolean _attached;
        private final String _name;


        public Angle(String name, float tol, float[] rot, float[] camRot, float dist, Vector3f offset, boolean fixed, boolean attached) {
            _name = name;
            _tol = tol;
            _rot = rot;
            _camRot = camRot;
            _dist = dist;
            _offset = offset;
            _fixed = fixed;
            _attached = attached;
        }

        public String getName() {
            return _name;
        }

        public boolean isAttached() {
            return _attached;
        }

        public boolean isFixed() {
            return _fixed;
        }

        public float getTolerance() {
            return _tol;
        }

        public float[] getRotation() {
            return _rot;
        }

        public float[] getCameraRotation() {
            return _camRot;
        }

        public float getDistance() {
            return _dist;
        }

        public Vector3f getOffset() {
            return _offset;
        }
    }
}
