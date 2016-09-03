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
import static org.excelsi.aether.Brain.*;


public class YarnDaemon extends EatDaemon {
    private NHBot _last;


    @Override public void poll(final Context c) {
        super.poll(c);
        if(strength==-1) {
            if(in.attack!=null&&in.attack.getWeapon().toItem() instanceof BallOfYarn) {
                strength = 1;
                _last = in.attack.getAttacker();
            }
        }
        else {
            _last = null;
        }
    }

    @Override public void perform(final Context c) {
        if(_last==null) {
            super.perform(c);
        }
        else {
            BallOfYarn com = (BallOfYarn) in.attack.getWeapon().toItem();
            in.b.getInventory().add(com);
            Entranced e = new Entranced(in.b, com);
            in.b.start(e);
            //NARRATIVE
            //N.narrative().print(in.b, Grammar.start(in.b, "play")+" with "+Grammar.noun(com)+".");
            c.n().print(in.b, Grammar.start(in.b, "play")+" with "+Grammar.noun(com)+".");
            if(_last!=null&&in.b.threat(_last)!=Threat.kos) {
                in.b.setThreat(_last, Threat.friendly);
                ((NPC)in.b).setFamiliar(_last);
            }
        }
    }
}
