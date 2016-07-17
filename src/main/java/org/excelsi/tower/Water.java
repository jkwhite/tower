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


public class Water extends Liquid implements Drinkable, Surface {
    private static final long serialVersionUID = 1L;
    //private Status _status = Status.uncursed;


    public Water() {
        this(8, 0f);
    }

    public Water(int depth, float cycle) {
        super("blue", depth, cycle);
    }

    public Water(int depth, Status status) {
        this(depth, 0f);
        setStatus(status);
    }

    /*
    public Status getStatus() {
        return _status;
    }

    public void setStatus(Status s) {
        _status = s;
    }
    */

    public String getColor() {
        return getDepth()<=4?"light-blue":"blue";
    }

    public float getShininess() {
        return 1f;
    }

    public int add(Item i) {
        int ret = super.add(i);
        dissolve(i, null, this, true);
        return ret;
    }

    public int add(Item i, NHBot adder) {
        int ret = super.add(i, adder);
        dissolve(i, adder, this, true);
        return ret;
    }

    public void addLoot(Container loot) {
        if(loot!=null) {
            for(Item i:loot.getItem()) {
                dissolve(i, null, loot, true);
            }
            super.addLoot(loot);
        }
    }

    public void pour(NHBot adder, Potion p) {
        N.narrative().print(adder, Grammar.start(adder, "pour out")+" "+Grammar.specific(p)+".");
        List<Infliction> infs = p.removeInflictions();
        Solution s = new Solution(((Fermionic)infs.get(0)).getColor());
        s.setEvaporationVerb("dissipates");
        addParasite(s);
        WaterInfliction wi = new WaterInfliction();
        wi.setStatus(getStatus());
        s.add(wi);
        for(Infliction i:infs) {
            s.add(i);
        }
    }

    public void immerse(NHBot b, Item i) {
        WaterInfliction wi = new WaterInfliction();
        wi.setStatus(getStatus());
        if(i instanceof Potion) {
            Potion p = (Potion) i;
            if(p.isEmpty()) {
                p.addFragment(wi);
                N.narrative().print(b, "The cool water fills the "+p.getCategory()+".");
            }
            else {
                N.narrative().print(b, "That potion is already full.");
            }
        }
        else {
            wi.apply(i, b);
        }
        if(getStatus()==Status.blessed&&Rand.d100(33)) {
            setStatus(Status.uncursed);
        }
    }

    public String getName() {
        return "water";
    }

    public void drink(NHBot b) {
        switch(getStatus()) {
            case blessed:
                if(b.isPlayer()) {
                    N.narrative().print(b, "The cool draught refreshes you.");
                }
                b.setHunger(b.getHunger()-Hunger.RATE/5);
                for(Item i:b.getInventory().randomized()) {
                    if(i.getStatus()==Status.cursed) {
                        i.setStatus(Status.uncursed);
                        setStatus(Status.uncursed);
                        if(b.isPlayer()) {
                            N.narrative().print(b, "Your pack feels lighter.");
                        }
                        break;
                    }
                }
                break;
            case uncursed:
                if(b.isPlayer()) {
                    N.narrative().print(b, "The cool draught refreshes you.");
                }
                b.setHunger(b.getHunger()-Hunger.RATE/5);
                break;
            case cursed:
                if(Rand.d100((50+b.getModifiedPresence())/2)) {
                    for(Item i:b.getInventory().randomized()) {
                        if(i.getStatus()!=Status.cursed) {
                            i.setStatus(Status.cursed);
                            break;
                        }
                    }
                }
                if(Rand.d100((50+b.getModifiedConstitution())/2)) {
                    if(b.isPlayer()) {
                        N.narrative().print(b, "Ugh! Sewer water!");
                    }
                    b.addAffliction(new Delay(new Nauseous(Rand.om.nextInt(10)+12), 1+Rand.om.nextInt(6)));
                }
                if(b.isPlayer()) {
                    N.narrative().print(b, "This water tastes foul.");
                }
                break;
        }
        if(!b.isPlayer()) {
            N.narrative().print(b, Grammar.start(b, "drink")+" from the water.");
        }
    }

    protected void soak(boolean say) {
        NHBot b = (NHBot) getOccupant();
        if(b.getModifiedWeight()<1) {
            if(say) {
                N.narrative().print(b, Grammar.start(b, "float")+" on the water's surface!");
            }
        }
        else if(getDepth()>4) {
            if(!b.isLevitating()&&!b.isAirborn()) {
                if(b.getInventory()!=null) {
                    for(Item i:b.getInventory().getItem()) {
                        dissolve(i, b, b.getInventory(), false);
                    }
                }
                Habitat h = b.getForm().getHabitat();
                if(h==Habitat.terrestrial&&b.getSize()==Size.small) {
                    // inflict 10% of current hp each turn with a minimum
                    // of 6 dmg per turn
                    b.setHp(Math.max(0, b.getHp()-Math.max(6, b.getHp()/10)));
                    if(b.getHp()>0) {
                        N.narrative().print(b, Grammar.startToBe(b)+" drowning!");
                    }
                    else {
                        N.narrative().print(b, Grammar.start(b, "drown")+".");
                        b.die("Drowned");
                    }
                }
            }
        }
    }

    protected Water spread() {
        Water w = new Water(getDepth(), getCycle());
        w.setStatus(getStatus());
        return w;
    }

    private void dissolve(Item i, NHBot dissolver, Container c, boolean all) {
        if(i instanceof Soluble) {
            ((Soluble)i).dissolve(dissolver, c, null, false, all);
        }
    }
}
