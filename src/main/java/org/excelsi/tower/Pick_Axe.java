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


public class Pick_Axe extends Tool implements Armament {
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.st, Stat.ag};


    public Pick_Axe() {
        initHp(50);
    }

    public float getSize() {
        return 2;
    }

    public String getAudio() {
        return "pickaxe";
    }

    public SlotType getSlotType() {
        return SlotType.hand;
    }

    public int getSlotCount() {
        return 2;
    }

    public float getWeight() {
        return 5;
    }

    public int getPower() {
        return 10;
    }

    public int getRate() {
        return 30;
    }

    public String getVerb() {
        return "hit";
    }

    public String getColor() {
        return "cyan";
    }

    public Stat[] getStats() {
        return STATS;
    }

    public int getModifiedPower() {
        return getPower();
    }

    public int getDiggingRate() {
        return 9;
    }

    public String getSkill() {
        return Weapon.TWO_HANDED_EDGED;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Type getType() {
        return Type.melee;
    }

    public Item toItem() {
        return this;
    }

    public void use(NHBot b) {
        if(b.getWielded()!=this) {
            try {
                b.setWielded(this);
            }
            catch(EquipFailedException e) {
                N.narrative().print(b, e.getMessage());
                throw new ActionCancelledException();
            }
            N.narrative().print(b, Grammar.start(b, "wield")+" "+Grammar.nonspecific(this)+".");
        }
        Direction d = N.narrative().direct(b, "Which direction do you want to dig?");
        dig(b, d);
    }

    public void dig(NHBot b, Direction d) {
        if(b.isAirborn()||b.isLevitating()) {
            N.narrative().print(b, Grammar.start(b)+" can't get any leverage.");
            return;
        }
        if(d==Direction.up) {
            N.narrative().print(b, "That is generally not recommended.");
            throw new ActionCancelledException();
        }
        else if(d==Direction.down) {
            DiggingAction da = new DiggingAction(b, d, getDiggingRate(), this);
            N.narrative().print(b, Grammar.start(b, "start")+" digging.");
            b.start(da);
        }
        else {
            NHSpace s = (NHSpace) b.getEnvironment().getMSpace().move(d, true);
            b.getEnvironment().face(d);
            if(s.isTransparent()||s.isDestroyable()) {
                for(Item i:s.getItem()) {
                    if(i instanceof Rock) {
                        int stones = Rand.om.nextInt(3*i.getCount());
                        SmallStone ss = new SmallStone();
                        ss.setCount(stones);
                        s.add(ss);
                        s.remove(i);
                        N.narrative().print(b, Grammar.start(b, "break")+" up some rocks.");
                        return;
                    }
                }
                for(Parasite p:s.getParasites()) {
                    p.attacked(this);
                }
                if(s.isDestroyable()) {
                    s.destroy();
                }
                if(b.getEnvironment().getMSpace().getDepth()>s.getDepth()) {
                    DiggingAction da = new DiggingAction(b, s, getDiggingRate(), this);
                    //N.narrative().print(b, Grammar.start(b, "start")+" digging.");
                    b.start(da);
                }
            }
            else {
                DiggingAction da = new DiggingAction(b, s, getDiggingRate(), this);
                //N.narrative().print(b, Grammar.start(b, "start")+" digging.");
                b.start(da);
            }
        }
    }
}
