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


public class Manufacture extends DefaultNHBotAction implements SpaceAction {
    public String getDescription() {
        return "Build an item from raw materials.";
    }

    public boolean isPerformable(NHBot b) {
        return ((Patsy)b).getItemCatalogue().size()>0;
    }

    public void perform() {
        List choices = new ArrayList(((Patsy)getBot()).getItemCatalogue());
        if(choices.size()>0) {
            Object[] chosen = null;
            try {
                chosen = N.narrative().choose(getBot(), "Build what?", "item", choices.toArray(), null, 1);
            }
            catch(ActionCancelledException e) {
                N.narrative().clear();
                N.narrative().print(getBot(), "Never mind.");
                throw e;
            }
            if(chosen.length==1) {
                String t = (String) chosen[0];
                    //create = (Createable) Class.forName("org.excelsi.tower."+Character.toUpperCase(t.charAt(0))+t.substring(1)).newInstance();
                Createable create = (Createable) Item.forName(t);
                if(create==null) {
                    //throw new IllegalStateException("unable to instantiate recorded item", e);
                    N.narrative().printf(getBot(), "Somehow that item doesn't want to pop into existence.");
                    return;
                }
                Build b = new Build();
                b.setCreateable(create);
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
