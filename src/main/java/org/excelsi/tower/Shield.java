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


public class Shield extends Matter implements Affector {
    public static final String SKILL = "shields";
    private NHBot _bearer;


    public final void attach(NHBot b) {
        _bearer = b;
    }

    public final void remove(NHBot b) {
        _bearer = null;
    }

    public final SlotType getSlotType() {
        return SlotType.hand;
    }

    public final String getSkill() {
        return SKILL;
    }

    public int getCoverage() {
        return 40;
    }

    public int getRate() {
        return 100;
    }

    public final int getSlotModifier() {
        int m = getCoverage();
        switch(getStatus()) {
            case blessed:
                m += m/2;
                break;
            case cursed:
                m /= 2;
        }
        if(_bearer!=null) {
            m += m*(_bearer.getSkill(getSkill())-50f)/100f;
        }
        return m;
    }

    public float getShininess() {
        return 4f;
    }

    public float getSize() {
        return 4;
    }

    public int getPower() {
        return 0;
    }
}
