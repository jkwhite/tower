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


public class MirrorShield extends Shield implements Interceptor, Createable {
    public MirrorShield() {
        //addFragment(new Mirror());
    }

    public Item[] getIngredients() {
        Item[] ing = new Item[]{new IronShield(), new CanOfShoePolish()};
        ing[1].setCount(3);
        return ing;
    }

    public float getLevelWeight() { return 0.75f; }

    public boolean accept(NHSpace s) { return true; }

    public String getCreationSkill() { return "gadgetry"; }

    public Maneuver getDifficulty() { return Maneuver.routine; }

    public int getPower() {
        return 40;
    }

    public int getRate() {
        return 100;
    }

    public float getWeight() {
        return 6;
    }

    public float getSize() {
        return 4;
    }

    public int getCoverage() {
        return 40;
    }

    public int getFindRate() {
        return 10;
    }

    public boolean intercepts(Attack a) {
        return a instanceof BoltAttack;
    }

    @Override public Performable intercept(final NHBot attacker, final NHBot defender, Attack a) {
        /*
        N.narrative().print(defender, Grammar.first(Grammar.possessive(defender, this))+" reflects "+new Source("the bolt")+"!");
        final BoltAttack reflect = new BoltAttack(a.getWeapon(), defender, a.getSource());
        final Direction d = defender.getEnvironment().getMSpace().directionTo(attacker.getEnvironment().getMSpace());
        return new Runnable() {
            public void run() {
                defender.getEnvironment().project(d, reflect);
            }
        };
        */
        return (c)->{
            c.n().print(defender, Grammar.first(Grammar.possessive(defender, this))+" reflects "+new Source("the bolt")+"!");
            final BoltAttack reflect = new BoltAttack(a.getWeapon(), defender, a.getSource());
            final Direction d = defender.getEnvironment().getMSpace().directionTo(attacker.getEnvironment().getMSpace());
            defender.getEnvironment().project(d, reflect);
        };
    }
}
