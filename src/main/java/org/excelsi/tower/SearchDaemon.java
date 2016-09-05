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
import static org.excelsi.aether.Brain.*;


public abstract class SearchDaemon extends Daemon {
    private Item _com = null;
    private NHSpace _dest = null;
    private int _lookAround = Rand.om.nextInt(5)+5;


    public SearchDaemon() {
        this(Rand.om.nextInt(5)+5);
    }

    public SearchDaemon(int period) {
        _lookAround = period;
    }

    public void poll(final Context c) {
        if(in.attack!=null) {
            strength = -1;
            return;
        }
        if(_dest==null) {
            strength = -1;
            if(in.attack==null&&Hunger.Degree.degreeFor(in.b.getHunger())!=Hunger.Degree.satiated) {
                NHSpace s = in.b.getEnvironment().getMSpace();
                checkSpace(s);
                if(strength==0&&--_lookAround<=0) {
                    _lookAround = 5;
                    for(NHSpace sp:in.b.getEnvironment().getVisible()) {
                        checkSpace(sp);
                        if(strength!=-1) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void checkSpace(NHSpace s) {
        if(s.numItems()>0) {
            for(Item i:s.getItem()) {
                if(accept(i)) {
                    _dest = s;
                    _com = i;
                    strength = strengthFor(i);
                    return;
                }
            }
        }
    }

    @Override public void perform(final Context c) {
        if(_dest==in.b.getEnvironment().getMSpace()) {
            _com = null;
            checkSpace(_dest);
            if(_com!=null) {
                perform(c, _com);
            }
            _dest = null;
            _lookAround = 0;
        }
        else if(_dest!=null) {
            if(!((NPC)in.b).approach(_dest, 99)) {
                _dest = null;
                _lookAround = 0;
            }
        }
    }

    protected abstract boolean accept(Item i);

    protected abstract int strengthFor(Item i);

    protected abstract void perform(Context c, Item i);
}

