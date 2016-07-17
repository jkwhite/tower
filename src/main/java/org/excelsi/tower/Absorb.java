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


/**
 * God only knows what I'd be without you.
 */
public class Absorb extends Unarmed {
    private int _power;


    public Absorb() {
        this(4);
    }

    public Absorb(int power) {
        _power = power;
    }

    public int getRate() {
        return 99;
    }

    public int getPower() {
        return _power;
    }

    public void setPower(int power) {
        _power = power;
    }

    public float getWeight() {
        return 0;
    }

    public float getSize() {
        return 0;
    }

    public String getVerb() {
        return "hit";
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        super.invoke(attacker, defender, a);
        int dmg = Rand.om.nextInt(getPower());
        if(dmg>0) {
            N.narrative().print(defender, Grammar.start(defender, "feel")+" "+Grammar.possessive(defender)+" strength seep away.");
            defender.setHp(Math.max(0, defender.getHp()-dmg));
            if(defender.getHp()==0) {
                defender.die("Soul destroyed by "+Grammar.nonspecific(attacker));
            }
        }
        return null;
    }
}
