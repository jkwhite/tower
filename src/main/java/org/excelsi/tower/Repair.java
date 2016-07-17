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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Repair extends DefaultNHBotAction {
    static {
        Extended.addCommand("repair", new Repair());
    }

    public String getDescription() {
        return "Repair a damaged or vanquished animatron.";
    }

    public void perform() {
        Direction d = N.narrative().direct(getBot(), "Which direction?");
        NHSpace s = (NHSpace) getBot().getEnvironment().getMSpace().move(d);
        if(s!=null) {
            if(s.isOccupied()) {
                NHBot b = s.getOccupant();
                if(b.getForm() instanceof Mech) {
                    if(b.getHp()<b.getMaxHp()) {
                        N.narrative().printf(getBot(), "%n patch up %n.", getBot(), b);
                        b.setHp(Math.min(b.getMaxHp(), b.getHp()+Math.max(1, Rand.om.nextInt(getBot().getSkill("robotics")/2))));
                    }
                    else {
                        N.narrative().printf(getBot(), "That's already in tip-top shape.");
                    }
                }
                else {
                    N.narrative().printf(getBot(), "That's not repairable.");
                }
            }
            else {
                boolean found = false;
                for(Item i:s.getItem()) {
                    if(i instanceof Corpse) {
                        Corpse c = (Corpse) i;
                        if(c.getSpirit()!=null) {
                            NHBot b = c.getSpirit();
                            if(b.getCommon().startsWith("animatronic ")) {
                                found = true;
                                if(N.narrative().confirm(getBot(), "Repair the "+b.getCommon()+"?")) {
                                    getBot().start(new Repairing(getBot(), s, c));
                                    break;
                                }
                            }
                        }
                    }
                }
                if(!found) {
                    N.narrative().printf(getBot(), "Nothing there needs repairing.");
                }
            }
        }
    }
}
