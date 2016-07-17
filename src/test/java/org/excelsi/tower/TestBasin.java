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
import java.util.Arrays;
import java.util.ArrayList;
import org.excelsi.matrix.Direction;


public class TestBasin extends junit.framework.TestCase {
    /**
     * Loads basis data via static registrator method on each transformative.
     */
    protected void setUp() {
        Class[] poke = new Class[]{Strength.class, Agility.class, Weakness.class, Concussion.class,
            Frenzy.class, Rewiring.class, Cyanide.class, Poison.class, Healing.class,
            Confusion.class, Polymorph.class, Levitation.class, Tunneling.class, Speed.class,
            Sloth.class, WaterInfliction.class, Fire.class, Lightning.class, Cold.class};
        for(Class c:poke) {
            try {
                c.newInstance();
            }
            catch(Throwable t) {
            }
        }
    }

    public void testLook() {
        Patsy p = new Patsy();
        Basin b = new Basin(false);
        b.look(p);
        b.add(new Potion(new Poison()), p);
        b.look(p);
        b.add(new MortarAndPestle());
        b.look(p);
        b.add(new Potion(new Poison()), p);
        b.look(p);
    }

    public void testInventoryCompact() {
        Patsy p = new Patsy();
        Potion em = new Potion();
        p.getInventory().add(em);
        p.getInventory().add(new Potion(new WaterInfliction()));
        assertEquals("base condition wrong", 2, p.getInventory().size());
        Basin b = new Basin(true);
        assertFalse("basin should not be empty", b.isEmpty());
        Dip d = new Dip(em, b);
        d.setBot(p);
        d.perform();
        assertFalse("didn't fill", em.isEmpty());
        assertEquals("didn't compact: "+p.getInventory(), 1, p.getInventory().size());
    }

    public void testIdentity() {
        new Poison().setClassIdentified(true);
        Potion before = new Potion(new Poison());
        assertEquals("a potion of poison", before.toString());
        Basin b = new Basin(false);
        NHBot adder = new Patsy();
        b.add(before, adder);

        Potion after = new Potion();
        b.immerse(adder, after);

        assertEquals("an empty potion", before.toString());
        assertEquals("a potion of poison", after.toString());
        assertTrue("basin not empty", b.isEmpty());
    }

    /**
     * Test that mixing potions produces the correct result.
     */
    public void testMix() {
        Basis.print();
        mixTest(new Strength(), new Weakness(), new Weakness());
        mixTest(new Weakness(), new Strength(), new Weakness());
        mixTest(new Strength(), new Rewiring(), new Rewiring());
        mixTest(new Weakness(), new Agility(), new Levitation());
        mixTest(new Agility(), new Levitation(), new Weakness());
        mixTest(new Weakness(), new Polymorph(), new Rewiring());
        mixTest(new Weakness(), new Polymorph(), new Rewiring());
        mixTest(new Polymorph(), new Polymorph(), new Polymorph());
    }

    public void testEmpty() {
        Potion potion = new Potion();
        NHBot adder = new Patsy();
        Basin b = new Basin(false);
        b.immerse(adder, potion);
        assertTrue("potion not empty", potion.isEmpty());
    }

    public void testFlamingSword() throws EquipFailedException {
        Rand.load();
        new Fire().setClassIdentified(true);
        Potion p1 = new Potion(new Healing());
        Potion p2 = new Potion(new Confusion());
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.setStrength(10000);
        p.setAgility(10000);
        Basin b = new Basin(false);
        b.add(p1, p);
        b.add(p2, p);
        assertFalse("chemical marriage", b.isEmpty());
        Broadsword s = new Broadsword();
        b.immerse(p, s);
        int fs = s.getFragments().size();
        assertEquals("slogans that used to be scrawled on the wall: "+fs, 1, fs);
        Fragment f = s.getFragments().get(0);
        assertTrue("are written in the heart: "+f, f.getClass()==Fire.class);
        assertEquals("frontier... what frontier?", "a broadsword of fire", s.toString());

        QuantumMechanics q = new QuantumMechanics();
        NHEnvironment.setMechanics(q);
        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(1, 1, 10, 10, 10, 10), true);
        p.setWielded(s);
        lev.getSpace(2,2).setOccupant(p);
        final BasicBot uhoh = new BasicBot();
        uhoh.setForm(new Humanoid());
        uhoh.setMaxHp(Integer.MAX_VALUE);
        uhoh.setHp(Integer.MAX_VALUE);
        uhoh.getInventory().add(new ScrollOfVacuumMetastabilityDisaster());
        lev.getSpace(3,2).setOccupant(uhoh);
        p.getEnvironment().face(Direction.east);
        final ArrayList events = new ArrayList();
        q.addMechanicsListener(new MechanicsListener() {
            public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                events.add("son i am able she said though you scare me watch said i beloved");
                assertTrue("wrong weapon: "+attack.getWeapon(), attack.getWeapon() instanceof Broadsword);
            }

            public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                events.add("beloved i said watch me scare you though said she able am i son");
                // scroll should be ash
                assertEquals("scroll didn't burn: "+uhoh.getInventory(), 0, uhoh.getInventory().size());
            }
        });
        p.getEnvironment().forward();
        assertEquals("did not get events", 2, events.size());
    }

    /**
     * Test that mixing potions with opposing basises produces the correct result.
     */
    public void testExplode() {
        Transformative[] t1 = new Transformative[]{new Strength()};
        Transformative[] t2 = new Transformative[]{new Concussion()};
        assertEquals("test setup wrong", t1.length, t2.length);
        for(int i=0;i<t1.length;i++) {
            Potion p1 = new Potion(t1[i]);
            Potion p2 = new Potion(t2[i]);
            final Patsy doomed = new Patsy();
            doomed.setForm(new Humanoid());
            doomed.setStrength(1000);
            doomed.setAgility(1000);
            Level lev = new Level(20, 20);
            lev.addRoom(new Level.Room(1, 1, 10, 10, 10, 10), true);
            Basin b = new Basin(false);
            lev.getSpace(2, 2).replace(b);

            QuantumMechanics q = new QuantumMechanics();
            NHEnvironment.setMechanics(q);
            final ArrayList events = new ArrayList();
            q.addMechanicsListener(new MechanicsListener() {
                public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                    events.add("if my parents are crying");
                    assertEquals("then i'll dig a tunnel", null, defender);
                    assertEquals("from my window to yours", attack.getType(), Attack.Type.ball);
                }

                public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                    if(defender!=null) {
                        events.add("purify the colors, purify my mind");
                        assertEquals("and spread the ashes of the colors", doomed, defender);
                        assertEquals("over this heart of mine", attack.getType(), Attack.Type.ball);
                    }
                }
            });

            b.setOccupant(doomed);
            b.add(p1, doomed);
            int oldMaxHp = doomed.getMaxHp();
            b.add(p2, doomed);
            assertTrue("didn't explode", b.isEmpty());
            assertEquals("did not get mech events: "+events, 2, events.size());
            assertTrue("didn't get status effect", doomed.getMaxHp()>oldMaxHp);
        }
    }

    /**
     * Test that mixing potions with opposing basises inflicts damage.
     */
    public void testExplodeKill() {
        Transformative[] t1 = new Transformative[]{new Strength()};
        Transformative[] t2 = new Transformative[]{new Concussion()};
        assertEquals("test setup wrong", t1.length, t2.length);
        for(int i=0;i<t1.length;i++) {
            Potion p1 = new Potion(t1[i]);
            Potion p2 = new Potion(t2[i]);
            final Patsy doomed = new Patsy();
            doomed.setForm(new Humanoid());
            doomed.setStrength(1000);
            doomed.setAgility(1000);
            Level lev = new Level(20, 20);
            lev.addRoom(new Level.Room(1, 1, 10, 10, 10, 10), true);
            Basin b = new Basin(false);
            lev.getSpace(2, 2).replace(b);

            QuantumMechanics q = new QuantumMechanics();
            NHEnvironment.setMechanics(q);
            final ArrayList events = new ArrayList();
            q.addMechanicsListener(new MechanicsListener() {
                public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
                    events.add("if my parents are crying");
                    assertEquals("then i'll dig a tunnel", null, defender);
                    assertEquals("from my window to yours", attack.getType(), Attack.Type.ball);
                }

                public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
                    if(defender!=null) {
                        events.add("purify the colors, purify my mind");
                        assertEquals("and spread the ashes of the colors", doomed, defender);
                        assertEquals("over this heart of mine", attack.getType(), Attack.Type.ball);
                    }
                }
            });

            b.setOccupant(doomed);
            b.add(p1, doomed);
            int oldMaxHp = doomed.getMaxHp();
            b.add(p2, doomed);
            assertTrue("didn't explode", b.isEmpty());
            assertEquals("did not get mech events: "+events, 2, events.size());
            assertTrue("didn't get status effect", doomed.getMaxHp()>oldMaxHp);
        }
    }

    private void mixTest(Transformative t1, Transformative t2, Transformative... result) {
        t1.setClassIdentified(true);
        t2.setClassIdentified(true);
        for(Transformative t:result) {
            t.setClassIdentified(true);
        }
        Potion p1 = new Potion(t1);
        Potion p2 = new Potion(t2);

        Patsy p = new Patsy();
        Basin b = new Basin(false);
        b.add(p1, p);
        b.add(p2, p);
        Potion p3 = new Potion();
        b.immerse(p, p3);
        Potion res = new Potion();
        for(Transformative t:result) {
            res.addFragment(t);
        }
        assertEquals("she knows which way the wind blows: "+t1+", "+t2+" => "+res, res.toString(), p3.toString());

        Potion p4 = new Potion();
        b.immerse(p, p4);
        assertEquals("but she doesn't know what for: "+t1+", "+t2+" => "+res, res.toString(), p4.toString());

        assertTrue("if tigers live in jungles, what's moving round outside", b.isEmpty());
    }
}
