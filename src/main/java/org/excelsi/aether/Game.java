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
import java.util.List;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.Actor;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;


public abstract class Game implements java.io.Serializable {
    public static final long serialVersionUID = 1L;
    public static final int LEVEL_WIDTH = 80*2;
    public static final int LEVEL_HEIGHT = 24*2;
    private ArrayList<Level> _levels = new ArrayList<Level>();
    private List<GameListener> _listeners = new ArrayList<GameListener>();
    private int _currentLevel = 1;
    private Patsy _player;
    private String _uuid;


    public Game() {
    }

    public void setUuid(String uuid) {
        _uuid = uuid;
    }

    public String getUuid() {
        return _uuid;
    }

    public final void init() {
        if(_levels.size()==0) {
            for(int i=0;i<_currentLevel;i++) {
                _levels.add(null);
            }
            Level l = createLevel(_currentLevel);
            _levels.add(l);
        }
    }

    public final void setPlayer(Patsy player) {
        _player = player;
    }

    public final Patsy getPlayer() {
        return _player;
    }

    public final Level getCurrentLevel() {
        return _levels.get(_currentLevel);
    }

    public final void tick(GameAction a) {
        try {
            if(N.narrative()==null) {
                if(a!=null) {
                    a.perform();
                }
            }
            else {
                N.narrative().clear();
                do {
                    if(a!=null) {
                        Actor.setCurrent(_player);
                        a.perform();
                        Actor.setCurrent(null);
                    }
                    do {
                        Time.tick();
                        getCurrentLevel().tick();
                    } while(getPlayer().isOccupied());
                } while(a!=null&&a.isRepeat()&&N.narrative().isClear());
            }
        }
        catch(ActionCancelledException e) {
            if(e.getMessage()!=null) {
                N.narrative().print(_player, e.getMessage());
            }
        }
        catch(QuitException e) {
            Thread.currentThread().interrupt();
        }
    }

    public final int getLevel() {
        return _currentLevel;
    }

    public final Level getFloor(int floor) {
        while(_levels.size()<1+floor) {
            _levels.add(null);
        }
        Level l = _levels.get(floor);
        if(l==null) {
            l = createLevel(floor);
            _levels.set(floor, l);
        }
        return l;
    }

    public final void setLevel(int level) {
        setLevel(level, null);
    }

    public final void setLevel(int level, MSpace start) {
        Logger.global.info("setting level "+level);
        MSpace[] surr = _player.getEnvironment().getMSpace().surrounding();
        int partition = 0;
        if(_player.getEnvironment().getMSpace() instanceof Stairs) {
            partition = ((Stairs)_player.getEnvironment().getMSpace()).getPartition();
        }
        //System.err.println("move with partition "+partition);
        _player.getEnvironment().getMSpace().clearOccupant();
        Level l = getFloor(level);
        final boolean ascended = _currentLevel<level;
        _currentLevel = level;
        MSpace s = start;
        if(s==null) {
            for(MSpace sp:getCurrentLevel().spaces()) {
                if(sp instanceof Stairs) {
                    Stairs st = (Stairs) sp;
                    if(ascended!=st.isAscending() && st.getPartition()==partition) {
                        //System.err.println("found stairs "+sp);
                        s = sp;
                        break;
                    }
                }
            }
        }
        if(s==null||s.isOccupied()) {
            s = getCurrentLevel().findRandomNormalEmptySpace();
            if(s==null||s.isOccupied()) {
                s = getCurrentLevel().findNearestEmpty(Ground.class, (MatrixMSpace) getCurrentLevel().findRandomEmptySpace());
            }
        }
        MSpace[] nsurr = s.surrounding();
        for(MSpace a:surr) {
            if(a!=null&&a.isOccupied()) {
                NHBot b = (NHBot) a.getOccupant();
                if(b.threat(_player)==Threat.familiar||Rand.d100(50)) {
                    for(MSpace pl:nsurr) {
                        if(pl!=null&&pl.isWalkable()&&!pl.isOccupied()&&b.canOccupy((NHSpace)pl)) {
                            a.clearOccupant();
                            pl.setOccupant(b);
                            break;
                        }
                    }
                }
            }
        }
        //_player.setLevel(getCurrentLevel());
        Logger.global.info(_player+" moved to level "+getCurrentLevel().getFloor());
        if(getCurrentLevel().getFloor()!=_currentLevel) {
            throw new IllegalStateException("level mismatch: "+getCurrentLevel().getFloor()+"/"+_currentLevel);
        }
        s.setOccupant(_player);

        for(GameListener gl:_listeners) {
            if(ascended) {
                gl.ascended(this);
            }
            else {
                gl.descended(this);
            }
        }
    }

    private static final String DISPLAY = "VG93ZXIKIApieSBKb2huIFdoaXRlIDxkaGNtcmxjaHRkakBnbWFpbC5jb20+CiAgCihjKSAyMDA2IEpvaG4gV2hpdGUK";

    public final void addListener(GameListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public final void removeListener(GameListener listener) {
        _listeners.remove(listener);
    }

    protected final BotFactory getFactory() {
        return Universe.getUniverse();
    }

    protected abstract Level createLevel(int floor);

    private void writeObject(ObjectOutputStream out) throws IOException {
        Item.writeStatic(out);
        AbstractFragment.writeStatic(out);

        out.defaultWriteObject();
        out.writeLong(Time.now());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Item.readStatic(in);
        AbstractFragment.readStatic(in);

        in.defaultReadObject();
        long time = in.readLong();
        Time.setTime(time);
    }

    static {
        Extended.addCommand(new String(Base64.decode("X194b3RsbW90ZAo=")).trim(), new Test());
    }

    static class Test extends DefaultNHBotAction {
        public String getDescription() {
            return "test action";
        }

        public void perform() {
            N.narrative().display(getBot(), new String(Base64.decode(DISPLAY)), true);
        }
    }
}
