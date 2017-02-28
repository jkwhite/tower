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
package org.excelsi.aether;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.excelsi.matrix.*;


/**
 * Abstract base class for level generators.
 */
public abstract class AbstractLevelGenerator implements LevelGenerator {
    protected Level _level;
    private List<Mixin> _mixins = new ArrayList<Mixin>();
    private int _maxWidth;
    private int _maxHeight;
    private int _addAscendingStairs = 1;
    private int _addDescendingStairs = 1;
    private RoomModulator _modulator;


    public AbstractLevelGenerator() {
        this(-1, -1);
    }

    public AbstractLevelGenerator(int maxWidth, int maxHeight) {
        _maxHeight = maxHeight;
        _maxWidth = maxWidth;
    }

    public void setAddAscendingStairs(int s) {
        _addAscendingStairs = s;
    }

    public int getAddAscendingStairs() {
        return _addAscendingStairs;
    }

    public void setAddDescendingStairs(int s) {
        _addDescendingStairs = s;
    }

    public int getAddDescendingStairs() {
        return _addDescendingStairs;
    }

    public void setRoomModulator(RoomModulator m) {
        _modulator = m;
    }

    public RoomModulator getRoomModulator() {
        return _modulator;
    }

    public void generate(Level level) {
        if(_maxWidth==-1) {
            _maxWidth = level.width();
        }
        if(_maxHeight==-1) {
            _maxHeight = level.height();
        }
        _level = level;
    }

    public static void mixin(Level level) {
        if(Universe.getUniverse()!=null) {
            for(Mixin m:Universe.getUniverse().mixinsFor(Level.class)) {
                m.mix(level);
            }
        }
    }

    protected Level.Room[] closestTo(final Level.Room r, int count) {
        ArrayList rooms = new ArrayList(_level.getRooms());
        Collections.sort(rooms, new Comparator() {
            public int compare(Object o1, Object o2) {
                Level.Room r1 = (Level.Room) o1;
                Level.Room r2 = (Level.Room) o2;
                return dist(r1, r) - dist(r2, r);
            }

            private int dist(Level.Room r1, Level.Room r2) {
                int x = r2.centerX() - r1.centerX();
                int y = r2.centerY() - r1.centerY();
                return (int) Math.sqrt(x*x+y*y);
            }

            public boolean equals(Object o) {
                return false;
            }
        });
        return (Level.Room[]) rooms.subList(1, Math.min(rooms.size(), 1+count)).toArray(new Level.Room[0]);
    }

    protected void generatePassageways() {
        for(Iterator i=_level.getRooms().iterator();i.hasNext();) {
            Level.Room r = (Level.Room) i.next();
            int numc = Rand.om.nextInt(_level.getRooms().size()/2)+1;
            Level.Room[] cs = closestTo(r, numc);
            for(int j=0;j<cs.length;j++) {
                Level.Passageway p = new Level.Passageway(r, cs[j]);
                if(!_level.passagewayExists(p)) {
                    addPassageway(p);
                }
            }
        }
        drawPassageways3();
    }

    protected void drawPassageways2() {
        for(Iterator i=_level.getPassageways().iterator();i.hasNext();) {
            Level.Passageway p = (Level.Passageway) i.next();
            Level.Room r1 = p.getRoom1();
            Level.Room r2 = p.getRoom2();
            int c1r, c2r;
            if(r1.leftOf(r2)) {
                if(r1.above(r2)) {
                    c1r = Rand.om.nextBoolean()?1:2;
                    c2r = Rand.om.nextBoolean()?0:3;
                }
                else {
                    c1r = Rand.om.nextBoolean()?0:1;
                    c2r = Rand.om.nextBoolean()?2:3;
                }
            }
            else {
                if(r1.above(r2)) {
                    c1r = Rand.om.nextBoolean()?2:3;
                    c2r = Rand.om.nextBoolean()?0:1;
                }
                else {
                    c1r = Rand.om.nextBoolean()?0:3;
                    c2r = Rand.om.nextBoolean()?1:2;
                }
            }

            Level.Room.Connector c1 = r1.getConnector(c1r);
            Level.Room.Connector c2 = r2.getConnector(c2r);
            setSpace(new Floor(), c1.coord()[0], c1.coord()[1]);
            setSpace(new Floor(), c2.coord()[0], c2.coord()[1]);
            int x = c1.away()[0], y = c1.away()[1];
            int ex = c2.away()[0], ey = c2.away()[1];
            int count = 0;
            MSpace dest = getSpace(ex, ey);
            Stack<MSpace> steps = new Stack<MSpace>();
            steps.push(getSpace(x, y));
            while(!steps.peek().equals(dest)) {
                MSpace s = steps.peek();
                int dx, dy;
                dx = x>ex?-1:x<ex?1:0;
                dy = y>ey?-1:y<ey?1:0;
                if(dx*dy!=0) {
                    if(Rand.om.nextBoolean()) {
                        dx = 0;
                    }
                    else {
                        dy = 0;
                    }
                }
                MSpace n = null;
                do {
                    n = getSpace(x+dx, y+dy);
                    if(n==null||n.equals(dest)||n instanceof Ground) {
                        break;
                    }
                } while(true);
            }
        }
    }

    protected void drawPassageways3() {
        drawPassageways3(7);
    }

    protected void drawPassageways3(int sanity) {
        drawPassageways3(sanity, true);
    }

    protected void drawPassageways3(int sanity, boolean doorsOnly) {
        for(Iterator it=_level.getPassageways().iterator();it.hasNext();) {
            Level.Passageway p = (Level.Passageway) it.next();
            Level.Room r1 = p.getRoom1();
            Level.Room r2 = p.getRoom2();
            int c1r, c2r;
            Level.Room.Connector[] c1s = r1.getConnectors();
            Level.Room.Connector[] c2s = r2.getConnectors();
            int[] best = new int[2];
            double least = Double.MAX_VALUE;
            for(int i=0;i<c1s.length;i++) {
                for(int j=0;j<c2s.length;j++) {
                    int[] coord1 = c1s[i].coord();
                    int[] coord2 = c2s[j].coord();
                    double dist = Math.hypot(coord2[0] - coord1[0], coord2[1] - coord1[1]);
                    if(dist<least) {
                        best[0] = i;
                        best[1] = j;
                        least = dist;
                    }
                }
            }
            c1r = best[0];
            c2r = best[1];

            Level.Room.Connector c1 = r1.getConnector(c1r);
            Level.Room.Connector c2 = r2.getConnector(c2r);
            MatrixMSpace door1 = r1.hasDoors()?r1.createDoor(c1.isVertical(), doorOpen(), doorLocked()):r1.createFloor();
            MatrixMSpace door2 = r2.hasDoors()?r2.createDoor(c2.isVertical(), doorOpen(), doorLocked()):r2.createFloor();
            int x = c1.away()[0], y = c1.away()[1];
            int ex = c2.away()[0], ey = c2.away()[1];
            if(!(getSpace(x, y) instanceof Wall)) {
                setSpace(door1, c1.coord()[0], c1.coord()[1]);
            }
            if(!(getSpace(ex, ey) instanceof Wall)) {
                setSpace(door2, c2.coord()[0], c2.coord()[1]);
            }
            if(!doorsOnly) {
                Ground start = new Ground();
                setSpace(start, x, y);
                Ground end = new Ground();
                setSpace(end, ex, ey);
                MSpace[][] paths = start.paths(end, true, 1, createPassagewayFilter(start), mf(sanity), null);
                //System.err.println("DRAWING PATH: "+Arrays.toString(paths[0]));
                for(MSpace m:paths[Rand.om.nextInt(1)]) {
                    m.replace(new Ground());
                }
            }
        }
    }

    protected Filter createPassagewayFilter(NHSpace start) {
        return null;
    }

    protected MSpace findNearestEmpty(Class type, MatrixMSpace from) {
        return _level.findNearestEmpty(type, from);
    }

    private static float mf(int num) {
        float f = Float.MIN_VALUE;
        while(num-->0) {
            f = Math.max(f, Rand.om.nextFloat());
        }
        return f;
    }

    protected void drawPassageways() {
        for(Iterator it=_level.getPassageways().iterator();it.hasNext();) {
            Level.Passageway p = (Level.Passageway) it.next();
            Level.Room r1 = p.getRoom1();
            Level.Room r2 = p.getRoom2();
            int c1r, c2r;
            /*
            if(r1.leftOf(r2)) {
                if(r1.above(r2)) {
                    c1r = Rand.om.nextBoolean()?1:2;
                    c2r = Rand.om.nextBoolean()?0:3;
                }
                else {
                    c1r = Rand.om.nextBoolean()?0:1;
                    c2r = Rand.om.nextBoolean()?2:3;
                }
            }
            else {
                if(r1.above(r2)) {
                    c1r = Rand.om.nextBoolean()?2:3;
                    c2r = Rand.om.nextBoolean()?0:1;
                }
                else {
                    c1r = Rand.om.nextBoolean()?0:3;
                    c2r = Rand.om.nextBoolean()?1:2;
                }
            }
            */
            Level.Room.Connector[] c1s = r1.getConnectors();
            Level.Room.Connector[] c2s = r2.getConnectors();
            int[] best = new int[2];
            double least = Double.MAX_VALUE;
            for(int i=0;i<c1s.length;i++) {
                for(int j=0;j<c2s.length;j++) {
                    int[] coord1 = c1s[i].coord();
                    int[] coord2 = c2s[j].coord();
                    double dist = Math.hypot(coord2[0] - coord1[0], coord2[1] - coord1[1]);
                    if(dist<least) {
                        best[0] = i;
                        best[1] = j;
                        least = dist;
                    }
                }
            }
            c1r = best[0];
            c2r = best[1];

            Level.Room.Connector c1 = r1.getConnector(c1r);
            Level.Room.Connector c2 = r2.getConnector(c2r);
            MatrixMSpace door1 = r1.hasDoors()?r1.createDoor(c1.isVertical(), doorOpen(), doorLocked()):new Floor();
            MatrixMSpace door2 = r2.hasDoors()?r2.createDoor(c2.isVertical(), doorOpen(), doorLocked()):new Floor();
            setSpace(door1, c1.coord()[0], c1.coord()[1]);
            setSpace(door2, c2.coord()[0], c2.coord()[1]);
            int x = c1.away()[0], y = c1.away()[1];
            int ex = c2.away()[0], ey = c2.away()[1];
            int count = 0;
            int dx = 0, dy = 0;
            do {
                /*
                if(x<0||y<0||x>=width()||y>=height()) {
                    break;
                }
                */
                Ground g = new Ground();
                setSpace(g, x, y);
                if(x==ex&&y==ey) {
                    break;
                }
                if(dx!=0&&dy!=0) {
                    if(getSpace(x+dx, y+dy) instanceof Ground) {
                        x += dx;
                        y += dy;
                        continue;
                    }
                }
                dx = x<ex?1:x>ex?-1:0;
                dy = y<ey?1:y>ey?-1:0;
                while(dx!=0 && dy!=0 && (getSpace(x+dx, y+dy)!=null&&!(getSpace(x+dx,y+dy) instanceof Ground))) {
                    if(dx != 0) {
                        dx = 0;
                    }
                    else if(dy != 0) {
                        dy = 0;
                    }
                }
                if(dy != 0 && dx != 0) {
                    if(Rand.om.nextBoolean()) {
                        if(dy != 0) {
                            dx = 0;
                        }
                    }
                    else {
                        if(dx != 0) {
                            dy = 0;
                        }
                    }
                }
                x += dx;
                y += dy;
            } while(getSpace(x, y)==null||getSpace(x, y) instanceof Ground);
        }
    }

    protected ArrayList<Level.Room> normalRooms() {
        return _level.normalRooms();
    }

    protected NHSpace findEmptySpace(ArrayList<Level.Room> rooms, MatrixMSpace near) {
        return _level.findEmptySpace(rooms, near);
    }

    protected NHSpace findEmptySpace(ArrayList<Level.Room> rooms) {
        return _level.findEmptySpace(rooms);
    }

    protected MSpace findEmptierSpace() {
        return _level.findEmptierSpace();
    }

    protected int width() {
        return Math.min(_maxWidth, _level.width());
    }

    protected int height() {
        return Math.min(_maxHeight, _level.height());
    }

    protected void addPassageway(Level.Passageway p) {
        _level.addPassageway(p);
    }

    protected NHSpace getSpace(int x, int y) {
        return _level.getSpace(x, y);
    }

    protected void setSpace(MatrixMSpace space, int x, int y) {
        _level.setSpace(space, x, y);
    }

    private boolean hasDoors() {
        return Rand.d100()>25;
    }

    private boolean doorOpen() {
        return Rand.d100(10);
    }

    private boolean doorLocked() {
        return Rand.d100(5);
    }
}
