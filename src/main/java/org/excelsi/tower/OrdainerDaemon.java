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
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;


public class OrdainerDaemon extends AttackDaemon {
    private long _last = -1;


    public void OrdainerDaemon() {
    }

    @Override public void poll(final Context c) {
        super.poll(c);
        if(_last==-1) {
            _last = Time.now();
        }
        if(strength>=0) {
            MSpace f = in.b.getEnvironment().getMSpace();
            MSpace t = in.important.getEnvironment().getMSpace();
            if(f.isAdjacentTo(t)) {
                if(Time.now()>_last+10) {
                    strength *= 2;
                }
                else {
                    strength = -1;
                }
            }
            else {
                strength = -1;
            }
        }
    }

    @Override public void perform(final Context c) {
        if(in.important!=null&&!in.important.isDead()) {
            _last = Time.now();
            in.b.getEnvironment().face(in.important);
            if(Rand.d100(in.b.getIntuition()-in.important.getIntuition())) {
                //NARRATIVE
                //N.narrative().print(in.b, Grammar.start(in.b, "tear")+" "+Grammar.possessive(in.important)+" soul from "+Grammar.possessiveIndirect(in.important)+" body.");
                c.n().print(in.b, Grammar.start(in.b, "tear")+" "+Grammar.possessive(in.important)+" soul from "+Grammar.possessiveIndirect(in.important)+" body.");
                in.important.die("Absolved by "+Grammar.nonspecific(in.b));
            }
        }
    }
}
