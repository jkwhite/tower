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


import org.excelsi.matrix.MSpace;
import org.excelsi.aether.*;


/**
 * Searches for hidden parasites and spaces in a bot's immediate vicinity.
 */
public class Search extends AbstractInstantaneousAction implements SpaceAction {
    public String getDescription() {
        return "Search for secret doors, passageways, and traps.";
    }

    public boolean isPerformable(NHBot b) {
        return true;
    }

    public void perform() {
        super.perform();
        int skill = getBot().getSkill("detect");
        for(MSpace s:getBot().getEnvironment().getMSpace().surrounding()) {
            if(s!=null) {
                if(s instanceof Secret) {
                    if(Rand.d100(skill)) {
                        ((Secret)s).reveal();
                        getBot().skillUp("detect");
                    }
                }
                else {
                    for(Parasite p:((NHSpace)s).getParasites()) {
                        if(p.isHidden()) {
                            if(Rand.d100(skill)) {
                                p.setHidden(false);
                                getBot().skillUp("detect");
                            }
                        }
                    }
                }
            }
        }
    }
}
