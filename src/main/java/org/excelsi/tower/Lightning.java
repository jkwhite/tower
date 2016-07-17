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
import org.excelsi.matrix.Filter;
import org.excelsi.matrix.MSpace;


public class Lightning extends Fire {
    private static final Modifier MOD = new Modifier();
    static {
        Basis.claim(new Basis(Basis.Type.swords, 6));
        MOD.setCandela(3);
        MOD.setCandelaColor(new float[]{0.6f, 0.4f, 1f, 1f});
    }

    public Lightning() {
        this(900);
    }

    public Lightning(int temp) {
        super(temp);
    }

    public int getPower() {
        return 80;
    }

    public int getModifiedPower() {
        return getPower();
    }

    public Modifier getModifier() {
        return MOD;
    }

    public int getRate() {
        return 95;
    }

    public Stat[] getStats() {
        return null;
    }

    public String getVerb() {
        return "shock";
    }

    public String getAudio() {
        return "lightning";
    }

    protected String getSourceName() {
        return "lightning";
    }

    public boolean inflict(NHBot defender, Source s) {
        super.inflict(defender, s);
        if(Rand.d100(101-defender.getModifiedConstitution())) {
            N.narrative().print(defender, "The "+getSourceName()+" bolt stuns "+Grammar.noun(defender)+".");
            defender.start(new Stun(defender, Rand.om.nextInt(5)+2));
        }
        if(defender.getEnvironment()!=null&&defender.getEnvironment().getMSpace() instanceof Water) {
            defender.getEnvironment().project(null, new Attack() {
                public Source getSource() {
                    return new Source("the "+getSourceName());
                }

                public NHBot getAttacker() { return null; }

                public Type getType() {
                    return Type.ball;
                }

                public int getRadius() {
                    return 10;
                }

                public boolean isPhysical() {
                    return true;
                }

                public boolean affectsAttacker() {
                    return true;
                }

                public Armament getWeapon() {
                    return new Armament() {
                        public Type getType() { return Type.melee; }
                        public int getPower() { return 40; }
                        public int getModifiedPower() { return 40; }
                        public int getRate() { return 100; }
                        public int getHp() { return 1; }
                        public void setHp(int hp) { }
                        public String getVerb() { return Lightning.this.getVerb(); }
                        public Stat[] getStats() { return null; }
                        public String getSkill() { return null; }
                        public String getColor() { return Lightning.this.getColor(); }
                        public String getModel() { return "+"; }
                        public String getAudio() { return "lightning"; }
                        public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return null; }
                        public void invoke(NHBot attacker, NHSpace defender, Attack a) { }
                        public Item toItem() { return null; }
                    };
                }
            },
            new Filter() {
                public boolean accept(MSpace m) {
                    return m instanceof Water;
                }
            });
        }
        return true;
    }

    public String getColor() {
        return "light-blue";
    }
}
