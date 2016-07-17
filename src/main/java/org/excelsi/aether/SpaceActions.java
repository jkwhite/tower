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


import java.util.*;


public class SpaceActions extends DefaultNHBotAction {
    public String getDescription() {
        return "Displays a menu of actions.";
    }

    public void perform() {
        List<SpaceAction> ga = new ArrayList<SpaceAction>();
        List<String> keys = new ArrayList<String>();
        for(Map.Entry<String,String> e:Universe.getUniverse().getActionmap().entrySet()) {
            try {
                GameAction g = (GameAction) Class.forName(e.getValue()).newInstance();
                if(g instanceof SpaceAction) {
                    SpaceAction sa = (SpaceAction) g;
                    if(sa.isPerformable(getBot())) {
                        ga.add(sa);
                        keys.add(Universe.getUniverse().keyFor(e.getKey()));
                    }
                }
            }
            catch(Exception ex) {
            }
        }
        for(Map.Entry<String,GameAction> e:Extended.getCommands().entrySet()) {
            if(e.getValue() instanceof SpaceAction) {
                SpaceAction bg = (SpaceAction) e.getValue();
                if(!ga.contains(bg)) {
                    if(bg.isPerformable(getBot())) {
                        ga.add(bg);
                        keys.add(null);
                    }
                }
            }
        }
        if(ga.size()>0) {
            Object[] choice = N.narrative().choose(getBot(), "", "Action", ga.toArray(), keys.toArray(new String[0]), 1);
            if(choice.length==1) {
                GameAction g = (GameAction) choice[0];
                if(g instanceof NHBotAction) {
                    ((NHBotAction)g).setBot(getBot());
                    g.perform();
                }
            }
            else {
                throw new ActionCancelledException();
            }
        }
        else {
            N.narrative().print(getBot(), "Nothing to do.");
            throw new ActionCancelledException();
        }
    }
}
