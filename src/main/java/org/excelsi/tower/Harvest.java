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


/**
 * Your time is near.
 */
public class Harvest extends PoleArm implements Sharpenable {
    private int _power = 6;


    public Harvest() {
        initHp(5000);
    }

    public String getName() {
        return isClassIdentified()?"Harvest":"stained scythe";
    }

    public boolean isUnique() {
        return true;
    }

    public float getSize() {
        return 9;
    }

    public String getAudio() {
        return "pickaxe";
    }

    public int getSlotCount() {
        return 2;
    }

    public float getWeight() {
        return 7;
    }

    public int getPower() {
        return _power;
    }

    public int getRate() {
        return 62;
    }

    public int getFindRate() {
        return 0;
    }

    public String getVerb() {
        return "hit";
    }

    public String getColor() {
        return "red";
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        if(attacker.isPlayer()&&Rand.d100(13)) {
            if(isClassIdentified()) {
                N.narrative().printf(attacker, "Harvest reaps %n.", defender);
            }
            //else {
                //N.narrative().printf(attacker, "The stained scythe reaps %n.", defender);
            //}
            defender.die("Reaped by "+attacker);
            _power++;
        }
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Type getType() {
        return Type.melee;
    }
}
