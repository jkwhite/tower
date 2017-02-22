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


import org.excelsi.aether.*;
import org.excelsi.matrix.*;


public class DiggingAction implements ProgressiveAction {
    private NHBot _b;
    private NHSpace _s;
    private Direction _d;
    private int _remaining;
    private Item _instrument;


    public DiggingAction(NHBot b, Direction d, int time, Item instrument) {
        _b = b;
        if(d!=Direction.down) {
            throw new IllegalArgumentException("can only dig down, not "+d);
        }
        _d = d;
        _remaining = time;
        _instrument = instrument;
    }

    public DiggingAction(NHBot b, NHSpace s, int time, Item instrument) {
        _b = b;
        _s = s;
        _remaining = time;
        _instrument = instrument;
    }

    public NHBot getBot() {
        return _b;
    }

    public int getInterruptRate() {
        return 100;
    }

    public Item getInstrument() {
        return _instrument;
    }

    public boolean iterate() {
        if(_s!=null&&!_s.isReplaceable()) {
            Context.c().n().print(_b, "This wall is too hard to dig into.");
            return false;
        }
        if(_d!=null&&(_b.getEnvironment().getLevel()==0||_b.getEnvironment().getLevel()>899)) {
            Context.c().n().print(_b, "This floor is too hard to dig into.");
            return false;
        }
        if(--_remaining>0) {
            return true;
        }
        else {
            if(_s!=null) {
                boolean broken = false;
                if(_s instanceof Breakable) {
                    broken = ((Breakable)_s).breakup();
                }
                if(!broken) {
                    NHSpace g = (NHSpace) _s.replace(_b.getEnvironment().getMSpace() instanceof Pit?new Pit():new Ground());
                    //N.narrative().print(_b, Grammar.start(_b, "succeed")+" in cutting away some rock.");
                    if(Rand.d100(50)) {
                        Rock r = new Rock();
                        r.setCount(Rand.om.nextInt(5)+1);
                        g.add(r);
                    }
                    if(Rand.d100(20)) {
                        SmallStone s = new SmallStone();
                        s.setCount(Rand.om.nextInt(7)+1);
                        g.add(s);
                    }
                }
            }
            else {
                //MatrixMSpace cur = (MatrixMSpace) _b.getEnvironment().getMSpace();
                NHSpace m = (NHSpace)_b.getEnvironment().getMSpace();
                if(m.getDepth()>4) {
                    if(m instanceof Water) {
                        m.replace(new Whirlpool());
                    }
                    else {
                        m.replace(new Hole());
                    }
                }
                else {
                    m.replace(new Pit());
                }
            }
            return false;
        }
    }

    public void stopped() {
    }

    public void interrupted() {
        Context.c().n().print(_b, Grammar.start(_b, "stop")+ " digging.");
    }

    public String getExcuse() {
        return null;
    }
}
