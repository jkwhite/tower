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


public abstract class Wand extends VariegatedItem implements WritingUtensil, Chargeable {
    private static final Stat[] STATS = new Stat[]{Stat.ag, Stat.ag, Stat.em};
    private int _charges;
    private Direction _dir;


    static {
        if(Universe.getUniverse()!=null&&Universe.getUniverse().getPublicColormap()!=null) {
            variegate("wand", Universe.getUniverse().getPublicColormap().keySet().toArray(new String[0]));
        }
        else {
            variegate("wand", new String[]{"yellow", "magenta", "cyan", "black"});
        }
    }

    public Wand() {
        _charges = Rand.om.nextInt(3)+3;
    }

    public float getLevelWeight() { return 0.7f; }

    public int score() {
        return 10;
    }

    public String getName() {
        if(isIdentified()||isClassIdentified()) {
            return super.getName();
        }
        else {
            return getColor()+" "+getCategory();
        }
    }

    public String getObscuredName() {
        return getCategory();
    }

    public final String getColor() {
        return getVariation();
    }

    public final String getModel() {
        return "/";
    }

    public final SlotType getSlotType() {
        return SlotType.none;
    }

    public Stat[] getStats() {
        return STATS;
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 0.5f;
    }

    public int getFindRate() {
        return 13;
    }

    public final String getCategory() {
        return "wand";
    }

    public void setCharges(int charges) {
        _charges = charges;
    }

    public int getCharges() {
        return _charges;
    }

    public void setDirection(Direction d) {
        _dir = d;
    }

    public Direction getDirection() {
        return _dir;
    }

    public boolean isDirectable() {
        return false;
    }

    public void setBionic(boolean bionic) {
        if(bionic!=isBionic()) {
            if(bionic) {
                addFragment(new Bionic());
            }
            else {
                removeFragment("bionic");
            }
        }
    }

    public boolean isBionic() {
        return hasFragment(Bionic.class);
    }

    public int getWritingAbility() {
        return 100;
    }

    public boolean discharge(NHBot b, Container c) {
        if(isBionic()) {
            dischargeBionic(b);
            return true;
        }
        if(_charges>0) {
            _charges--;
            return true;
        }
        else {
            N.narrative().print(b, "Nothing happens.");
            return false;
        }
    }

    protected final boolean discharge(NHBot b) {
        if(isBionic()) {
            dischargeBionic(b);
            return true;
        }
        if(_charges>0) {
            setCharges(_charges-1);
            if(b.isPlayer()) {
                if(!isClassIdentified()) {
                    setClassIdentified(true);
                    N.narrative().print(b, "This is "+this+".");
                }
            }
            return true;
        }
        else {
            N.narrative().print(b, "Nothing happens.");
            return false;
        }
    }

    public String toString() {
        String n = super.toString();
        if(isIdentified()) {
            if(isBionic()) {
                //n += " (bionic)";
            }
            else {
                n += " ("+getCharges()+" charge"+(getCharges()==1?"":"s")+")";
            }
        }
        return n;
    }

    private void dischargeBionic(NHBot b) {
        b.setHp(Math.max(0, b.getHp()-10));
        if(b.getHp()==0) {
            b.die("Killed by overcasting "+Grammar.nonspecific(this));
        }
    }

    public static class Bionic implements Fragment {
        private Item _owner;

        public GrammarType getPartOfSpeech() { return GrammarType.adjective; }
        public String getText() { return "bionic"; }
        public String getName() { return getText(); }
        public void setIdentified(boolean ident) { }
        public boolean isIdentified() { return true; }
        public void setClassIdentified(boolean b) {}
        public boolean isClassIdentified() { return true; }
        public int getOccurrence() { return 0; }
        public Modifier getModifier() { return new Modifier(); }
        public int getPowerModifier() { return 0; }
        public void setOwner(Item i) { _owner = i; }
        public Item getOwner() { return _owner; }
        public void apply(Fragment f) {}
        public boolean intercepts(Attack a) {
            return false;
        }

        public Runnable intercept(NHBot attacker, NHBot defender, Attack a) {
            return null;
        }

    }
}
