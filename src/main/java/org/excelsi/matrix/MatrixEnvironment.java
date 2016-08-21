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
package org.excelsi.matrix;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.EnumSet;
import java.util.Arrays;


public class MatrixEnvironment implements Environment {
    public static final long serialVersionUID = 1L;
    private Bot _b;
    private MatrixMSpace _m;
    private MSpace _next;
    private MSpace _last;
    private Direction _facing = Direction.north;


    public MatrixEnvironment(Bot b, MatrixMSpace m) {
        setBot(b);
        _m = m;
        b.setEnvironment(this);
    }

    public String toString() {
        return _b+"@"+_m.getI()+","+_m.getJ();
    }

    public Bot getBot() {
        return _b;
    }

    public MSpace getMSpace() {
        return _m;
    }

    public MSpace getLast() {
        return _last;
    }

    public void setBot(Bot b) {
        _b = b;
    }

    public void setMSpace(MatrixMSpace m) {
        if(_m!=null&&_m!=m&&_m.getOccupant()==_b) {
            _m.clearOccupant();
        }
        _last = _m;
        _m = m;
        if(m.getOccupant()!=getBot()) {
            m.setOccupant(getBot());
        }
    }

    public void dangerousSetMSpace(MatrixMSpace m) {
        _m = m;
    }

    protected void moveMSpace(MatrixMSpace m) {
        MSpace old = _m;
        _last = _m;
        _m = m;
        if(m.getOccupant()!=getBot()) {
            m.setOccupant(getBot());
        }
        for(EnvironmentListener e:getListeners()) {
            e.moved(_b, old, _m);
        }
    }

    public Bot getClosest() {
        throw new UnsupportedOperationException("not implemented");
    }

    public void approach(final Bot b, int max) {
        approach(b, max, true);
    }

    public void approach(final Bot b, int max, boolean overrun) {
        MSpace[] path = getMSpace().path(((MatrixEnvironment)b.getEnvironment()).getMSpace(), false, new Filter() {
                public boolean accept(MSpace s) {
                    return s!=null&&s.isWalkable()&&(!s.isOccupied()||s.getOccupant().equals(b));
                } }, Math.max(1f, _b.sanity()));
        if(path.length>1&&path.length<=max) {
            face(path[1]);
            if(overrun||path[1].getOccupant()!=b) {
                forward();
            }
        }
    }

    public void face(Bot b) {
        face(((MatrixEnvironment)b.getEnvironment()).getMSpace());
    }

    public void face(MSpace m) {
        if(m==_m) {
            return;
        }
        face(_m.directionTo(m));
    }

    public void faceAway(Bot b) {
        MatrixMSpace m = (MatrixMSpace) ((MatrixEnvironment)b.getEnvironment()).getMSpace();
        //float x2 = m.getI(), x1 = _m.getI(), y1 = m.getJ(), y2 = _m.getJ();
        float x2 = _m.getI(), x1 = m.getI(), y1 = _m.getJ(), y2 = m.getJ();
        if(x2<x1) {
            if(y2<y1) {
                face(Direction.southwest);
            }
            else if(y2>y1) {
                face(Direction.northwest);
            }
            else {
                face(Direction.west);
            }
        }
        else if(x2>x1) {
            if(y2<y1) {
                face(Direction.southeast);
            }
            else if(y2>y1) {
                face(Direction.northeast);
            }
            else {
                face(Direction.east);
            }
        }
        else {
            if(y2<y1) {
                face(Direction.south);
            }
            else if(y2>y1) {
                face(Direction.north);
            }
        }
    }

    public void move(Direction d) {
        _last = _m;
        _next = _m.move(d);
        if(_next != null && _next.isWalkable()) {
            if(_next.isOccupied()) {
                collide(_next.getOccupant());
            }
            else {
                MSpace old = _m;
                _m.moveOccupant(_next);
                //_m = (MatrixMSpace) _next;
                for(EnvironmentListener e:getListeners()) {
                    //VERIFY
                    //e.moved(_b, old, _m);
                }
            }
        }
    }

    public void forward() {
        _last = _m;
        _next = _m.move(_facing);
        if(_next != null && _next.isWalkable()) {
            if(_next.isOccupied()) {
                collide(_next.getOccupant());
            }
            else {
                _m.moveOccupant(_next);
                _m = (MatrixMSpace) _next;
                MSpace old = _m;
                for(EnvironmentListener e:getListeners()) {
                    e.moved(_b, old, _m);
                }
            }
        }
    }

    public void backward() {
        _last = _m;
        _next = _m.move(_facing.opposing());
        if(_next != null && _next.isWalkable()) {
            if(_next.isOccupied()) {
                collide(_next.getOccupant());
            }
            else {
                _m.moveOccupant(_next);
                _m = (MatrixMSpace) _next;
                MSpace old = _m;
                for(EnvironmentListener e:getListeners()) {
                    e.moved(_b, old, _m);
                }
            }
        }
    }

    public void turnLeft() {
        Direction old = _facing;
        _facing = _facing.left();
        for(EnvironmentListener e:getListeners()) {
            e.faced(_b, old, _facing);
        }
    }

    public void turnRight() {
        Direction old = _facing;
        _facing = _facing.right();
        for(EnvironmentListener e:getListeners()) {
            e.faced(_b, old, _facing);
        }
    }

    public Direction getFacing() {
        return _facing;
    }

    public void face(Direction d) {
        if(_facing!=d) {
            Direction old = _facing;
            _facing = d;
            for(EnvironmentListener e:getListeners()) {
                e.faced(_b, old, d);
            }
        }
    }

    public void die(MSource s) {
        for(EnvironmentListener e:getListeners()) {
            e.died(getBot(), s);
        }
        getSpace().clearOccupant();
    }

    protected void collide(Bot b) {
    }

    public List<EnvironmentListener> getListeners() {
        return _b.getListeners();
    }

    @Override public MSpace getSpace() {
        return _m;
    }
}
