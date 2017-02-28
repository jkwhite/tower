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
import java.util.logging.Logger;

import org.excelsi.matrix.*;


/** 
 * A level generator that uses a probabilistic substitution
 * cellular automata.
 */
public class SectionLevelGenerator extends AbstractLevelGenerator {
    private float TOLERANCE = 10.0f;
    private int _minx = 0;
    private int _miny = 0;
    private int _partition = 0;
    private int _astairIndex = 0;
    private int _dstairIndex = 0;
    private int _spacing = 1;
    private int _maxCells = 10;
    private boolean _allowWallOverlap = false;
    private boolean _mixin = true;
    private boolean _drawPassageways = true;
    private int[][] _roots;


    public SectionLevelGenerator() {
        super();
    }

    public SectionLevelGenerator(int maxWidth, int maxHeight) {
        this(0, 0, maxWidth, maxHeight);
    }

    public SectionLevelGenerator(int minx, int miny, int maxWidth, int maxHeight) {
        super(maxWidth, maxHeight);
        _minx = minx;
        _miny = miny;
    }

    public SectionLevelGenerator(int[][] cells) {
        _roots = cells;
    }

    public void setAscendingStairIndex(int i) {
        _astairIndex = i;
    }

    public int getAscendingStairIndex() {
        return _dstairIndex;
    }

    public void setDescendingStairIndex(int i) {
        _dstairIndex = i;
    }

    public int getDescendingStairIndex() {
        return _dstairIndex;
    }

    public void setCurrentPartition(int p) {
        _partition = p;
    }

    public int getCurrentPartition() {
        return _partition;
    }

    public int getCellSpacing() {
        return _spacing;
    }

    public void setCellSpacing(int spacing) {
        _spacing = spacing;
    }

    public void setMixin(boolean mixin) {
        _mixin = mixin;
    }

    public boolean isMixin() {
        return _mixin;
    }

    public void setMaxCells(int max) {
        _maxCells = max;
    }

    public int getMaxCells() {
        return _maxCells;
    }

    public void setAllowWallOverlap(boolean allow) {
        _allowWallOverlap = allow;
    }

    public boolean isAllowWallOverlap() {
        return _allowWallOverlap;
    }

    public void setDrawPassageways(boolean draw) {
        _drawPassageways = draw;
    }

    public boolean isDrawPassageways() {
        return _drawPassageways;
    }

    public void generate(Level level, MatrixMSpace player) {
        super.generate(level);

        // run automata
        Step s;
        if(_roots==null) {
            Cell top = new Cell(_minx, _miny, width(), height(), 1);
            s = new Step(top);
        }
        else {
            Cell[] tops = new Cell[_roots.length];
            for(int i=0;i<_roots.length;i++) {
                tops[i] = new Cell(_roots[i][0], _roots[i][1], _roots[i][2], _roots[i][3], 1);
            }
            s = new Step(tops);
        }
        // this ruleset creates horizontal and vertical
        // cells in equal proportion, alternating the twain
        Ruleset rules = new Ruleset(new Rule[] {
            new Rule("1 -> | 2 2", 0.5f),
            new Rule("2 -> - 1 1", 0.5f),
        });
        int ctries = 0;
        while(s.cells().length<_maxCells&&++ctries<5000) {
            s = s.next(rules);
        }

        // make rooms
        for(Cell c:s.cells()) {
            Level.Room room = null;
            int tries = 0;
            do {
                float sanity = getRoomSanity();
                int x = (int)(sanity*Rand.om.nextInt(c.width()))+c.x1-_spacing;
                int y = (int)(sanity*Rand.om.nextInt(c.height()))+c.y1-_spacing;
                //int w = (int)(sanity*Rand.om.nextInt(c.width()))+c.x1-1;
                //int h = (int)(sanity*Rand.om.nextInt(c.height()))+c.y1-1;
                //int w = Math.min(9-(int)(sanity*Rand.om.nextInt(5)), c.width()-1);
                //int h = Math.min(7-(int)(sanity*Rand.om.nextInt(5)), c.height()-1);
                int w = Math.min((int)((2f-sanity)*9)-Rand.om.nextInt(5), c.width()-_spacing);
                int h = Math.min((int)((2f-sanity)*7)-Rand.om.nextInt(5), c.height()-_spacing);
                /*
                if(_spacing==0) {
                    w = c.width();
                    h = c.height();
                }
                */
                /*
                int x = c.x1;
                int y = c.y1;
                int w = c.width()-1;
                int h = c.height()-1;
                */
                room = new Level.Room(c.centerX(), c.centerY(), w, h, c.x2, c.y2);
            } while((room.width()<3||room.height()<3)&&++tries<100);
            if(room==null) {
                Logger.global.fine("could not make room for "+c);
            }
            else {
                if(room.width()>=3&&room.height()>=3&&!_level.overlapsAny(room, _allowWallOverlap)) {
                    modulate(room);
                    _level.addRoom(room, true);
                }
            }
        }

        // make passageways
        List<Level.Room> rooms = _level.getRooms();
        //Map<Level.Room, List<Level.Passageway>> pways = new HashMap<Level.Room, List<Level.Passageway>>();
        int[] pways = new int[rooms.size()];
        boolean allConnected = false;
        Level.Room r = rooms.get(0);
        int tries = 0;
        while(!allConnected&&++tries<10000) {
            List<Level.Room> added = new ArrayList<Level.Room>();
            int pcount = Rand.om.nextInt(1)+1;
            Level.Room[] closest = closestTo(r, rooms.size());
            int j = 0;
            for(Level.Room other:closest) {
                Level.Passageway p = new Level.Passageway(r, other);
                if(!_level.passagewayExists(p)) {
                    _level.addPassageway(p);
                    added.add(other);
                    pways[rooms.indexOf(r)]++;
                    pways[rooms.indexOf(other)]++;
                    if(++j==pcount) {
                        break;
                    }
                }
            }
            if(added.size()==0) {
                int idx = Rand.om.nextInt(rooms.size());
                for(int i=0;i<pways.length;i++) {
                    if(pways[i]<pways[idx]) {
                        idx = i;
                    }
                }
                r = rooms.get(idx);
                Logger.global.fine("******* added was zero, getting random");
            }
            else {
                int idx = Rand.om.nextInt(added.size());
                for(int i=0;i<pways.length&&i<added.size();i++) {
                    if(pways[i]<pways[idx]) {
                        idx = i;
                    }
                }
                r = added.get(idx);
            }
            allConnected = true;
            for(int p:pways) {
                if(p==0) {
                    allConnected = false;
                    break;
                }
            }
        }
        if(tries==10000) {
            throw new IllegalStateException("can't connect all rooms");
        }
        //drawPassageways3(Math.max(2, 8-_level.getFloor()/2));
        if(_drawPassageways) {
            drawPassageways3(getPassagewaySanity(), false);
        }
        //addBots();

        //
        //  TODO: this needs some work to make it more stable,
        //  comment out for now
        // grow rooms
        /*
        drawPassageways();
        ArrayList<GrowCell> growing = new ArrayList<GrowCell>();
        for(Level.Room ro:rooms) {
            growing.add(new GrowCell(ro));
        }
        boolean mod = true;
        while(mod) {
            mod = false;
            for(GrowCell g:growing) {
                if(g.isFull()) {
                    continue;
                }
                mod = true;
                if(g.gx1==g.gx2&&g.gy1==g.gy2) {
                    MSpace sp = getSpace(g.gx1, g.gy1);
                    if(sp!=null) {
                        push(sp, new Wall());
                    }
                    //setSpace(new Wall(), g.gx1, g.gy1);
                }
                else {
                    //boolean[] mods = g.grow();
                    //push(g.gx1, g.gy1, Direction.northwest, new Wall());
                    //push(g.gx2, g.gy1, Direction.northeast, new Wall());
                    //push(g.gx1, g.gy2, Direction.southwest, new Wall());
                    //push(g.gx2, g.gy2, Direction.southeast, new Wall());
                    for(int x=g.gx1;x<=g.gx2;x++) {
                        push(x, g.y1, Direction.north, new Wall());
                        push(x, g.y2, Direction.south, new Wall());
                    }
                    for(int y=g.gy1;y<=g.gy2;y++) {
                        push(g.gx1, y, Direction.west, new Wall());
                        push(g.gx2, y, Direction.east, new Wall());
                    }
                    for(int x=g.gx1+1;x<g.gx2;x++) {
                        for(int y=g.gy1+1;y<g.gy2;y++) {
                            if(!(getSpace(x, y) instanceof Floor)) {
                                setSpace(new Floor(), x, y);
                            }
                        }
                    }
                }
                g.grow();
            }
        }
        */

        // add stairs
        ArrayList<Level.Room> normals = normalRooms();
        //sp = findEmptySpace(normals);
        //sp.replace(new Stairs(false));

        if(getAddDescendingStairs()>0) {
            if(_level.getFloor()>1) {
                MSpace fpm = null;
                if(player!=null) {
                    // search in a spiral pattern for the nearest
                    // stairs to the player
                    int i = player.getI(), j = player.getJ();
                    int di = 0, dj = -1;
                    /*
                    int tick = 0, tock = 1;
                    boolean clock = false;
                    do {
                        MSpace pm = getSpace(i, j);
                        if(pm instanceof Floor) {
                            fpm = pm;
                            break;
                        }
                        if(++tick==tock) {
                            tick = 0;
                            if(clock) {
                                tock++;
                                clock = false;
                            }
                            else {
                                clock = true;
                            }
                            if(dj==-1) { dj=0; di=-1; }
                            else if(di==-1) { dj=1; di=0; }
                            else if(dj==1) { dj=0; di=1; }
                            else if(di==1) { dj=-1; di=0; }
                        }
                        i += di;
                        j += dj;
                    } while(true);
                    */
                }
                if(fpm==null) {
                    fpm = findEmptySpace(normals, player);
                }
                fpm.replace(new Stairs(false, _dstairIndex++));
            }
        }
        for(int i=0;i<getAddAscendingStairs();i++) {
            NHSpace sp = _level.findEmptySpace(normals, Stairs.class);
            sp.replace(new Stairs(true, _astairIndex++));
        }
        for(int i=1;i<getAddDescendingStairs();i++) {
            NHSpace sp = _level.findEmptySpace(normals, Stairs.class);
            sp.replace(new Stairs(false, _dstairIndex++));
        }
        if(_mixin) {
            mixin(_level);
        }
    }

    protected void push(int x, int y, Direction dir, MSpace repl) {
        MSpace o = getSpace(x, y);
        if(o!=null) {
            if(!o.push(repl, dir)) {
                return;
            }
        }
        else {
            setSpace((MatrixMSpace)repl, x, y);
        }
    }

    protected void push(MSpace s, MSpace pusher) {
        Direction sel = Direction.north;
        int similars = -1;
        for(Direction d:Direction.values()) {
            if(s.move(d)==null) {
                if(similars==-1) {
                    sel = d;
                }
                MSpace[] sur = s.creator().move(d).surrounding();
                int sims = 0;
                for(MSpace su:sur) {
                    if(su.getClass()==s.getClass()) {
                        sims++;
                    }
                }
                if(similars==-1||sims>similars) {
                    sel = d;
                    similars = sims;
                }
            }
        }
        s.push(pusher, sel);
    }

    /**
     * Lets extended classes modify created rooms before they are drawn.
     */
    protected void modulate(Level.Room room) {
        if(getRoomModulator()!=null) {
            getRoomModulator().modulate(room);
        }
    }

    private float _roomSanity = 1f;
    private int _passagewaySanity = 1;
    public void setRoomSanity(float sanity) {
        _roomSanity = 1f;
    }

    public float getRoomSanity() {
        return _roomSanity;
    }

    public void setPassagewaySanity(int sanity) {
        _passagewaySanity = sanity;
    }
    
    public int getPassagewaySanity() {
        return _passagewaySanity;
    }

    protected Filter createPassagewayFilter(final NHSpace start) {
        if(_roots!=null) {
            return new Filter() {
                public boolean accept(MSpace m) {
                    MatrixMSpace s = (MatrixMSpace) m;
                    if(!(s instanceof NullMatrixMSpace)&&!start.getClass().isAssignableFrom(s.getClass())) {
                        return false;
                    }
                    int i = s.getI(), j = s.getJ();
                    for(int[] r:_roots) {
                        if(i>=r[0]&&i<=r[2]&&j>=r[1]&&j<=r[3]) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        else {
            return super.createPassagewayFilter(start);
        }
    }
 
    class Ruleset {
        private Rule[] _rules;


        public Ruleset(Rule[] rules) {
            _rules = rules;
        }

        public Cell[] apply(Cell c) {
            for(Rule r:_rules) {
                if(r.matches(c)) {
                    return r.apply(c);
                }
            }
            return new Cell[]{c};
        }
    }

    class Rule {
        private int _fromColor;
        private int[] _toColors;
        private boolean _horizontal;
        private float _percent;
        private String _desc;


        public Rule(String desc, float percent) {
            _desc = desc;
            _percent = percent;
            String[] ft = desc.split("->");
            _fromColor = Integer.parseInt(ft[0].trim());
            String[] to = ft[1].trim().split(" ");
            if(to[0].trim().equals("|")) {
                _horizontal = true;
            }
            else if(to[0].trim().equals("-")) {
                _horizontal = false;
            }
            else {
                throw new IllegalArgumentException("no orientation for '"+to[0]+"'");
            }
            _toColors = new int[to.length-1];
            for(int i=0;i<_toColors.length;i++) {
                _toColors[i] = Integer.parseInt(to[i+1].trim());
            }
        }

        public boolean matches(Cell c) {
            return c.color==_fromColor && Rand.om.nextFloat()<=_percent&&c.size()>16;
        }

        public Cell[] apply(Cell c) {
            Logger.global.fine("applying rule '"+_desc+"'");
            if(c.color!=_fromColor) {
                throw new IllegalArgumentException("cell "+c+" does not match "+_fromColor);
            }
            if(_horizontal) {
                if(c.width()<6) {
                    return new Cell[]{c};
                }
                float var = c.width()/2;
                int middle = (int) ((float)c.x1 + var*multiplier(c.width()/2)+(float)c.width()/2);
                //int middle = c.x1 + c.width()/2;
                Logger.global.fine("hsegmenting "+c+" at "+middle);
                return c.segmentHorizontal(middle, _toColors);
            }
            else {
                if(c.height()<7) {
                    return new Cell[]{c};
                }
                float var = c.height()/2;
                int middle = (int) ((float)c.y1 + var*multiplier(c.height()/2)+(float)c.height()/2);
                if(middle==c.y1) {
                    return new Cell[]{c};
                }
                //int middle = c.y1 + c.height()/2;
                Logger.global.fine("vsegmenting "+c+" at "+middle);
                return c.segmentVertical(middle, _toColors);
            }
        }
    }

    private static float multiplier(int s) {
        float mul;
        float coeff = s/10f;
        //float coeff = 0.7f;
        do {
            mul = (float) Rand.om.nextGaussian()*coeff;
        } while(Math.abs(mul)>0.8);
        return mul;
    }

    class Step {
        private Cell[] _cells;


        public Step(Cell... roots) {
            _cells = roots;
            Arrays.sort(_cells);
        }

        public Step next(Ruleset rules) {
            List<Cell> next = new ArrayList<Cell>();
            /*
            for(Cell c:_cells) {
                next.addAll(Arrays.asList(rules.apply(c)));
            }
            */
            next.addAll(Arrays.asList(rules.apply(_cells[0])));
            for(int i=1;i<_cells.length;i++) {
                next.add(_cells[i]);
            }
            return new Step(next.toArray(new Cell[next.size()]));
        }

        public Cell[] cells() {
            return _cells;
        }
    }

    class Cell implements Comparable<Cell> {
        int x1;
        int y1;
        int x2;
        int y2;
        int color;


        public Cell(int x1, int y1, int x2, int y2, int color) {
            if(x1>=x2) {
                throw new IllegalArgumentException("illegal width: "+x1+" >= "+x2);
            }
            if(y1>=y2) {
                throw new IllegalArgumentException("illegal height: "+y1+" >= "+y2);
            }
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.color = color;
        }

        public Cell[] segmentHorizontal(int x, int[] colors) {
            return new Cell[]{
                new Cell(x1, y1, x, y2, colors[0]),
                new Cell(x+_spacing, y1, x2, y2, colors[1])
            };
        }

        public Cell[] segmentVertical(int y, int[] colors) {
            return new Cell[]{
                new Cell(x1, y1, x2, y, colors[0]),
                new Cell(x1, y+_spacing, x2, y2, colors[1])
            };
        }

        public int width() {
            return x2-x1;
        }

        public int height() {
            return y2-y1;
        }

        public int centerX() {
            return (x2+x1)/2;
        }

        public int centerY() {
            return (y2+y1)/2;
        }

        public int size() {
            return width() * height();
        }

        public int compareTo(Cell c) {
            return c.size() - size();
        }

        public String toString() {
            return "("+x1+","+y1+")-("+x2+","+y2+") "+color;
        }
    }

    class GrowCell extends Cell {
        int gx1;
        int gx2;
        int gy1;
        int gy2;
        boolean xfull;
        boolean yfull;
        boolean dir;


        public GrowCell(Level.Room room) {
            super(room.getX1(), room.getY1(), room.getX2(), room.getY2(), 0);
            gx1 = gx2 = room.centerX();
            gy1 = gy2 = room.centerY();
        }

        public boolean grow() {
            boolean mod = false;
            if(yfull||(!xfull&&dir)) {
                if(gx1>x1) {
                    gx1--;
                    mod = true;
                }
                if(gx2<x2) {
                    gx2++;
                    mod = true;
                }
                xfull = !mod;
            }
            else {
                if(gy1>y1) {
                    gy1--;
                    mod = true;
                }
                if(gy2<y2) {
                    gy2++;
                    mod = true;
                }
                yfull = !mod;
            }
            dir = !dir;
            return isFull();
        }

        public boolean isFull() {
            return xfull&&yfull;
        }
    }
}
