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


/**
 * A daemon that confronts lesser pack members over food.
 */
public class EatDaemon extends Daemon {
    private Chemical _hunger;
    private NHBot _last;

 
    public void init(java.util.Map<String,Chemical> chems) {
        _hunger = chems.get("hunger");
    }

    public String getChemicalSpec() {
        return "hunger";
    }

    public void poll(final Context c) {
        if(in.attack!=null&&in.attack.getWeapon().toItem() instanceof Comestible
            && Hunger.Degree.degreeFor(in.b.getHunger())!=Hunger.Degree.satiated) {
            strength = 1;
            _last = in.attack.getAttacker();
        }
        else {
            strength = -1;
            _last = null;
        }
    }

    public void perform(final Context c) {
        Comestible com = (Comestible) in.attack.getWeapon().toItem();
        in.b.getInventory().add(com);
        Consume c = new Consume(com);
        c.setBot(in.b);
        c.perform();
        if(_last!=null&&in.b.threat(_last)!=Threat.kos) {
            in.b.setThreat(_last, Threat.friendly);
            ((NPC)in.b).setFamiliar(_last);
        }
    }

    public Chemical getChemical() {
        return _hunger;
    }
}
