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


public class Scythe extends PoleArm implements Sharpenable {
    public Scythe() {
        initHp(500);
    }

    public float getSize() {
        return 6;
    }

    public String getAudio() {
        return "pickaxe";
    }

    public int getSlotCount() {
        return 2;
    }

    public float getWeight() {
        return 5;
    }

    public int getPower() {
        return 9;
    }

    public int getRate() {
        return 42;
    }

    public int getFindRate() {
        return 10;
    }

    public String getVerb() {
        return "hit";
    }

    public String getColor() {
        return "cyan";
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        if(getStatus()==Status.blessed) {
            if(Rand.d100(13)) {
                N.narrative().printf(attacker, "%V %n.", attacker, "reap", defender);
                defender.die("reaped by "+attacker);
            }
        }
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Type getType() {
        return Type.melee;
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
        Direction d = N.narrative().direct(b, "Which direction do you want to till?");
        till(b, d);
    }

    public void till(NHBot b, Direction d) {
        if(b.isAirborn()||b.isLevitating()) {
            N.narrative().print(b, Grammar.start(b)+" can't get any leverage.");
            return;
        }
        if(d==Direction.up || d==Direction.down) {
            N.narrative().print(b, "That is generally not recommended.");
            throw new ActionCancelledException();
        }
        else {
            NHSpace s = (NHSpace) b.getEnvironment().getMSpace().move(d, true);
            b.getEnvironment().face(d);
            if(s instanceof Soil || s instanceof Grass) {
                N.narrative().print(b, "That ground is already tilled.");
                throw new ActionCancelledException();
            }
            else if(s instanceof Ground || s.getClass()==Floor.class) {
                TillingAction da = new TillingAction(b, s, 2, this);
                N.narrative().print(b, Grammar.start(b, "start")+" tilling.");
                b.start(da);
            }
            else {
                N.narrative().print(b, "That is not tillable.");
                throw new ActionCancelledException();
            }
        }
    }
}
