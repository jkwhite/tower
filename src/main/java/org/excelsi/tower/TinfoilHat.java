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


public class TinfoilHat extends ClothHelmet implements Createable, Deterrent, Interceptor {
    public Item[] getIngredients() {
        Item[] ing = new Item[]{new Tinfoil()};
        ing[0].setCount(8);
        return ing;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public Maneuver getDifficulty() {
        return Maneuver.routine;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public boolean intercepts(Attack a) {
        return a instanceof BoltAttack;
    }

    public Performable intercept(final NHBot attacker, final NHBot defender, Attack a) {
        /*
        N.narrative().print(defender, Grammar.first(Grammar.possessive(defender, this))+" reflects "+new Source("the bolt")+"!");
        final BoltAttack reflect = new BoltAttack(a.getWeapon(), defender, new Source(a.getSource().toString()));
        final Direction d = defender.getEnvironment().getMSpace().directionTo(attacker.getEnvironment().getMSpace());
        return new Performable() {
            public void perform(final Context c) {
                defender.getEnvironment().project(d, reflect);
            }
        };
        */
        return (c)-> {
            c.n().print(defender, Grammar.first(Grammar.possessive(defender, this))+" reflects "+new Source("the bolt")+"!");
            final BoltAttack reflect = new BoltAttack(a.getWeapon(), defender, new Source(a.getSource().toString()));
            final Direction d = defender.getEnvironment().getMSpace().directionTo(attacker.getEnvironment().getMSpace());
            defender.getEnvironment().project(d, reflect);
        };
    }

    public String getColor() {
        return "silver";
    }

    public float getSize() {
        return 1;
    }

    public float getWeight() {
        return 0.5f;
    }

    public int getRate() {
        return 60;
    }

    public int getPower() {
        return 5;
    }

    public int getFindRate() {
        return 3;
    }

    public Modifier getModifier() {
        Modifier m = new Modifier();
        m.setReasoning(-40);
        m.setIntuition(20);
        m.setPresence(20);
        m.setEmpathy(20);
        return m;
    }
}
