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
package org.excelsi.aether;


import org.excelsi.matrix.Direction;
import java.util.Arrays;


public class TestMechanics extends junit.framework.TestCase {
    public void testBallAttack() {
        QuantumMechanics q = new QuantumMechanics();
        NHEnvironment.setMechanics(q);
        final BasicBot b1 = new BBot();
        b1.setStrength(100);
        b1.setAgility(100);
        b1.setHp(5);
        b1.setMaxHp(5);
        final BasicBot b2 = new BBot();
        b2.setHp(5);
        b2.setMaxHp(5);
        BasicBot b3 = new BBot();
        b3.setHp(5);
        b3.setMaxHp(5);

        final Level lev = new Level(20, 20);
        EverythingAdapter ea = new EverythingAdapter();
        lev.getEventSource().addContainerListener(ea);
        lev.getEventSource().addContainerListener(new ContainerAdapter());
        lev.getEventSource().addNHEnvironmentListener(ea);
        lev.getEventSource().addNHSpaceListener(ea);
        lev.addRoom(new Level.Room(1, 1, 10, 10, 10, 10), true);
        lev.getSpace(2, 2).setOccupant(b1);
        lev.getSpace(4, 4).setOccupant(b2);
        lev.getSpace(6, 6).setOccupant(b3);

        q.addMechanicsListener(new MechanicsListener() {
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                assertEquals("wrong attack type", Attack.Type.ball, attack.getType());
                assertEquals("wrong attacker", b1, attacker);
                assertNull("wrong defender", defender);
                NHSpace l = null;
                for(NHSpace s:path) {
                    assertTrue("too far away: "+s, lev.getSpace(2,2).distance(s)<=5);
                    if(l!=null) {
                        assertTrue("wrong order: "+l+", "+s, lev.getSpace(2,2).distance(s)>=lev.getSpace(2,2).distance(l));
                    }
                    l = s;
                }
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                assertEquals("wrong attack type", Attack.Type.ball, attack.getType());
                assertEquals("wrong attacker", b1, attacker);
                assertEquals("wrong defender", b2, defender);
            }
        });

        Outcome[] outs = b1.getEnvironment().project(Direction.north, new Attack() {
            public Source getSource() { return new Source("crm 114"); }
            public Type getType() {
                return Type.ball;
            }

            public int getRadius() {
                return 5; // gets b2 but not b3
            }

            public boolean isPhysical() {
                return true;
            }

            public Armament getWeapon() {
                return b1.getForm().getNaturalWeapon();
            }

            public boolean affectsAttacker() {
                return false;
            }

            public NHBot getAttacker() { return b1; }
        });
        assertEquals("setsuko slid down onto the shelter floor.", 1, outs.length);
        Outcome o = outs[0];
        assertEquals("she would never move again.", b2, o.getDefender());
    }

    private static class BBot extends BasicBot {
        public BBot() {
            setForm(new Kong());
        }
    }

    private static class Kong extends Form {
        public Kong() {
            super(new ABomb(), null, new Slot(SlotType.torso, "body", 100));
        }
    }

    private static class ABomb extends Item implements Armament {
        public Type getType() { return Type.melee; }
        public int getPower() { return 100; }
        public int getModifiedPower() { return 100; }
        public int getRate() { return 100; }
        public int getHp() { return 1; }
        public void setHp(int hp) { }
        public String getVerb() { return "nuke"; }
        public String getStats() { return null; }
        public String getSkill() { return "horseback riding"; }
        public String getColor() { return null; }
        public String getModel() { return "yee-haw"; }
        public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return null; }
        public void invoke(NHBot attacker, NHSpace defender, Attack a) {}
        public void invoke(NHBot attacker) { }
        public Item toItem() { return this; }
        public float getWeight() { return 0; }
        public float getSize() { return 0; }
        public SlotType getSlotType() { return SlotType.useless; }
        public String getCategory() { return "wmd"; }
    }
}
