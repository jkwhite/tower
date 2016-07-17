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


public abstract class Gun extends Weapon1H implements Chargeable {
    private int _charges = Rand.om.nextInt(300)+200;
    private static final Stat[] STATS = new Stat[]{Stat.ag, Stat.ag, Stat.st};


    public void setCharges(int charges) {
        _charges = 100*charges;
    }

    public int getCharges() {
        return _charges;
    }

    public boolean discharge(NHBot b, Container c) {
        if(_charges>0) {
            --_charges;
            return true;
        }
        else {
            N.narrative().print(b, "Click! Nothing happens.");
            return false;
        }
    }

    public String toString() {
        String n = super.toString();
        if(isIdentified()) {
            n += " ("+getCharges()+" charge"+(getCharges()==1?"":"s")+")";
        }
        return n;
    }

    public final String getSkill() {
        return "guns";
    }

    public Type getType() {
        return Type.missile;
    }

    public Stat[] getStats() {
        return STATS;
    }

    abstract public int getDistance();

    abstract public String getRayAudio();

    public Armament toRay() {
        return new Armament() {
            public int getPower() { return Gun.this.getPower(); }
            public int getModifiedPower() {
                int p = getPower();
                switch(getStatus()) {
                    case blessed:
                        p += p/2;
                        break;
                    case cursed:
                        p /= 2;
                        break;
                }
                return p;
            }
            public int getRate() { return Gun.this.getRate(); }
            public Type getType() { return Type.missile; }
            public int getHp() { return 1; }
            public void setHp(int hp) {}
            public String getVerb() { return "zap"; }
            public Stat[] getStats() { return Gun.this.getStats(); }
            public String getSkill() { return Gun.this.getSkill(); }
            public String getColor() { return "translucent-orange"; }
            public String getAudio() { return getRayAudio(); }
            public String getModel() { return "-"; }
            public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return Gun.this.invoke(attacker, defender, a); }
            public void invoke(NHBot attacker, NHSpace s, Attack a) { Gun.this.invoke(attacker, s, a); }
            public Item toItem() { return null; }
        };
    }
}
