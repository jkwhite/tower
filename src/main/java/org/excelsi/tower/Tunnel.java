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
package org.excelsi.tower;


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;


public class Tunnel extends Floor {
    private static final long serialVersionUID = 1L;
    private Tunnel _mfrom;
    private Tunnel _mto;
    private int _from;
    private int _to;
    private boolean _entering;


    public Tunnel(int from, int to) {
        _from = from;
        _to = to;
        _mfrom = this;
    }

    public Tunnel(int from, int to, Tunnel mto) {
        this(from, to);
        _mto = mto;
        mto.setDestination(this);
    }

    public void setDestination(Tunnel to) {
        _mto = to;
    }

    public void setEntering(boolean entering) {
        _entering = entering;
    }

    public void trigger() {
        super.trigger();
        NHBot b = getOccupant();
        if(b!=null) {
            if(!b.isPlayer()) {
                // gets too complicated otherwise
                return;
            }
            if(_entering) {
                _entering = false;
            }
            else {
                if(_mto==null) {
                    Level tl = b.getEnvironment().getFloor(_to);
                    MSpace end = tl.findRandomNormalEmptySpace();
                    Tunnel t = new Tunnel(_to, _from, this);
                    t.setEntering(true);
                    _mto = t;
                    end.replace(t);
                }

                _mto.setEntering(true);
                b.getEnvironment().setLevel(_to, _mto);
                /*
                if(_mto==null) {
                    MSpace end = findEnd(b);
                    Tunnel t = new Tunnel(_to, _from, this);
                    t.setEntering(true);
                    _mto = t;
                    end.replace(t);
                }
                _mto.setOccupant(b);
                */
            }
        }
    }
    
    private MSpace findEnd(NHBot b) {
        MSpace ns = ((NHEnvironment)b.getEnvironment()).getMSpace();
        if(ns.isReplaceable()) {
            return ns;
        }
        ArrayList li = new ArrayList<MSpace>(Arrays.asList(ns.surrounding()));
        MSpace end = null;
        /*
        for(MSpace s:li) {
            if(s!=null && !(s instanceof Tunnel) && !(s instanceof Stairs) && !s.isOccupied()) {
                end = s;
                break;
            }
        }
        */
        if(end==null) {
            end = ((Patsy)b).getLevel().findRandomNormalEmptySpace();
        }
        return end;
    }

    public String getModel() {
        return "0";
    }

    public String getColor() {
        return "light-green";
    }

    public int getDepth() {
        return 0;
    }

    public boolean look(NHBot b) {
        N.narrative().print(b, "There is a tunnel here.");
        return true;
    }

    public int add(Item i) {
        super.add(i);
        destroy(i);
        return -1;
    }

    public int add(Item i, NHBot adder) {
        super.add(i, adder);
        destroy(i);
        return -1;
    }

    public int add(Item item, NHBot adder, NHSpace origin) {
        super.add(item, adder, origin);
        destroy(item);
        return -1;
    }

    public void update() {
        if(!isOccupied()&&Rand.d100(1)) {
            try {
                NHBot b = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                    public boolean accept(NHBot b) {
                        return !b.isPlayer() && (b.getMinLevel()<=_to && b.getMaxLevel()>=_to);
                    }
                });
                setOccupant(b);
            }
            catch(NoSuchBotException e) {
            }
        }
    }
}
