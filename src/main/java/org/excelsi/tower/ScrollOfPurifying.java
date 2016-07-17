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


public class ScrollOfPurifying extends Scroll {
    public int score() { return 60; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Pearl()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        if(checkSacrifice(b)) {
            return;
        }
        int times = 1;
        boolean done = false;
        switch(getStatus()) {
            case blessed:
                times = Rand.om.nextInt(2)+2;
            case uncursed:
                for(Item i:b.getInventory().randomized()) {
                    if(i.getStatus()==Status.cursed) {
                        i.setStatus(Status.uncursed);
                        N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" "+i.getName()+" glows "+Status.uncursed.getColor()+".");
                        done = true;
                        if(--times==0) {
                            break;
                        }
                    }
                }
                break;
            case cursed:
                for(Item i:b.getInventory().randomized()) {
                    if(i.getStatus()!=Status.cursed) {
                        i.setStatus(i.getStatus().worse());
                        N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" "+i.getName()+" glows "+i.getStatus().getColor()+".");
                        done = true;
                        break;
                    }
                }
                break;
        }
        if(!done) {
            N.narrative().print(b, Grammar.start(b, "feel")+" clean all over.");
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
        boolean purified = false;
        for(Item i:a.getItem()) {
            switch(getStatus()) {
                case blessed:
                case uncursed:
                    if(i.getStatus()==Status.cursed) {
                        i.setStatus(Status.uncursed);
                        purified = true;
                    }
                    break;
                case cursed:
                    i.setStatus(Status.cursed);
                    purified = true;
                    break;
            }
            if(--total==0) {
                break;
            }
        }
        if(b.isPlayer()) {
            if(purified) {
                if(getStatus()==Status.cursed) {
                    N.narrative().print(b, "A black glow envelopes the sacrificial items.");
                }
                else {
                    N.narrative().print(b, "A yellow glow dances over the sacrificial items.");
                }
            }
            else {
                N.narrative().print(b, "Nothing happens.");
            }
        }
    }
}
