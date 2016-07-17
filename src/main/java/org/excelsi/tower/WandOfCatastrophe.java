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


public class WandOfCatastrophe extends Wand implements Createable {
    public WandOfCatastrophe() {
        setCharges(1);
    }

    public Item[] getIngredients() {
        Potion p = new Potion(new Fire());
        p.addFragment(new Lightning());
        Item[] ing = new Item[]{new SteelTube(), new Battery(), new Nozzle(), p, new Wire(), new RollOfTape()};
        return ing;
    }

    public boolean accept(NHSpace s) { return true; }

    public String getCreationSkill() { return "gadgetry"; }

    public Maneuver getDifficulty() { return Maneuver.hard; }

    public void invoke(final NHBot b) {
        if(discharge(b)) {
            Direction chosen = getDirection();
            if(chosen==null) {
                chosen = N.narrative().direct(b, "Which direction?");
            }
            b.getEnvironment().face(chosen);
            if(getStatus()==Status.cursed) {
                N.narrative().print(b, "The wand backfires!");
                b.getEnvironment().project(Direction.north, new Attack() {
                    public Type getType() {
                        return Type.ball;
                    }

                    public Source getSource() {
                        return new Source(WandOfCatastrophe.this);
                    }

                    public NHBot getAttacker() { return b; }

                    public int getRadius() {
                        return 3;
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
                            public int getPower() { return 33; }
                            public int getModifiedPower() { return 33; }
                            public int getRate() { return 100; }
                            public int getHp() { return 1; }
                            public void setHp(int hp) { }
                            public String getVerb() { return "blast"; }
                            public String getAudio() { return "blast"; }
                            public Stat[] getStats() { return null; }
                            public String getSkill() { return "thrown"; }
                            public String getColor() { return "orange"; }
                            public String getModel() { return "+"; }
                            public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return null; }
                            public void invoke(NHBot attacker, NHSpace s, Attack a) { }

                            public Item toItem() { return null; }
                        };
                    }
                });
            }
            else {
                int t = 3000;
                if(getStatus()==Status.blessed) {
                    t *= 2;
                }
                final int temp = t;
                b.getEnvironment().project(chosen, new Attack() {
                        public Source getSource() {
                            return new Source(WandOfCatastrophe.this);
                        }

                        public NHBot getAttacker() { return b; }

                        public Type getType() {
                            return Type.bolt;
                        }

                        public boolean isPhysical() {
                            return false;
                        }

                        public Armament getWeapon() {
                            return new Fire(temp);
                        }

                        public int getRadius() {
                            return 2;
                        }

                        public boolean affectsAttacker() {
                            return false;
                        }
                });
                if(Rand.d100(66)) {
                    setStatus(Status.cursed);
                }
            }
        }
    }

    public boolean isDirectable() {
        return true;
    }

    public int getFindRate() {
        return 0;
    }
}
