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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.excelsi.matrix.*;
import java.util.Arrays;
import java.util.Vector;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;


public final class Level extends Matrix implements Stage {
    private static final long serialVersionUID = 1L;
    protected List<Room> _rooms = new ArrayList<Room>();
    protected List _passageways = new ArrayList();
    private LevelGenerator _generator;
    private int _floor;
    private String _displayedFloor;
    private String _realm;
    private String _name;
    private ActQueue _queue = new ActQueue();
    private EventRelayer _relayer = new EventRelayer();
    private float _lightLevel = 1.0f;
    private int _partitions = 1;


    public Level(int m, int n) {
        super(m, n);
        addListener(_relayer);
    }

    @Override public String getObjectType() {
        return "level";
    }

    @Override public int getOrdinal() {
        return _floor;
    }

    @Override public Matrix getMatrix() {
        return this;
    }

    public void setLight(float light) {
        float ol = _lightLevel;
        _lightLevel = light;
        putProperty(Keys.LIGHT, light);
        if(ol!=_lightLevel) {
            for(MatrixListener l:getListeners()) {
                l.attributeChanged(this, "light", ol, _lightLevel);
            }
        }
    }

    public float getLight() {
        return _lightLevel;
    }

    public void setName(String name) {
        _name = name;
    }

    @Override public String getName() {
        return _name;
    }

    public void setRealm(String realm) {
        _realm = realm;
    }

    @Override public String getRealm() {
        return _realm;
    }

    public void setDisplayedFloor(String displayedFloor) {
        _displayedFloor = displayedFloor;
    }

    public String getDisplayedFloor() {
        return _displayedFloor!=null?_displayedFloor:_floor+"";
    }

    public void setPartitions(int partitions) {
        _partitions = partitions;
    }

    public int getPartitions() {
        return _partitions;
    }

    // tigers
    // sea lions
    // giraffe
    // guinea pigs
    @Override public EventSource getEventSource() {
        return _relayer;
    }

    public void setFloor(int floor) {
        if(_floor>0) {
            throw new IllegalArgumentException("cannot assign floor "+floor+": floor is already "+_floor);
        }
        _floor = floor;
    }

    public int getFloor() {
        return _floor;
    }

    public void setGenerator(LevelGenerator g) {
        _generator = g;
    }

    public DefaultNHSpace getSpace(int i, int j) {
        return (DefaultNHSpace) super.getSpace(i, j);
    }

    public List<NHSpace> getAscendingStairs() {
        List<NHSpace> spaces = new ArrayList<NHSpace>();
        for(MSpace m:spaces()) {
            if(m instanceof Stairs && ((Stairs)m).isAscending()) {
                spaces.add((NHSpace)m);
            }
        }
        return spaces;
    }

    public ArrayList<Level.Room> normalRooms() {
        ArrayList<Level.Room> normal = new ArrayList();
        for(int i=0;i<getRooms().size();i++) {
            if(!getRooms().get(i).isSpecial()) {
                normal.add(getRooms().get(i));
            }
        }
        return normal;
    }

    public MSpace findNearestEmpty(final Class type, MatrixMSpace from) {
        return findNearestEmpty(new SpaceFilter() { public boolean accept(MSpace m) { return type.isAssignableFrom(m.getClass()); } }, from);
    }

    public List<MSpace> findAll(final Class type) {
        ArrayList<MSpace> all = new ArrayList<MSpace>();
        for(int i=0;i<width();i++) {
            for(int j=0;j<height();j++) {
                MSpace m = getSpace(i,j);
                if(m!=null&&type.isAssignableFrom(m.getClass())) {
                    all.add(m);
                }
            }
        }
        Collections.shuffle(all);
        return all;
    }

    public MSpace findNearestEmpty(SpaceFilter type, MatrixMSpace from) {
        MSpace fpm = null;
        int i = from.getI(), j = from.getJ();
        int di = 0, dj = -1;
        int tick = 0, tock = 1;
        boolean clock = false;
        int tries = 0;
        do {
            MSpace pm = getSpace(i, j);
            //if(pm!=null&&type==pm.getClass()&&!pm.isOccupied()) {
            if(pm!=null&&type.accept(pm)&&!pm.isOccupied()) {
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
        } while(++tries<10000);
        if(tries==10000) {
            throw new IllegalStateException("no more space");
        }
        return fpm;
    }

    public MSpace findRandom(SpaceFilter f) {
        MSpace[] spaces = this.spaces();
        int i=Rand.om.nextInt(spaces.length);
        int j=i;
        do {
            if(spaces[j]!=null&&f.accept(spaces[j])) {
                return spaces[j];
            }
            if(++j==spaces.length) {
                j=0;
            }
        } while(j!=i);
        return null;
    }

    public NHSpace findEmptySpace(ArrayList<Level.Room> rooms) {
        Level.Room r = rooms.get(Rand.om.nextInt(rooms.size()));
        NHSpace ms = getSpace(r.centerX(), r.centerY());
        while(ms.isOccupied()||!ms.isWalkable() || !(ms instanceof Floor)) {
            ms = getSpace(r.randomX(), r.randomY());
        }
        return ms;
    }

    public NHSpace findEmptySpace(ArrayList<Room> rooms, Class avoid) {
        ArrayList<Room> ar = new ArrayList<Room>(rooms);
        Collections.shuffle(ar);
        Room r = null;
        for(Room ro:ar) {
            boolean good = true;
fast:       for(int i=ro.getX1();i<ro.getX2();i++) {
                for(int j=ro.getY1();j<ro.getY2();j++) {
                    MSpace sp = getSpace(i,j);
                    if(sp!=null && sp.getClass()==avoid) {
                        good = false;
                        break;
                    }
                }
            }
            if(good) {
                r = ro;
                break;
            }
        }
        if(r==null) {
            r = rooms.get(Rand.om.nextInt(rooms.size()));
        }
        NHSpace ms = getSpace(r.centerX(), r.centerY());
        while(ms==null||ms.isOccupied()||!ms.isWalkable() || !(ms instanceof Floor)) {
            ms = getSpace(r.randomX(), r.randomY());
        }
        return ms;
    }

    public NHSpace findEmptySpace(ArrayList<Level.Room> rooms, final MatrixMSpace near) {
        Collections.sort(rooms, new Comparator<Level.Room>() {
            public boolean equals(Object o) { return false; }
            public int compare(Level.Room r1, Level.Room r2) {
                return (int) (10f*near.distance(getSpace(r2.centerX(), r2.centerY())) - 10f*near.distance(getSpace(r1.centerX(), r1.centerY())));
            }
        });
        //Level.Room r = rooms.get(0);
        Level.Room r = rooms.get(rooms.size()-1);
        NHSpace ms = getSpace(r.centerX(), r.centerY());
        while(ms.isOccupied()||!ms.isWalkable() || !(ms instanceof Floor)) {
            ms = getSpace(r.randomX(), r.randomY());
        }
        return ms;
    }

    public NHSpace findDistantEmptySpace(ArrayList<Level.Room> rooms, final MatrixMSpace near) {
        Collections.sort(rooms, new Comparator<Level.Room>() {
            public boolean equals(Object o) { return false; }
            public int compare(Level.Room r1, Level.Room r2) {
                return (int) (10f*near.distance(getSpace(r2.centerX(), r2.centerY())) - 10f*near.distance(getSpace(r1.centerX(), r1.centerY())));
            }
        });
        Level.Room r = rooms.get(0);
        NHSpace ms = getSpace(r.centerX(), r.centerY());
        while(ms.isOccupied()||!ms.isWalkable() || !(ms instanceof Floor)) {
            ms = getSpace(r.randomX(), r.randomY());
        }
        return ms;
    }

    public MSpace findEmptierSpace() {
        return findEmptierSpace(true);
    }

    public MSpace findEmptierSpace(boolean inRooms) {
        MSpace ms = null;
        ArrayList<Level.Room> normal = normalRooms();
        if(inRooms&&normal.size()>0) {
            ms = findRandomNormalEmptySpace();
        }
        else {
            ms = findRandomEmptySpace();
        }
        return ms;
    }

    public MSpace findRandomNormalEmptySpace() {
        ArrayList<Level.Room> normal = normalRooms();
        if(normal.size()==0) {
            return findRandomEmptySpace();
        }
        NHSpace found = null;
        for(int tries=0;tries<1000;tries++) {
            Level.Room r = normal.get(Rand.om.nextInt(normal.size()));
            NHSpace ms = (NHSpace) getSpace(r.randomX(), r.randomY());
            if(ms!=null&&!ms.isOccupied()&&ms.isWalkable()&&!ms.isSpecial()&&ms.getParasites().size()==0&&ms instanceof Floor) {
                found = ms;
                break;
            }
        }
        return found;
    }

    public MSpace findRandomEmptySpace() {
        MSpace ms = null;
        int tries = 0;
        do {
            ms = getSpace(Rand.om.nextInt(width()), Rand.om.nextInt(height()));
            if(++tries>1000) {
                //throw new IllegalStateException("no more room");
                return null;
            }
        } while(ms==null || ((NHSpace)ms).getParasites().size()>0 || ((NHSpace)ms).isSpecial() || ms.isOccupied() || !ms.isWalkable() || !(ms instanceof Floor||ms instanceof Ground));
        return ms;
    }

    public int[] findNull(int w, int h) {
        int x = Rand.om.nextInt(width()-w);
        int y = Rand.om.nextInt(height()-h);
        int ox = x, oy = y;
        do {
            boolean good = true;
            for(int i=x;i<=x+w;i++) {
                for(int j=y;j<=y+h;j++) {
                    if(getSpace(i,j)!=null) {
                        good = false;
                        break;
                    }
                }
            }
            if(good) {
                return new int[]{x, y};
            }
            ++x;
            if(x+w>=width()) {
                x = 0;
                ++y;
                if(y+h>=height()) {
                    y = 0;
                }
            }
        } while(ox!=x&&oy!=y);
        return null;
    }

    public List<Room> getRooms() {
        return _rooms;
    }

    public List getPassageways() {
        return _passageways;
    }

    private static class ActTimer implements java.io.Serializable {
        public NHBot bot;
        public int timer;

        public void reset() {
            //timer = Math.max(21 - bot.getModifiedQuickness()/5, 1);
            //timer = Math.max(26 - bot.getModifiedQuickness()/4, 1);
            timer = Math.max(1,4-bot.getModifiedQuickness()/33);
        }

        public void randomize() {
            reset();
            timer = Rand.om.nextInt(timer+1);
        }
    }

    private static class ActQueue implements java.io.Serializable {
        private List<ActTimer> _timers = new Vector<ActTimer>();


        public void add(NHBot b) {
            ActTimer t = new ActTimer();
            t.bot = b;
            t.randomize();
            _timers.add(t);
        }

        public void remove(NHBot b) {
            for(ActTimer t:_timers) {
                if(t.bot==b) {
                    _timers.remove(t);
                    break;
                }
            }
        }

        public NHBot[] getBots() {
            synchronized(_timers) {
                NHBot[] bots = new NHBot[_timers.size()];
                for(int i=0;i<bots.length;i++) {
                    bots[i] = _timers.get(i).bot;
                }
                return bots;
            }
        }

        public NHBot next() {
            if(_timers.isEmpty()) {
                return null;
            }
            NHBot next = null;
            for(;;) {
                for(ActTimer t:_timers) {
                    if(t.timer<=0) {
                        t.reset();
                        next = t.bot;
                        return next;
                    }
                }
                for(ActTimer t:_timers) {
                    --t.timer;
                }
            }
        }
    }

    @Override public void tick(final Context c) {
        //tick();
        NHBot first = null;
        while(true) {
            NHBot acting = _queue.next();
            if(acting==null) {
                // no bots
                return;
            }
            if(first!=null && first==acting) {
                break;
            }
            if(acting.isPlayer()) {
                acting.getEnvironment().unhide();
                //break;
            }
            Actor.setCurrent(acting);
            Actor.setContext(c);
            c.setActor(acting);
            acting.act(c);
            c.setActor(null);
            Actor.setCurrent(null);
            Actor.setContext(null);
            if(first==null) {
                first = acting;
            }
        }
        for(NHBot b:_queue.getBots()) {
            Actor.setCurrent(b);
            b.tick();
            Actor.setCurrent(null);
        }
        for(MSpace m:spaces()) {
            if(m!=null) {
                m.update();
            }
        }
    }

    public void tick() {
        while(true) {
            NHBot acting = _queue.next();
            if(acting.isPlayer()) {
                acting.getEnvironment().unhide();
                break;
            }
            Actor.setCurrent(acting);
            acting.act();
            Actor.setCurrent(null);
        }
        for(NHBot b:_queue.getBots()) {
            Actor.setCurrent(b);
            b.tick();
            Actor.setCurrent(null);
        }
        for(MSpace m:spaces()) {
            if(m!=null) {
                m.update();
            }
        }
    }

    private EnvironmentAdapter _dequeuer = new EnvironmentAdapter() {
        public void died(Bot dead) {
            _queue.remove((NHBot)dead);
        }
    };

    public void occupied(MSpace m, Bot b) {
        super.occupied(m, b);
        NHBot nb = (NHBot) b;
        _queue.add(nb);
        if(b instanceof Patsy) {
            ((Patsy)b).setLevel(this);
        }
        b.addListener(_dequeuer);
        b.addListener(_relayer);
        nb.setEventSource(new POV(nb, _relayer));
    }

    public void unoccupied(MSpace m, Bot b) {
        _queue.remove((NHBot)b);
        b.removeListener(_relayer);
        b.removeListener(_dequeuer);
    }

    public void moved(MSpace from, MSpace to, Bot b) {
    }

    public List<NHBot> bots() {
        return Arrays.asList(_queue.getBots());
    }

    public NHBot getPlayer() {
        NHBot p = null;
        for(NHBot b:bots()) {
            if(b.isPlayer()) {
                p = b;
                break;
            }
        }
        return p;
    }

    public MatrixEnvironment[] getBots() {
        NHBot[] bots = _queue.getBots();
        MatrixEnvironment[] ms = new MatrixEnvironment[bots.length];
        for(int i=0;i<ms.length;i++) {
            ms[i] = bots[i].getEnvironment();
        }
        return ms;
    }

    public synchronized boolean isGenerated() {
        return true;
    }

    public final boolean overlapsAny(Room room, boolean allowWallOverlap) {
        for(Room r:_rooms) {
            if(r.overlaps(room, allowWallOverlap)) {
                return true;
            }
        }
        return false;
    }

    public final void addRoom(Room room) {
        addRoom(room, false);
    }

    public final void addRoom(Room room, boolean draw) {
        _rooms.add(room);
        if(draw) {
            drawRoom(room);
        }
    }

    public final void drawRooms() {
        for(Room r:_rooms) {
            drawRoom(r);
        }
    }

    public final void drawRoom(Room room) {
        Arc2D arc = new Arc2D.Float((float)room.getX1(), (float)room.getY1(), (float)room.width(), (float)room.height(), 0f, 360f, Arc2D.CHORD);

        for(int i=room.getX1();i<=room.getX2();i++) {
            for(int j=room.getY1();j<=room.getY2();j++) {
                if(room.isRounded()&&!arc.contains(i,j)) {
                    continue;
                }
                setSpace(room.createFloor(), i, j);
            }
        }
        if(room.isWalled()) {
            for(int i=room.getX1();i<=room.getX2();i++) {
                setSpace(room.createWall(), i, room.getY1());
                setSpace(room.createWall(), i, room.getY2());
            }
            for(int i=room.getY1();i<=room.getY2();i++) {
                setSpace(room.createWall(), room.getX1(), i);
                setSpace(room.createWall(), room.getX2(), i);
            }
            setSpace(room.createWall(), room.getX1(), room.getY1());
            setSpace(room.createWall(), room.getX2(), room.getY1());
            setSpace(room.createWall(), room.getX1(), room.getY2());
            setSpace(room.createWall(), room.getX2(), room.getY2());
        }
    }

    public final boolean passagewayExists(Passageway p) {
        boolean ret = _passageways.indexOf(p)>=0;
        return ret;
    }

    public final void addPassageway(Passageway p) {
        _passageways.add(p);
    }

    public String toString() {
        return "L"+_floor;
    }

    public static class Room implements Cloneable, java.io.Serializable {
        public static class Connector {
            private int[] _coord;
            private int[] _away;


            public Connector(int x, int y, int[] away) {
                _coord = new int[]{x, y};
                _away = away;
            }

            public int[] coord() {
                return _coord;
            }

            public int[] away() {
                return new int[]{_coord[0]+_away[0],_coord[1]+_away[1]};
            }

            public boolean isVertical() {
                return _away[0]==0;
            }
        }

        private int _x1;
        private int _y1;
        private int _x2;
        private int _y2;
        private boolean _special;
        private boolean _doors;
        private Class _floor = Floor.class;
        private Class _wall = Wall.class;
        private Class _door = Doorway.class;
        private boolean _walled = true;
        private boolean _rounded = false;


        public Room(int x, int y, int w, int h, int mx, int my) {
            _x1 = x-w/2;
            _y1 = y-h/2;
            _x2 = x+(w+1)/2;
            _y2 = y+(h+1)/2;
            if(_x2>=mx-1) {
                int d = _x2 - mx + 1;
                _x1 -= d;
                _x2 -= d;
            }
            if(_x1<=0) {
                int d = -_x1;
                _x1 += d;
                _x2 += d;
            }
            if(_y2>=my-1) {
                int d = _y2 - my + 1;
                _y1 -= d;
                _y2 -= d;
            }
            if(_y1<=0) {
                int d = -_y1;
                _y1 += d;
                _y2 += d;
            }
            _doors = Rand.d100()>25;
        }

        public void setRounded(boolean rounded) {
            _rounded = rounded;
        }

        public boolean isRounded() {
            return _rounded;
        }

        public void setDoorClass(Class door) {
            _door = door;
        }

        public void setFloorClass(Class floor) {
            _floor = floor;
        }

        public void setWallClass(Class wall) {
            _wall = wall;
        }

        public void setWalled(boolean walled) {
            _walled = walled;
            if(!_walled) {
                _doors = false;
            }
        }

        public boolean isWalled() {
            return _walled;
        }

        public Room clone() {
            try {
                Room c = (Room) super.clone();
                return c;
            }
            catch(CloneNotSupportedException e) {
                throw new Error(e);
            }
        }

        public DefaultNHSpace createWall() {
            try {
                return (DefaultNHSpace) _wall.newInstance();
            }
            catch(Exception e) {
                throw new Error(e);
            }
        }

        public DefaultNHSpace createFloor() {
            try {
                return (DefaultNHSpace) _floor.newInstance();
            }
            catch(Exception e) {
                throw new Error(e);
            }
        }

        public Doorway createDoor(boolean vert, boolean open, boolean locked) {
            try {
                Doorway d = (Doorway) _door.newInstance();
                d.setVertical(vert);
                d.setOpen(open);
                d.setLocked(locked);
                return d;
            }
            catch(Exception e) {
                throw new Error(e);
            }
        }

        public void setSpecial(boolean special) {
            _special = special;
        }

        public boolean isSpecial() {
            return _special;
        }

        public void setDoors(boolean doors) {
            _doors = doors;
        }

        public boolean hasDoors() {
            return _doors;
        }

        public Connector[] getConnectors() {
            return new Connector[]{getConnector(0), getConnector(1), getConnector(2), getConnector(3)};
        }

        public Connector getConnector(int num) {
            switch(num) {
                case 0:
                    return new Connector(centerX(), getY1(), new int[]{0,-1});
                case 1:
                    return new Connector(getX2(), centerY(), new int[]{1, 0});
                case 2:
                    return new Connector(centerX(), getY2(), new int[]{0, 1});
                case 3:
                    return new Connector(getX1(), centerY(), new int[]{-1, 0});
                default:
                    throw new RuntimeException("invalid connector '"+num+"'");
            }
        }

        public boolean leftOf(Room r) {
            return centerX() < r.centerX();
        }

        public boolean above(Room r) {
            return centerY() < r.centerY();
        }

        public boolean overlaps(Room r, boolean allowWallOverlap) {
            int tx1 = getX1(), ty1 = getY1(), ty2 = getY2(), tx2 = getX2();
            int ox1 = r.getX1(), oy1 = r.getY1(), oy2 = r.getY2(), ox2 = r.getX2();
            if(allowWallOverlap) {
                return (ox1 > tx1 && ox1 < tx2 && oy1 > ty1 && oy1 < ty2)
                    || (ox2 > tx1 && ox2 < tx2 && oy1 > ty1 && oy1 < ty2)
                    || (ox1 > tx1 && ox1 < tx2 && oy2 > ty1 && oy2 < ty2)
                    || (ox2 > ox1 && ox2 < tx2 && oy2 > ty1 && oy2 < ty2);
            }
            else {
                return (ox1 >= tx1 && ox1 <= tx2 && oy1 >= ty1 && oy1 <= ty2)
                    || (ox2 >= tx1 && ox2 <= tx2 && oy1 >= ty1 && oy1 <= ty2)
                    || (ox1 >= tx1 && ox1 <= tx2 && oy2 >= ty1 && oy2 <= ty2)
                    || (ox2 >= ox1 && ox2 <= tx2 && oy2 >= ty1 && oy2 <= ty2);
            }
        }

        public int width() {
            return _x2 - _x1;
        }

        public int height() {
            return _y2 - _y1;
        }

        public int[] center() {
            return new int[] { (_x2+_x1)/2, (_y2+_y1)/2 };
        }

        public int centerX() {
            return (_x2+_x1)/2;
        }

        public int centerY() {
            return (_y2+_y1)/2;
        }

        public int randomX() {
            return _x1+1+Rand.om.nextInt(_x2-_x1);
        }

        public int randomY() {
            return _y1+1+Rand.om.nextInt(_y2-_y1);
        }

        public int getX1() {
            return _x1;
        }

        public int getY1() {
            return _y1;
        }

        public int getX2() {
            return _x2;
        }

        public int getY2() {
            return _y2;
        }

        public boolean contains(Level lev, Class space) {
            for(int i=getX1()+1;i<getX2();i++) {
                for(int j=getY1()+1;j<getY2();j++) {
                    MSpace m = lev.getSpace(i,j);
                    if(m!=null&&space.isAssignableFrom(m.getClass())) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String toString() {
            return "("+_x1+","+_y1+")-("+_x2+","+_y2+")";
        }
    }

    public static class Passageway implements java.io.Serializable {
        private Room _src;
        private Room _dest;


        public Passageway(Room room1, Room room2) {
            _src = room1;
            _dest = room2;
        }

        public Room getRoom1() {
            return _src;
        }

        public Room getRoom2() {
            return _dest;
        }

        public boolean equals(Object o) {
            Passageway p = (Passageway) o;
            return (_src == p._src && _dest == p._dest)
                || (_dest == p._src && _src == p._dest);
        }

        public int hashCode() {
            return _src.hashCode() ^ _dest.hashCode();
        }

        public String toString() {
            return _src + " -> " + _dest;
        }
    }

    @FunctionalInterface
    public interface SpaceFilter {
        boolean accept(MSpace m);
    }
}
