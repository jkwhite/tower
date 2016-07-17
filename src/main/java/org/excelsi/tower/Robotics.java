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
import java.util.*;


public class Robotics extends DefaultNHBotAction implements SpaceAction {
    static {
        Extended.addCommand("robotics", new Robotics());
    }

    public String getDescription() {
        return "Build a robot from raw materials.";
    }

    public boolean isPerformable(NHBot b) {
        return b.getSkill("robotics")>0;
    }

    public void perform() {
        List choices = new ArrayList(((Patsy)getBot()).getCatalogue());
        choices.addAll(((Patsy)getBot()).getAnim().values());
        if(choices.size()>0) {
            Object[] chosen = null;
            try {
                chosen = N.narrative().choose(getBot(), "Build what?", "robot", choices.toArray(), null, 1);
            }
            catch(ActionCancelledException e) {
                N.narrative().clear();
                N.narrative().print(getBot(), "Never mind.");
                throw e;
            }
            if(chosen.length==1) {
                boolean anim = true;
                if(chosen[0] instanceof String) {
                    anim = false;
                    String name = (String) chosen[0];
                    //chosen[0] = Universe.getUniverse().createBot((String)chosen[0]);
                    for(NHBot b:Universe.getUniverse().getBots()) {
                        if(b.getCommon().equals(name)) {
                            chosen[0] = b;
                            break;
                        }
                    }
                }
                NHBot ch = (NHBot) chosen[0];
                Build b = new Build();
                ch = DefaultNHBot.copy(ch);
                ch.setHp(ch.getMaxHp());
                ch.setColor("silver");
                List<Item> ing = new ArrayList<Item>(Arrays.asList((Item[])DefaultNHBot.deepCopy(ch.getPack())));
                //System.err.println("ING: "+ing);
                if(!anim) {
                    for(int i=0;i<ing.size();i++) {
                        if(ing.get(i) instanceof Corpse) {
                            ing.remove(i);
                            i--;
                        }
                    }
                }
                b.setCreateable(new Build.CreateableBot(ch, (Item[]) ing.toArray(new Item[0]), "robotics"));
                b.setBot(getBot());
                b.perform();
            }
            else {
                N.narrative().print(getBot(), "Nothing to build.");
            }
        }
        else {
            N.narrative().print(getBot(), "You're fresh out of ideas!");
        }
    }
}
