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


public class SpikedShield extends Shield {
    public int getPower() {
        return 30;
    }

    public int getRate() {
        return 100;
    }

    public float getWeight() {
        return 9;
    }

    public float getSize() {
        return 4;
    }

    public int getCoverage() {
        return 25;
    }

    public int getFindRate() {
        return 25;
    }

    public Attack invoke(NHBot attacker, final NHBot defender, Attack a) {
        super.invoke(attacker, defender, a);
        if(a.getType()==Attack.Type.melee&&a.getWeapon().toItem()!=null&&a.getWeapon().toItem().getSize()<4) {
            return new Attack() {
                public Source getSource() { return new Source(defender, SpikedShield.this); /*return Grammar.possessive(defender, SpikedShield.this);*/ }
                public NHBot getAttacker() { return defender; }
                public boolean isPhysical() { return true; }
                public boolean affectsAttacker() { return false; }
                public int getRadius() { return 1; }
                public Armament getWeapon() { return new Spike(); }
                public Type getType() { return Type.melee; }
            };
        }
        else {
            return null;
        }
    }

    private static final class Spike extends Weapon1HEdged {
        public String getSkill() {
            return Shield.SKILL;
        }

        public int getPower() {
            return 8;
        }

        public int getRate() {
            return 100;
        }

        public float getWeight() {
            return 0;
        }

        public float getSize() {
            return 1;
        }
    }
}
