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
import java.util.ArrayList;


public class NuclearBomb extends Tool {
    public void use(final NHBot b) {
        b.getInventory().consume(this);
        b.getEnvironment().project(Direction.north, new Attack() {
            public Type getType() {
                return Type.ball;
            }

            public NHBot getAttacker() {
                return b;
            }

            public Source getSource() {
                return new Source(NuclearBomb.this);
            }

            public int getRadius() {
                return Integer.MAX_VALUE;
            }

            public boolean isPhysical() {
                return true;
            }

            public boolean affectsAttacker() {
                return false;
            }

            public Armament getWeapon() {
                return new Armament() {
                    public Type getType() { return Type.melee; }
                    public int getPower() { return 594; }
                    public int getModifiedPower() { return 594; }
                    public int getRate() { return 1000; }
                    public int getHp() { return 1; }
                    public void setHp(int hp) { }
                    public String getVerb() { return "blast"; }
                    public Stat[] getStats() { return null; }
                    public String getSkill() { return "thrown"; }
                    public String getColor() { return "orange"; }
                    public String getAudio() { return "hit_crushing"; }
                    public String getModel() { return "+"; }
                    public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return null; }
                    public void invoke(NHBot attacker, NHSpace s, Attack a) { }
                    public Item toItem() { return NuclearBomb.this; }
                };
            }
        });
    }

    public float getWeight() {
        return 0f;
    }

    public float getSize() {
        return 0f;
    }

    public int getFindRate() {
        return 0;
    }

    public String getColor() {
        return "silver";
    }
}
