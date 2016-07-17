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


public class Construct extends DefaultNHBotAction implements SpaceAction {
    static {
        Extended.addCommand("construct", new Construct());
    }

    public String getDescription() {
        return "Build a structure.";
    }

    public boolean isPerformable(NHBot b) {
        return b.getSkill("construct")>0;
    }

    public static class SpaceHolder {
        private Createable _c;


        public SpaceHolder(Createable c) {
            _c = c;
        }

        public String toString() {
            return _c.getName();
        }

        public Createable getCreateable() {
            return _c;
        }
    }

    public void perform() {
        List<String> structures = new ArrayList<String>(((Patsy)getBot()).getSpaceCatalogue());
        List<SpaceHolder> choices = new ArrayList<SpaceHolder>();
        for(int i=0;i<structures.size();i++) {
            String c = structures.get(i);
            try {
                Object o = Class.forName(c).newInstance();
                if(o instanceof Createable) {
                    Createable cr = (Createable) o;
                    if(cr.getIngredients()!=null) {
                        choices.add(new SpaceHolder(cr));
                    }
                }
            }
            catch(Exception e) {
            }
        }
        if(choices.size()>0) {
            Object[] chosen = null;
            try {
                chosen = N.narrative().choose(getBot(), "Build what?", "structure", choices.toArray(), null, 1);
            }
            catch(ActionCancelledException e) {
                N.narrative().clear();
                N.narrative().print(getBot(), "Never mind.");
                throw e;
            }
            if(chosen.length==1) {
                SpaceHolder sh = (SpaceHolder) chosen[0];
                Build b = new Build();
                b.setBot(getBot());
                b.setCreateable(sh.getCreateable());
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
