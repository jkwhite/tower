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


public class Alchemy extends DefaultNHBotAction implements SpaceAction {
    static {
        //Extended.addCommand("alchemy", new Alchemy());
    }

    public String getDescription() {
        return "Create a creature through alchemy.";
    }

    public boolean isPerformable(NHBot b) {
        //return b.getSkill("alchemy")>0;
        return false;
    }

    public void perform() {
        List<NHBot> choices = new ArrayList<NHBot>();
        for(NHBot bot:Universe.getUniverse().getBots()) {
            if(!(bot.getForm() instanceof Mech)) {
                if(((Patsy)getBot()).getKills().containsKey(bot.getCommon())) {
                    choices.add(bot);
                }
            }
        }
        if(choices.size()>0) {
            try {
                Object[] chosen = N.narrative().choose(getBot(), "Create what?", "monster", choices.toArray(), null, 1);
                if(chosen.length==1) {
                    NHBot c = (NHBot) chosen[0];
                    Build b = new Build();
                    b.setCreateable(new Build.CreateableBot(c, null, null));
                    b.setBot(getBot());
                    b.perform();
                }
                else {
                    N.narrative().print(getBot(), "Nothing to create.");
                }
            }
            catch(ActionCancelledException e) {
                N.narrative().print(getBot(), "Never mind.");
            }
        }
        else {
            N.narrative().print(getBot(), "Nothing comes to mind.");
        }
    }
}
