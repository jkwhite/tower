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
import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


public class Fabricator extends Floor implements Device {
    private static final long serialVersionUID = 1L;

    private Set<String> _knowns;


    public Fabricator() {
        this(false);
    }

    public Fabricator(boolean universal) {
        if(!universal) {
            _knowns = new HashSet<String>();
        }
    }

    public String getModel() {
        return "a]";
    }

    public String getColor() {
        return "light-blue";
    }

    public boolean look(final Context c, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(c, nothing, lootOnly);
        if(!lootOnly) {
            c.n().print(this, "There is a fabricator here.");
            return true;
        }
        return ret;
    }

    public String getName() {
        return "fabricator";
    }

    public int add(Item i, NHBot adder) {
        if(i instanceof Corpse) {
            String light = "green";
            Corpse c = (Corpse) i;
            if(c.getSpirit()!=null) {
                if(_knowns!=null) {
                    _knowns.add(c.getSpirit().getCommon());
                }
            }
            else {
                light = "red";
            }
            adder.getInventory().consume(i);
            N.narrative().print(adder, Grammar.first(Grammar.noun(i))+" vanishes in a "+light+" flash of light.");
            return -1;
        }
        return super.add(i, adder);
    }

    public void use(NHBot b) {
        String in = N.narrative().reply(b, "FAB> ");
        final Request r = Grammar.parseRequest(in);
        try {
            String noun = r.getNoun();
            boolean random = false;
            if("?".equals(noun)) {
                if(_knowns==null) {
                    random = true;
                }
                else {
                    if(_knowns.size()==0) {
                        throw new NoSuchBotException("no bots yet");
                    }
                    List<String> ra = new ArrayList<String>(_knowns);
                    noun = ra.get(Rand.om.nextInt(ra.size()));
                }
            }
            if(_knowns!=null&&!_knowns.contains(noun)) {
                throw new NoSuchBotException("fabricator instance missing data");
            }
            final boolean ran = random;
            final String nou = noun;
            BotFactory.Constraints c = new BotFactory.Constraints() {
                public boolean accept(NHBot b) {
                    return ran||b.getCommon().equals(nou);
                }
            };
            for(int i=0;i<r.getCount();i++) {
                NHBot fab = Universe.getUniverse().createBot(c);
                MSpace m = ((Level)getMatrix()).findNearestEmpty(Floor.class, this);
                m.setOccupant(fab);
            }
        }
        catch(NoSuchBotException e) {
            N.narrative().print(b, "ERR> NO DATA");
        }
    }

    public Set<String> getKnowns() {
        return _knowns;
    }
}
