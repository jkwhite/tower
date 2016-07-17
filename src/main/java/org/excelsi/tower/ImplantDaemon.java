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


public class ImplantDaemon extends AttackDaemon {
    private long _last = -1;
    private static final int PERIOD = 20;


    public void poll() {
        super.poll();
        if(_last==-1) {
            _last = Time.now();
        }
        if(strength>=0) {
            MSpace f = in.b.getEnvironment().getMSpace();
            MSpace t = in.important.getEnvironment().getMSpace();
            if(f.isAdjacentTo(t)) {
                if(Time.now()>_last+PERIOD) {
                    strength *= 2;
                }
            }
        }
    }

    public void run() {
        if(Time.now()>_last+PERIOD) {
            if(in.important!=null&&!in.important.isDead()) {
                _last = Time.now();
                in.b.getEnvironment().face(in.important);
                if(Rand.d100(in.b.getIntuition()-in.important.getIntuition())) {
                    N.narrative().print(in.b, Grammar.start(in.b, "tear")+" "+Grammar.possessive(in.important)+" soul from "+Grammar.possessive(in.important)+" body.");
                    in.important.die("Soul destroyed by "+Grammar.nonspecific(in.b));
                }
            }
        }
        else {
            super.run();
        }
    }
}
