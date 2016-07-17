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


public class Battery extends Tool {
    public int _uses = Rand.om.nextInt(4)+3;


    public Battery() {
    }

    public float getSize() {
        return 0.1f;
    }

    public float getWeight() {
        return 0.1f;
    }

    public void setUses(int uses) {
        _uses = uses;
    }

    public int getUses() {
        return _uses;
    }

    public String getColor() {
        return "orange";
    }

    public int getFindRate() {
        return 8;
    }

    public int score() {
        return 50;
    }

    public void use(final NHBot b) {
        if(b.getEnvironment()!=null) {
            NHSpace s = b.getEnvironment().getMSpace();
            if(s instanceof Chargeable) {
                Chargeable d = (Chargeable) s;
                if(N.narrative().confirm(b, "There is "+Grammar.nonspecific(d.getName())+" here. Replace its battery?")) {
                    recharge(d, b);
                    //return true;
                    return;
                }
            }
            for(Parasite p:s.getParasites()) {
                if(p instanceof Chargeable) {
                    Chargeable d = (Chargeable) p;
                    if(N.narrative().confirm(b, "There is "+Grammar.nonspecific(d.getName())+" here. Replace its battery?")) {
                        recharge(d, b);
                        //return true;
                        return;
                    }
                }
            }
        }
        Chargeable chosen = (Chargeable) N.narrative().choose(b, new ItemConstraints(
            b.getInventory(), "recharge", new ItemFilter() {
                public boolean accept(Item i, NHBot bot) {
                    return i instanceof Chargeable;
                }
            }), false);
        N.narrative().print(b, Grammar.start(b, "replace")+" the "+chosen.getName()+"'s battery.");
        recharge(chosen, b);
    }

    private void recharge(Chargeable chosen, NHBot b) {
        int charge = getUses();
        switch(getStatus()) {
            case blessed:
                charge += 3;
                break;
            case uncursed:
                break;
            case cursed:
                charge = 0;
                N.narrative().print(b, "Defective battery!");
                if(chosen instanceof Item&&Rand.d100(50)) {
                    N.narrative().print(b, "The battery corrodes the "+chosen.getName()+".");
                    b.getInventory().consume((Item)chosen);
                    b.getInventory().consume(this);
                    return;
                }
                break;
        }
        if(chosen instanceof WandOfWishing) {
            charge = Rand.d100(3)?1:0;
        }
        chosen.setCharges(charge);
        b.getInventory().consume(this);
    }
}
