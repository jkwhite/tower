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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;



/** 
 * Matrix-based implementation of MSpace.
 */
public abstract class MatrixMSpace extends Id implements MSpace, Cloneable {
    public static final long serialVersionUID = 1L;
    private int _i;
    private int _j;
    private Matrix _m;
    private Bot _occupant;
    private List<MSpaceListener> _listeners;


    public MatrixMSpace() {
    }

    @Override public String getObjectType() {
        return "space";
    }

    public MatrixMSpace clone() {
        try {
            return (MatrixMSpace) super.clone();
        }
        catch(CloneNotSupportedException e) {
            throw new Error();
        }
    }

    public boolean equals(Object o) {
        if(!(o instanceof MatrixMSpace)) return false;
        MatrixMSpace mo = (MatrixMSpace) o;
        return o!=null&&_i==mo._i&&_j==mo._j&&_m==mo._m;
    }

    public int hashCode() {
        return _i<<8+_j;
    }

    public MatrixMSpace union(MatrixMSpace m) {
        throw new IllegalStateException(this+" already exists; cannot union with "+m);
    }

    public void addMSpaceListener(MSpaceListener listener) {
        if(_listeners == null) {
            _listeners = new ArrayList<MSpaceListener>(2);
        }
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeMSpaceListener(MSpaceListener listener) {
        if(_listeners!=null) {
            _listeners.remove(listener);
        }
    }

    public List<MSpaceListener> getMSpaceListeners() {
        return _listeners!=null?new ArrayList<MSpaceListener>(_listeners):new ArrayList<MSpaceListener>();
    }

    public boolean isNull() {
        return false;
    }
    
    public void setMatrix(Matrix m) {
        _m = m;
    }

    public Matrix getMatrix() {
        return _m;
    }

    public int getI() {
        return _i;
    }

    public int getJ() {
        return _j;
    }
    
    public void setI(int i) {
        _i = i;
    }
    
    public void setJ(int j) {
        _j = j;
    }

    public boolean isStretchy() {
        return false;
    }

    public void setOccupant(Bot occupant) {
        if(_occupant == occupant) {
            return;
        }
        if(_occupant != null) {
            throw new IllegalStateException("cannot set occupant "+occupant+": occupant is already "+_occupant);
        }
        if(occupant == null) {
            throw new IllegalArgumentException("null occupant");
        }
        _occupant = occupant;
        if(occupant.getEnvironment()==null) {
            occupant.setEnvironment(createEnvironment(occupant));
        }
        ((MatrixEnvironment)occupant.getEnvironment()).setMSpace(this);
        if(_listeners!=null) {
            for(MSpaceListener m:new ArrayList<MSpaceListener>(_listeners)) {
                m.occupied(this, occupant);
                if(!isOccupied()) {
                    break;
                }
            }
        }
    }

    public Bot getOccupant() {
        synchronized(this) {
            return _occupant;
        }
    }

    protected MatrixEnvironment createEnvironment(Bot b) {
        return new MatrixEnvironment(b, this);
    }

    protected void trigger() {
    }

    public void moveOccupant(MSpace to) {
        if(_occupant == null) {
            throw new IllegalArgumentException(this+" not occupied");
        }
        if(to.getOccupant()!=null) {
            throw new IllegalStateException("cannot move occupant "+_occupant+": occupant is already "+to.getOccupant());
        }
        if(!canLeave(to)) {
            return;
        }
        synchronized(this) {
            ((MatrixMSpace)to)._occupant = _occupant;
            _occupant = null;
            ((MatrixEnvironment)to.getOccupant().getEnvironment()).moveMSpace((MatrixMSpace)to);
        }
        if(_listeners!=null) {
            ArrayList<MSpaceListener> tempListeners = new ArrayList<MSpaceListener>(_listeners);
            for(MSpaceListener m:tempListeners) {
                m.moved(this, this, to, to.getOccupant());
            }
        }
        if(((MatrixMSpace)to)._listeners!=null) {
            ArrayList<MSpaceListener> tempListeners = new ArrayList<MSpaceListener>(((MatrixMSpace)to)._listeners);
            for(MSpaceListener m:tempListeners) {
                m.moved(to, this, to, to.getOccupant());
            }
        }
        ((MatrixMSpace)to).trigger();
    }

    public void swapOccupant(MSpace other) {
        if(_occupant == null) {
            throw new IllegalArgumentException(this+" not occupied");
        }
        if(other.getOccupant()==null) {
            throw new IllegalStateException(other+" not occupied");
        }
        if(!canLeave(other)) {
            return;
        }
        MatrixMSpace o = (MatrixMSpace) other;

        Bot occ = _occupant;
        Bot oocc = o._occupant;
        //_occupant = null;
        //o._occupant = null;
        synchronized(this) {
            _occupant = o._occupant;
            o._occupant = occ;
            ((MatrixEnvironment)o.getOccupant().getEnvironment()).moveMSpace(o);
            ((MatrixEnvironment)getOccupant().getEnvironment()).moveMSpace(this);
        }
        if(_listeners!=null) {
            ArrayList<MSpaceListener> tempListeners = new ArrayList<MSpaceListener>(_listeners);
            for(MSpaceListener m:tempListeners) {
                m.moved(this, this, o, o.getOccupant());
                m.moved(this, o, this, getOccupant());
            }
        }
        if(o._listeners!=null) {
            ArrayList<MSpaceListener> tempListeners = new ArrayList<MSpaceListener>(o._listeners);
            for(MSpaceListener m:tempListeners) {
                m.moved(o, this, o, o.getOccupant());
                m.moved(o, o, this, getOccupant());
            }
        }

        o.trigger();
    }

    public boolean isOccupied() {
        return getOccupant() != null;
    }

    public void clearOccupant() {
        if(_occupant == null) {
            //throw new IllegalStateException("cannot clear unoccupied space");
            java.util.logging.Logger.global.severe("no occupant at "+this);
            return;
        }
        Bot old = _occupant;
        _occupant = null;
        if(_listeners!=null) {
            for(MSpaceListener m:new ArrayList<MSpaceListener>(_listeners)) {
                m.unoccupied(this, old);
            }
        }
    }

    public final MatrixMSpace move(Direction d) {
        return move(d, false);
    }

    public final MatrixMSpace move(Direction d, boolean anull) {
        if(d==null) {
            return this;
        }
        int[] v = d.vector();
        MatrixMSpace m = getSpace(_i+v[0], _j+v[1]);
        if(anull&&m==null) {
            m = createNullSpace(getMatrix(), _i+v[0], _j+v[1]);
        }
        return m;
    }

    protected MatrixMSpace createNullSpace(Matrix m, int i, int j) {
        return new NullMatrixMSpace(m, i, j);
    }

    private static final EnumSet<Direction> ALL_DIRS = EnumSet.of(Direction.north, Direction.northwest,
        Direction.northeast, Direction.west, Direction.east, Direction.south, Direction.southwest, Direction.southeast);
    public MSpace[] surrounding() {
        return surrounding(ALL_DIRS);
    }

    public MSpace[] surrounding(EnumSet<Direction> dirs) {
        return surrounding(dirs, false);
    }

    private static final Direction[] DIRS_ARRAY = new Direction[]{Direction.north, Direction.south, Direction.east, Direction.west,
                    Direction.northeast, Direction.northwest, Direction.southeast, Direction.southwest};
    public MSpace[] surrounding(boolean anull) {
        Direction[] s = DIRS_ARRAY;
        MSpace[] ss = new MSpace[s.length];
        for(int i=0;i<s.length;i++) {
            ss[ss.length-i-1] = move(s[i], anull);
        }
        return ss;
    }

    public MSpace[] surrounding(MSpace[] sur, boolean anull) {
        return surrounding(sur, ALL_DIRS, anull);
    }

    public MSpace[] surrounding(EnumSet<Direction> dirs, boolean anull) {
        return surrounding(new MSpace[dirs.size()], dirs, anull);
    }

    public MSpace[] surrounding(MSpace[] sur, EnumSet<Direction> dirs, boolean anull) {
        int i=0;
        for(Direction d:dirs) {
            sur[i++] = move(d, anull);
        }
        return sur;
    }

    public MSpace[] cardinal() {
        return cardinal(false);
    }

    private static final EnumSet<Direction> CARDINAL = EnumSet.of(Direction.north, Direction.south,
                    Direction.east, Direction.west);
    public MSpace[] cardinal(boolean anull) {
        return surrounding(new MSpace[CARDINAL.size()], CARDINAL, anull);
    }

    public MSpace[] cardinal(MSpace[] cards, boolean anull) {
        return surrounding(cards, CARDINAL, anull);
    }

    public Direction directionTo(MSpace m) {
        for(Direction d:ALL_DIRS) {
            if(move(d, true).equals(m)) {
                return d;
            }
        }
        //throw new IllegalArgumentException(neighbor+" is not a neighbor of "+this);
        MatrixMSpace o = (MatrixMSpace) m;
        int dx = _i - o._i, dy = _j - o._j;
        int mdx = Math.abs(dx), mdy = Math.abs(dy);
        if(mdx>2*mdy) {
            return dx>0?Direction.west:Direction.east;
        }
        else if(mdy>2*mdx) {
            return dy>0?Direction.north:Direction.south;
        }
        else if(dx>0) {
            return dy>0?Direction.northwest:Direction.southwest;
        }
        else {
            return dy>0?Direction.northeast:Direction.southeast;
        }
    }

    public MSpace creator() {
        return new Creator(this, getMatrix());
    }

    public boolean isReplaceable() {
        return true;
    }

    public MSpace replace(MSpace replacement) {
        if(!isReplaceable()) {
            throw new IllegalStateException("space '"+this+"' is not replaceable");
        }
        Bot b = _occupant;
        MSpace rep = _m.replace(this, (MatrixMSpace)replacement);
        if(b!=null&&rep!=null) {
            rep.setOccupant(b);
        }
        return rep;
    }

    public boolean push(MSpace pusher, Direction dir) {
        /*
        MSpace[] sur = cardinal();
        MatrixMSpace into = move(dir, true);
        int i = getI(), j = getJ();
        if(into instanceof NullMatrixMSpace||(isStretchy()&&into.isStretchy())) {
            getMatrix().setSpace(null, getI(), getJ());
            getMatrix().setSpace(this, into.getI(), into.getJ());
            if(pusher!=null) {
                getMatrix().setSpace((MatrixMSpace)pusher, i, j);
            }
            if(isStretchy()) {
                for(MSpace s:sur) {
                    MatrixMSpace ms = (MatrixMSpace) s;
                    if(ms != null && ms.getClass()==getClass()) {
                        List<MatrixMSpace> cp = getMatrix().criticalPath(ms, this, false, new NullFilter(), 1, 1)[0];
                        if(cp!=null) {
                            for(MatrixMSpace m:cp) {
                                getMatrix().setSpace(clone(), m.getI(), m.getJ());
                            }
                        }
                    }
                }
            }
        }
        */
        return true;
    }

    public MSpace[][] paths(MSpace to, boolean cardinal, int count, Filter filter, float sanity, Affinity a) {
        if(filter==null) {
            filter = new NullFilter();
        }
        List<MatrixMSpace>[] paths = getMatrix().criticalPath(this, (MatrixMSpace)to, cardinal, filter, count, sanity, a);
        MSpace[][] ret = new MSpace[count][];
        int i=0;
        for(List<MatrixMSpace> p:paths) {
            if(p != null) {
                ret[i] = (MSpace[]) p.toArray(new MSpace[p.size()]);
            }
            else {
                return new MSpace[count][0];
            }
            i++;
        }
        return ret;
    }

    public MSpace[] path(MSpace to, boolean cardinal, Filter filter, float sanity) {
        return path(to, cardinal, filter, sanity, null);
    }

    public MSpace[] path(MSpace to, boolean cardinal, Filter filter, float sanity, Affinity a) {
        MSpace[][] paths = paths(to, cardinal, 1, filter, sanity, a);
        return paths[0];
    }

    public float distance(MSpace other) {
        MatrixMSpace o = (MatrixMSpace) other;
        return (float) Math.hypot(getI()-o.getI(), getJ()-o.getJ());
    }

    public MSpace closest(Filter filter, boolean cardinal) {
        HashSet<MSpace> visited = new HashSet<MSpace>();
        LinkedList<MSpace> frontier = new LinkedList<MSpace>();
        frontier.add(this);
        visited.add(this);
        while(frontier.size()>0) {
            MSpace s = frontier.remove(0);
            for(MSpace m:cardinal?s.cardinal():s.surrounding()) {
                if(m!=null) {
                    if(visited.contains(m)) {
                        continue;
                    }
                    if(filter.accept(m)) {
                        return m;
                    }
                    frontier.add(m);
                    visited.add(m);
                }
            }
        }
        return null;
    }

    public boolean visibleFrom(MSpace other, float max) {
        MatrixMSpace o = (MatrixMSpace) other;
        if(distance(o)>max) {
            return false;
        }
        float x2 = getI(), y2 = getJ(), x1 = o.getI(), y1 = o.getJ();
        float m = (y2-y1)/(x2-x1);
        float dx, dy;
        if(x2==x1) {
            dx = 0;
            dy = y2>y1?1:-1;
        }
        else if(y2==y1) {
            dy = 0;
            dx = x2>x1?1:-1;
        }
        else if(Math.abs(m)<=1) {
            dx = x2>x1?1:-1;
            dy = Math.abs(m);
            if(y1>y2) {
                dy = -dy;
            }
        }
        else {
            dx = Math.abs(1/m);
            if(x1>x2) {
                dx = -dx;
            }
            dy = y2>y1?1:-1;
        }
        //System.err.println("x2="+x2+", y2="+y2+", x1="+x1+", y1="+y1);
        //System.err.println("dx="+dx+", dy="+dy);
        MSpace chance = other;
        do {
            x1 += dx;
            y1 += dy;
            other = getSpace((int)x1, (int)y1);
            chance = getSpace((int)Math.ceil(x1), (int)Math.ceil(y1));
            //System.err.println("next: "+other+" or "+chance);
            if(other==this||chance==this) {
                //System.err.println("visible");
                return true;
            }
        } while((other!=null&&other.isTransparent())||(chance!=null&&chance.isTransparent()));
        //System.err.println("invisible");
        return false;
    }

    public boolean isCardinalTo(MSpace other) {
        MatrixMSpace o = (MatrixMSpace) other;
        return _i==o._i||_j==o._j;
    }

    public boolean isDiagonalTo(MSpace other) {
        MatrixMSpace o = (MatrixMSpace) other;
        return Math.abs(o._j-_j)/(o._i-_i)==1;
    }

    public boolean isAdjacentTo(MSpace other) {
        //MatrixMSpace o = (MatrixMSpace) other;
        //return _i-1<=o._i && _i+1>=o._i && _j-1<=o._j && _j+1>=o._j;
        if(other==this) {
            return true;
        }
        for(MSpace m:surrounding()) {
            if(m==other) {
                return true;
            }
        }
        return false;
    }

    @Override public Environs getEnvirons() {
        return _m;
    }

    @Override public Typed getContainer() {
        return _m;
    }

    public MSpace[] spaces() {
        return _m.spaces();
    }

    public String toString() {
        return getClass().getName()+"@"+getI()+","+getJ()+"["+_m+"]";
    }

    MatrixMSpace getSpace(int i, int j) {
        return _m!=null?_m.getSpace(i, j):null;
    }

    static class Creator extends MatrixMSpace {
        private MatrixMSpace _real;


        public Creator(MatrixMSpace real, Matrix m) {
            _real = real;
            setI(_real.getI());
            setJ(_real.getJ());
            setMatrix(m);
        }

        private Creator(int i, int j, Matrix m) {
            super();
            setI(i);
            setJ(j);
            setMatrix(m);
        }

        public MSpace replace(MSpace replacement) {
            if(_real!=null) {
                _real = (MatrixMSpace) _real.replace(replacement);
            }
            else {
                try {
                    getMatrix().setSpace((MatrixMSpace)replacement, getI(), getJ());
                }
                catch(IndexOutOfBoundsException e) {
                    return null;
                }
                _real = (MatrixMSpace) replacement;
            }
            return new Creator((MatrixMSpace)_real, getMatrix());
        }

        public boolean isWalkable() {
            return _real!=null?_real.isWalkable():false;
        }

        public boolean isTransparent() {
            return _real!=null?_real.isTransparent():false;
        }

        public boolean isReplaceable() {
            return _real!=null?_real.isReplaceable():true;
        }

        public boolean isNull() {
            return _real!=null?_real.isNull():true;
        }

        public MatrixMSpace getSpace(int i, int j) {
            if(i<0||j<0||i>=getMatrix().width()||j>=getMatrix().height()) {
                return null;
            }
            try {
                MSpace m = super.getSpace(i, j);
                return m==null?new Creator(i, j, getMatrix()):new Creator((MatrixMSpace)m, getMatrix());
            }
            catch(IndexOutOfBoundsException e) {
                return null;
            }
        }

        public void update() {
        }
    }

    protected boolean canLeave(MSpace to) {
        return true;
    }

    private class NullFilter implements Filter {
        public boolean accept(MSpace s) {
            MatrixMSpace sp = (MatrixMSpace) s;
            if(sp.getI()<0||sp.getJ()<0||sp.getI()>_m.width()||sp.getJ()>_m.height()) {
                return false;
            }
            return sp instanceof NullMatrixMSpace
                || MatrixMSpace.this.getClass().isAssignableFrom(sp.getClass());
        }
    }

    public class WalkableFilter implements Filter {
        public boolean accept(MSpace s) {
            MatrixMSpace sp = (MatrixMSpace) s;
            if(sp.getI()<0||sp.getJ()<0||sp.getI()>_m.width()||sp.getJ()>_m.height()) {
                return false;
            }
            return s.isWalkable();
        }
    }

    public class WalkableOrThisFilter implements Filter {
        public boolean accept(MSpace s) {
            MatrixMSpace sp = (MatrixMSpace) s;
            if(sp.getI()<0||sp.getJ()<0||sp.getI()>_m.width()||sp.getJ()>_m.height()) {
                return false;
            }
            return s.isWalkable() || MatrixMSpace.this.getClass().isAssignableFrom(sp.getClass());
        }
    }
}
