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
import org.excelsi.matrix.MSpace;


public class SligguthDaemon extends ConsumeDaemon {
    private Brain.Chemical _chem;


    public void init(java.util.Map<String,Brain.Chemical> chems) {
        _chem = chems.get("fight");
    }

    public String getChemicalSpec() {
        return "fight";
    }

    public Brain.Chemical getChemical() {
        return _chem;
    }

    protected boolean accept(Item i) {
        return super.accept(i) && i instanceof Corpse && ((Corpse)i).getSpirit()!=null;
    }

    protected void perform(Item i) {
        MSpace m = null;
        for(MSpace s:in.b.getEnvironment().getMSpace().surrounding()) {
            if(s!=null&&!s.isOccupied()&&s.isWalkable()) {
                m = s;
                break;
            }
        }
        if(m!=null) {
            if(in.b.getEnvironment().getMSpace().contains(i)) {
                in.b.getEnvironment().getMSpace().consume(i);
                N.narrative().print(in.b, Grammar.start(in.b, "envelopes")+" "+Grammar.noun(i)+".");
                NHBot b = ((Corpse)i).getSpirit();
                if(b!=null) {
                    b.setDead(false);
                    b.setHp(b.getMaxHp());
                    //b.setModel("Z");
                    b.setColor("puke-green");
                    String prefix = "zombie ";
                    if(b.isUnique()) {
                        prefix = "Zombie ";
                    }
                    if(!b.getCommon().startsWith(prefix)) {
                        b.setCommon(prefix+b.getCommon());
                    }
                    b.addAffliction(new SligguthTransformation(20));
                    m.setOccupant(b);
                    b.setThreat(in.b, Threat.familiar);
                    in.b.setThreat(b, Threat.familiar);
                    N.narrative().print(in.b, "A putrid corpse emerges from "+Grammar.noun(in.b)+"!");
                }
            }
        }
    }

    private static class SligguthTransformation extends Affliction {
        private int _time;


        public SligguthTransformation(int time) {
            super("sligguth", Affliction.Onset.tick);
            _time = time;
        }

        public void beset() {
            if(--_time==0) {
                String onoun = Grammar.noun(getBot());
                String opro = Grammar.pronoun(getBot());
                N.narrative().print(getBot(), "A gray ooze streams out from within "+onoun+", consuming "+opro+".");
                getBot().polymorph(Universe.getUniverse().createBot("sligguth"));
            }
        }

        public String getStatus() {
            return null;
        }

        public String getExcuse() {
            return null;
        }

        public void compound(Affliction a) {
        }
    }
}
