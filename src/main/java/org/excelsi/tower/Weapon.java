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
import java.util.ArrayList;
import java.util.Arrays;


public abstract class Weapon extends Inflicter implements Armament {
    public static final String ONE_HANDED_EDGED = "one-handed edged";
    public static final String TWO_HANDED_EDGED = "two-handed edged";
    public static final String ONE_HANDED_CRUSHING = "one-handed crushing";
    public static final String TWO_HANDED_CRUSHING = "two-handed crushing";
    public static final String BOWS = "bows";
    public static final String UNARMED = "unarmed";
    public static final String WHIPS = "whips";
    public static final String POLEARMS = "pole-arms";
    public static final String THROWN = "thrown";
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.st, Stat.ag};

    public Weapon(Infliction... inflictions) {
        super(inflictions);
        initHp(100);
    }

    public int score() {
        return getPower();
    }

    public boolean invokesIncidentally() {
        return true;
    }

    public String getColor() {
        return "cyan";
    }

    public final String getModel() {
        return "(";
    }

    public final String getCategory() {
        return "weapon";
    }

    public String getAudio() {
        if(getSize()==0) {
            return "hit_tiny";
        }
        else if(getSize()<1.5f) {
            return "hit_small";
        }
        else if(getSize()<4f) {
            return "hit_medium";
        }
        else {
            return "hit_large";
        }
    }

    public final SlotType getSlotType() {
        return SlotType.hand;
    }

    public Stat[] getStats() {
        return STATS;
    }

    public void invoke(NHBot b) {
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        inflict(defender);
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public String getVerb() {
        return "hit";
    }

    public int getFindRate() {
        return 60;
    }

    public int getModifiedPower() {
        return staticGetPower(getPower(), getFragments(), getStatus(), getHp(), getMaxHp());
    }

    public static int staticGetPower(int p, List<Fragment> fs, Status s, int hp, int maxHp) {
        int pow = p;
        for(Fragment f:fs) {
            pow += f.getPowerModifier();
        }
        switch(s) {
            case blessed:
                pow += pow/2;
                break;
            case cursed:
                pow /= 2;
                break;
        }
        pow = (int) (pow*((float)hp/maxHp));
        return pow;
    }

    public final Item toItem() {
        return this;
    }
}
