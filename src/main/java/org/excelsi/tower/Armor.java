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
import java.util.List;


public abstract class Armor extends Inflicter implements Armament, Reinforcable, Combustible, Wearable {
    public static final String CLOTH = "cloth";
    public static final String LIGHT_LEATHER = "light leather";
    public static final String HEAVY_LEATHER = "heavy leather";
    public static final String CHAIN = "chain";
    public static final String PLATE = "plate";

    //private boolean _reinforced;


    public Armor(Infliction... inflictions) {
        super(inflictions);
        initHp(100);
    }

    public int score() {
        return getPower();
    }

    public void reinforce(ReinforcingMaterial m) {
        if(isReinforced()) {
            throw new IllegalStateException(this+" is already reinforced");
        }
        addFragment(new Reinforced(m.getReinforcingStrength()));
    }

    /*
    public void setReinforced(boolean reinforced) {
        if(reinforced!=_reinforced) {
            if(reinforced) {
                addFragment(new Potion.Reinforced());
            }
            else {
                removeFragment(Potion.Reinforced.NAME);
            }
            _reinforced = reinforced;
        }
    }
    */

    public boolean isReinforced() {
        return hasFragment(Reinforced.class);
    }

    public boolean isCombustible() {
        return true;
    }

    public String getCombustionPhrase() {
        return "melts";
    }

    public int getCombustionTemperature() {
        return isReinforced()?1200:900;
    }

    public void combust(Container c) {
        c.consume(this);
    }

    public String getColor() {
        return "cyan";
    }

    public final String getModel() {
        return "[";
    }

    public final String getCategory() {
        return "armor";
    }

    public Type getType() {
        return Type.melee;
    }

    public void invoke(NHBot b) {
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        inflict(attacker);
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public String getVerb() {
        return "block";
    }

    public Stat[] getStats() {
        return null;
    }

    public int getFindRate() {
        return 60;
    }

    public int getModifiedPower() {
        int pow = getPower();
        for(Fragment f:getFragments()) {
            pow += f.getPowerModifier();
        }
        switch(getStatus()) {
            case blessed:
                pow += pow/2;
                break;
            case cursed:
                pow /= 2;
                break;
        }
        pow = (int) (pow*((float)getHp())/getMaxHp());
        return pow;
    }

    public final Item toItem() {
        return this;
    }
}
