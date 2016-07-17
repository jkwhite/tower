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


import org.excelsi.matrix.*;
import static org.excelsi.aether.Brain.*;
import java.util.logging.Logger;


public class WanderDaemon extends Daemon {
    private Chemical _basic;
    protected NHSpace _dest;
    private boolean _rooms = true;
    private boolean _oblivious = false;
    private int _frequency = 50;
    private int _travel = 2;
    private static DefaultNHBot.MoveAction _move = new DefaultNHBot.Forward();


    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public void setRooms(boolean rooms) {
        _rooms = rooms;
    }

    public boolean isRooms() {
        return _rooms;
    }

    public void setOblivious(boolean o) {
        _oblivious = o;
    }

    public boolean isOblivious() {
        return _oblivious;
    }

    public int getFrequency() {
        return _frequency;
    }

    public void setFrequency(int frequency) {
        _frequency = frequency;
    }

    public void setTravel(int travel) {
        _travel = travel;
    }

    public int getTravel() {
        return _travel;
    }

    public void poll() {
        if(in.important!=null&&(in.b.threat(in.important)!=Threat.kos)||(in.attack==null&&in.b.isBlind())) {
            strength = 1;
        }
        else {
            strength = -1;
        }
        //System.err.println(in.b+" str: "+strength);
    }

    public void run() {
        //System.err.println(in.b+" DEST: "+_dest+", travel="+_travel);
        if(_dest!=null) {
            approachDest();
            return;
        }
        if(Rand.d100(_frequency)) {
            MSpace m = in.b.getEnvironment().getMSpace();
            if(!m.isOccupied()) {
                Logger.global.severe(in.b+" has come unstuck");
                return;
            }
            MSpace[] sur = m.surrounding();
            int i = Rand.om.nextInt(sur.length);
            for(int j=0;j<sur.length;j++) {
                if(sur[i]!=null&&sur[i].isWalkable()&&accept(sur[i])) {
                    in.b.getEnvironment().face(sur[i]);
                    MSpace sp = in.b.getEnvironment().getMSpace().move(in.b.getEnvironment().getFacing());
                    if(sp!=null&&(in.b.isBlind()||!sp.isOccupied())) {
                        _move.setBot(in.b);
                        _move.perform();
                    }
                    break;
                }
                if(++i==sur.length) {
                    i = 0;
                }
            }
        }
        else if(_dest==null&&Rand.d100(_travel)) {
            _dest = findNewSpace();
            //System.err.println(in.b+" DEST: "+_dest);
        }
    }

    protected void approachDest() {
        if(!((NPC)in.b).approach(_dest, 100, false)) {
            // can't make it for some reason
            _dest = null;
        }
    }

    protected NHSpace findNewSpace() {
        NHSpace dest = null;
        Level lev = (Level) ((MatrixMSpace)in.b.getEnvironment().getMSpace()).getMatrix();
        if(_rooms) {
            dest = (NHSpace) lev.findRandomNormalEmptySpace();
        }
        else {
            dest = (NHSpace) lev.findRandomEmptySpace();
        }
        return dest;
    }

    public Chemical getChemical() {
        return _basic;
    }

    protected boolean accept(MSpace s) {
        return true;
    }
}
