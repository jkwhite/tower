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


public class ScrollOfIdentify extends Scroll {
    public int score() { return 40; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Diamond()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        if(checkSacrifice(b)) {
            return;
        }
        int times = 1;
        String suf = "";
        switch(getStatus()) {
            case blessed:
                times = Rand.om.nextInt(2)+2;
                suf = " first";
            case uncursed:
                while(times-->0) {
                    Item chosen = N.narrative().choose(b, new ItemConstraints(b.getInventory(), "What do you want to identify"+suf+"?",
                        "That is already identified.", new ItemFilter() {
                            public boolean accept(Item i, NHBot bot) {
                                if(!i.isIdentified()) {
                                    return true;
                                }
                                for(Fragment f:i.getFragments()) {
                                    if(!f.isIdentified()) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        }), false
                    );
                    suf = " next";
                    chosen.setIdentified(true);
                    chosen.setClassIdentified(true);
                    /*
                    for(Fragment f:chosen.getFragments()) {
                        f.setIdentified(true);
                        f.setClassIdentified(true);
                    }
                    */
                    if(times>0) {
                        N.narrative().printfm(b, "%K", b.getInventory(), chosen);
                    }
                    else {
                        N.narrative().printf(b, "%K", b.getInventory(), chosen);
                    }
                }
                break;
            case cursed:
                Item[] its = b.getInventory().getItem();
                int i = Rand.om.nextInt(its.length);
                its[i].setClassIdentified(false);
                its[i].setIdentified(false);
                if(b.isPlayer()) {
                    if(b.getWielded()==its[i]) {
                        N.narrative().print(b, "Oh! You're holding "+its[i]+".");
                    }
                    else if(b.isEquipped(its[i])) {
                        N.narrative().print(b, "You suddenly realize you're wearing "+its[i]+".");
                    }
                    else {
                        N.narrative().print(b, "Wow! You have "+its[i]+".");
                    }
                    //N.narrative().print(b, "Where did that come from?");
                }
                break;
        }
    }

    public void sacrifice(NHBot b, Altar a) {
        super.sacrifice(b, a);
        int total = 0;
        for(Item i:a.getItem()) {
            if(i instanceof Corpse) {
                Corpse c = (Corpse) i;
                a.destroy(c);
                total++;
            }
        }
        for(Item i:a.getItem()) {
            switch(getStatus()) {
                case blessed:
                case uncursed:
                    i.setIdentified(true);
                    i.setClassIdentified(true);
                    break;
                case cursed:
                    i.setIdentified(false);
                    i.setClassIdentified(false);
                    break;
            }
            if(--total==0) {
                break;
            }
        }
        if(b.isPlayer()) {
            if(getStatus()==Status.cursed) {
                N.narrative().print(b, "Some strange items appear on the altar...");
            }
            else {
                N.narrative().print(b, "You sense a strange familiarity with the sacrificial items...");
            }
        }
    }
}
