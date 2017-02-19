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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.LinkedList;


public class Matrix extends MapEnvirons implements java.io.Serializable, MSpaceListener, Environs {
    public static final long serialVersionUID = 1L;
    //private final Environs _env = new MapEnvirons();
    private MatrixMSpace[] _matrix;
    private int _width;
    private List<MatrixListener> _listeners = new ArrayList<MatrixListener>();
    protected static Random _rand = new Random();


    public Matrix(int m, int n) {
        _matrix = new MatrixMSpace[m*n];
        _width = m;
    }

    @Override public String getObjectType() {
        return "matrix";
    }

    //@Override public String findString(String name) {
        //return _env.findString(name);
    //}
//
    //@Override public float findFloat(String name) {
        //return _env.findFloat(name);
    //}
//
    //@Override public Environs putProperty(String name, Object value) {
        //_env.putProperty(name, value);
        //return this;
    //}

    @Deprecated public void addListener(MatrixListener listener) {
        addMatrixListener(listener);
    }

    public void addMatrixListener(MatrixListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    @Deprecated public void removeListener(MatrixListener listener) {
        removeMatrixListener(listener);
    }

    public void removeMatrixListener(MatrixListener listener) {
        if(!_listeners.remove(listener)) {
            throw new IllegalArgumentException(listener+" is not listening to "+this);
        }
    }

    protected List<MatrixListener> getListeners() {
        return _listeners!=null?_listeners:new ArrayList<MatrixListener>();
    }

    public int size() {
        return _matrix.length;
    }

    public int width() {
        return _width;
    }

    public int height() {
        return _matrix.length / _width;
    }

    public String toString() {
        return "M"+width()+"x"+height();
    }

    @Override public void occupied(MSpace s, Bot occupant) {
    }

    @Override public void unoccupied(MSpace s, Bot occupant) {
        //occupant.setEnvironment(null);
    }

    @Override public void moved(MSpace source, MSpace from, MSpace to, Bot occupant) {
    }

    public void setSpace(MSpace ms, int i, int j) {
        final MatrixMSpace m = (MatrixMSpace) ms;
        //System.err.println("SETTING SPACE: "+m);
        Bot oldocc = null;
        boolean readd = false;
        if(m!=null) {
            oldocc = m.getOccupant();
            readd = m.getMatrix()!=null;
            if(readd&&oldocc!=null) {
                m.clearOccupant();
            }
        }
        int idx = _width*j+i;
        if(idx>=_matrix.length) {
            throw new IndexOutOfBoundsException("("+i+","+j+")");
        }
        MSpace old = _matrix[idx];
        if(old!=null) {
            for(MatrixListener l:new ArrayList<MatrixListener>(_listeners)) {
                l.spacesRemoved(this, new MSpace[]{old}, Actor.current());
            }
            old.removeMSpaceListener(this);
        }
        _matrix[idx] = m;
        if(m!=null) {
            m.setI(i);
            m.setJ(j);
            m.setMatrix(this);
            for(MatrixListener l:_listeners) {
                l.spacesAdded(this, new MSpace[]{m}, Actor.current());
            }
            m.addMSpaceListener(this);
            /*
            if(m.isOccupied()) {
                // hack to refresh matrix listeners
                ((MatrixEnvironment)m.getOccupant().getEnvironment()).setMSpace(m);
            }
            */
            if(readd) {
                m.setOccupant(oldocc);
            }
        }
    }

    public MatrixMSpace getSpace(int i, int j) {
        MatrixMSpace m = null;
        //int idx = _width*j+i;
        //if(idx>=0&&idx<_matrix.length) {
            //m = _matrix[idx];
        //}
        if(i>=0&&i<_width&&j>=0&&j<height()) {
        //if(i<0||i>=_width||j<0||_width*j>=_matrix.length) {
            m = _matrix[_width*j+i];
        }
        else {
            //m = _matrix[_width*j+i];
        }
        return m;
    }

    public MSpace replace(MatrixMSpace waning, MatrixMSpace waxing) {
        setSpace(null, waning.getI(), waning.getJ());
        setSpace(waxing, waning.getI(), waning.getJ());
        return waxing;
    }

    public void union(Matrix m) {
        if(m.width()!=width()||m.height()!=height()) {
            throw new IllegalArgumentException("incompatible matrix dimensions");
        }
        for(int i=0;i<width();i++) {
            for(int j=0;j<height();j++) {
                MatrixMSpace s = m.getSpace(i,j);
                if(s==null) {
                    continue;
                }
                MatrixMSpace r = getSpace(i,j);
                m.setSpace(null, i, j);
                if(r==null) {
                    setSpace(s, i, j);
                }
                else {
                    MatrixMSpace rep = r.union(s);
                    if(rep!=r) {
                        setSpace(rep, i, j);
                    }
                }
            }
        }

        /*
        for(int i=0;i<_matrix.length;i++) {
            MatrixMSpace s = m._matrix[i];
            if(s==null) {
                continue;
            }
            if(_matrix[i]!=null) {
                _matrix[i] = _matrix[i].union(s);
            }
            else {
                _matrix[i] = s;
            }
        }
        */
    }

    /** 
     * Finds the shortest path between two spaces. 
     * 
     * @param a start space
     * @param b end space
     * @param cardinal whether or not to follow only cardinal directions (north, south, east, west)
     * @param f optional filter
     * @param count number of in-order paths to return
     * @param sanity thoughts of a dry brain in a dry season, 0.0 to 1.0
     * @return list of critical paths from <code>a</code> to </code>b</code>
     */
    public List<MatrixMSpace>[] criticalPath(MatrixMSpace a, MatrixMSpace b, boolean cardinal, Filter f, int count, float sanity) {
        return criticalPath(a, b, cardinal, f, count, sanity, null);
    }

    /** 
     * Finds the shortest path between two spaces. 
     * 
     * @param a start space
     * @param b end space
     * @param cardinal whether or not to follow only cardinal directions (north, south, east, west)
     * @param f optional filter
     * @param count number of in-order paths to return
     * @param sanity thoughts of a dry brain in a dry season, 0.0 to 1.0
     * @param goodness goodness function or <code>null</code>
     * @return list of critical paths from <code>a</code> to </code>b</code>
     */
    public List<MatrixMSpace>[] criticalPath(MatrixMSpace a, MatrixMSpace b, boolean cardinal, Filter f, int count, float sanity, Affinity goodness) {
        sanity = 1-sanity;
        HashSet<MSpace> visited = new HashSet<MSpace>();
        Map<MatrixMSpace, Path<MatrixMSpace>> paths = new HashMap<MatrixMSpace, Path<MatrixMSpace>>();
        List<MatrixMSpace> frontier = new LinkedList<MatrixMSpace>();
        frontier.add(a);
        Path<MatrixMSpace> initPath = new Path<MatrixMSpace>();
        initPath.add(a);
        paths.put(a, initPath);
        List<List<MatrixMSpace>> cps = new ArrayList<List<MatrixMSpace>>();
        final float initdist = a.distance(b);
        final MSpace[] check = new MSpace[cardinal?4:8];
        final boolean antinull = goodness==null;
        while(frontier.size()>0) {
            MatrixMSpace c = frontier.remove(0);
            Path<MatrixMSpace> p = paths.get(c);
            if(c.equals(b)) {
                if(p.cost<3*p.size()) {
                    cps.add(p);
                    if(cps.size()==count) {
                        //System.err.println("PATH: "+p);
                        //System.err.println("COST: "+p.cost);
                        break;
                    }
                    else {
                        continue;
                    }
                }
                else {
                    continue;
                }
            }
            if(visited.contains(c)) {
                continue;
            }
            if(!c.equals(b)) {
                visited.add(c);
            }
            if(cardinal) {
                c.cardinal(check, antinull);
            }
            else {
                c.surrounding(check, antinull);
            }
            for(int i=0;i<check.length;i++) {
            //for(MSpace s:check) {
                MSpace s = check[i];
                //if(!visited.contains(s)) {
                if(s!=null) {
                    //if(!s.equals(b)) {
                        //visited.add(s);
                    //}
                    MatrixMSpace ms = (MatrixMSpace) s;
                    //System.err.println("examining: "+ms);
                    if(f.accept(ms)) {
                        //System.err.println("accepted");
                        Path<MatrixMSpace> np = paths.get(ms);
                        float cost = 1f;
                        if(goodness!=null) {
                            cost = goodness.evaluate(p.get(p.size()-1), ms);
                        }
                        if(np!=null) {
                            if(np.cost<=cost+p.cost) {
                                // there's a shorter path already, don't bother
                                //System.err.println("FOUND SHORTER PATH");
                                continue;
                            }
                        }
                        np = new Path<MatrixMSpace>();
                        int stidx;
                        if(sanity==0f) {
                            stidx = frontier.size();
                            if(goodness!=null) {
                                if(cost<=1f) stidx /= 2;
                                else if(cost>1f && cost<3f) stidx = 3 * stidx / 4;
                            }
                            //frontier.add(ms);
                            //if(ms.distance(b)<initdist) {
                                //stidx /= 2;
                            //}
                        }
                        else {
                            stidx = frontier.size()-_rand.nextInt(Math.max(1,(int)(sanity*frontier.size())));
                        }
                        //if(goodness!=null) {
                            //stidx = (int) (stidx*(1f-goodness.evaluate(ms)));
                        //}
                        np.addAll(p);
                        np.add(ms);
                        paths.put(ms, np);
                        np.cost = p.cost + cost;
                        //if(goodness!=null&&np.cost>=3*np.size()) {
                            //continue;
                        //}
                        if(stidx==frontier.size()) {
                            frontier.add(ms);
                        }
                        else {
                            frontier.add(stidx, ms);
                        }
                        //System.err.println("frontier: "+frontier);
                    }
                }
            }
        }
        while(cps.size()<count) {
            cps.add(null);
        }
        return (List<MatrixMSpace>[]) cps.toArray(new List[cps.size()]);
    }

    private static class Path<T> extends ArrayList<T> {
        public float cost;
    }

    public MSpace[] spaces() {
        //no - too costly
        //MSpace[] spaces = new MSpace[_matrix.length];
        //System.arraycopy(_matrix, 0, spaces, 0, spaces.length);
        //return spaces;
        return _matrix;
    }
}
