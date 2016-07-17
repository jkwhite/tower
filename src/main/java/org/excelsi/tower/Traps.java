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
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.MSpace;
import java.util.ArrayList;
import java.util.List;


public class Traps implements Mixin {
    static {
        Extended.addCommand("trap", new BuildTrap());
    }

    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        Level l = (Level) o;
        NHSpace s;
        while(Rand.d100(40)) {
            s = (NHSpace) l.findEmptierSpace();
            if(s==null) {
                return; // out of room
            }
            int type = Rand.d100();
            if(type<50) {
                s.addParasite(new DartTrap());
            }
            else if(type<=80) {
                s.addParasite(new PitTrap());
            }
            else {
                s.addParasite(new BoardTrap());
            }
        }
    }

    public static class BuildTrap extends DefaultNHBotAction implements SpaceAction {
        public String getDescription() {
            return "Build a trap.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getSkill("traps")>0;
        }

        public void perform() {
            String[] known = ((Patsy)getBot()).getCatalogue("traps").toArray(new String[0]);
            if(known.length==0) {
                N.narrative().print(getBot(), "You don't know any traps!");
                throw new ActionCancelledException();
            }
            Trap[] choose = new Trap[known.length];
            for(int i=0;i<known.length;i++) {
                try {
                    choose[i] = (Trap) Class.forName(known[i]).newInstance();
                }
                catch(Exception e) {
                    java.util.logging.Logger.global.severe(e.toString());
                }
            }
            Trap type = (Trap) N.narrative().choose(getBot(), "Build what kind of trap?", "trap", choose, null, 1)[0];
            Direction d = N.narrative().direct(getBot(), "Which direction?");
            final MSpace s = getBot().getEnvironment().getMSpace().move(d);
            if(s!=null&&s.isWalkable()) {
                //Trap tocreate = null;
                //if(type.equals("dart")) {
                    //tocreate = new DartTrap();
                //}
                //else if(type.equals("board")) {
                    //tocreate = new BoardTrap();
                //}
                //else if(type.equals("pit")) {
                    //tocreate = new PitTrap();
                //}
                //else {
                    //N.narrative().print(getBot(), "Hmm... how did that one go again?");
                    //throw new ActionCancelledException();
                //}
                //final Trap t = tocreate;
                final Trap t = type;
                Build.build(t, getBot(), new Build.Finish() {
                    public void done() {
                        ((NHSpace)s).addParasite(t);
                        t.setHidden(false);
                        if(!Rand.d100(getBot().getSkill("traps"))&&!Rand.d100(getBot().getModifiedAgility())) {
                            N.narrative().print(getBot(), "Oops!");
                            t.trigger(getBot());
                        }
                        else {
                            N.narrative().print(getBot(), "The "+t.getName()+" is set.");
                        }
                    }

                    public void fail() {
                        N.narrative().print(getBot(), "How'd that go again?");
                    }
                });
            }
            else {
                N.narrative().print(getBot(), "That's no place for a trap!");
            }
        }

        public String toString() {
            return "Build trap";
        }
    }
}
