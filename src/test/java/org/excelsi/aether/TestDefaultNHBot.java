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


public class TestDefaultNHBot extends junit.framework.TestCase {
    public void testAttributes() {
        DefaultNHBot b = new TBot();
        b.setStat(Stat.st, 100);
        assertEquals("no stat", 100, b.getStat(Stat.st));
        b.setName("echoes");
        assertEquals("wrong name", "echoes", b.getName());
        b = (DefaultNHBot) b.copy(b);
        b = (DefaultNHBot) b.clone();
        assertEquals("wrong name", "echoes", b.getName());
        assertEquals("no stat", 100, b.getStat(Stat.st));
        b.setModel("m");
        b.setModel("m");
        b.setModel(null);
        b.setModel(null);
        b.setModel("m");
        assertEquals("wrong model", "m", b.getModel());
        assertFalse("starts alive", b.isDead());
        b.setForm(new Handed());
        assertEquals("wrong init score", 0, b.score());
        b.setPack(new Item[]{new Weapon(), new Weapon(), new Armor()});
        assertEquals("wrong pack: "+b.getPack(), 3, b.getPack().length);
        assertFalse("equip", b.isEquipped(b.getPack()[0]));
    }

    public void testModifier() {
        DefaultNHBot b = new TBot();
        Modifier m = new Modifier(20);
        b.addModifier(m);
        assertEquals("didn't get str bonus", 20, b.getModifiedStrength());
        m.setPresence(20);
        assertEquals("didn't get pr bonus", 20, b.getModifiedPresence());
        m.add(new Modifier(0,10));
        assertEquals("didn't get qu bonus", 10, b.getModifiedQuickness());
        assertEquals("wrong mod list", 1, b.getModifiers().size());
        b.removeModifier(m);
        assertEquals("didn't remove", 0, b.getModifiers().size());
        try {
            b.removeModifier(m);
            fail("didn't catch re-removed");
        }
        catch(IllegalArgumentException good) {
        }
    }

    public void testDequipEquipped() throws EquipFailedException {
        DefaultNHBot b = new TBot();
        b.setForm(new Handed());
        Weapon w = new Weapon();
        b.setWielded(w);
        assertEquals("wrong equipped weapon", w, b.getWielded());
        try {
            b.setWielded(w);
            fail("did not get equip exception");
        }
        catch(EquipFailedException good) {
        }
        b.setWielded(null);
        assertNull("still have something equipped", b.getWielded());
        Weapon w2 = new Weapon();
        b.setWielded(w2);
        assertEquals("wrong equipped weapon", w2, b.getWielded());
    }

    public void testWearTakeoff() throws EquipFailedException {
        DefaultNHBot b = new TBot();
        b.setForm(new Form(null, null, new Slot(SlotType.torso, "torso", 50),
                    new Slot(SlotType.hand, "hand", 50)));
        Item a = new Armor();
        b.wear(a);
        b.takeOff(a);
        try {
            b.takeOff(a);
            fail("not wearing");
        }
        catch(EquipFailedException good) {
        }
    }

    public void testPolymorph() throws EquipFailedException {
        Floor f = new Floor();
        Auf auf = new Auf();
        auf.setForm(new Handed());
        Weapon w = new Weapon();
        auf.getInventory().add(w);
        auf.setWielded(w);
        f.setOccupant(auf);
        Achse achse = new Achse();
        achse.setForm(new Nothing());
        auf.polymorph(achse);
        assertTrue("did not polymorph", auf.getForm() instanceof Nothing);
        assertNull("should have dropped weapon", auf.getWielded());
    }

    public void testTurn() {
        BasicBot b1 = new BasicBot();
        b1.setCommon("x");
        b1.setForm(new Form(new Weapon(), null, new Slot(SlotType.hand, "hand", 100)));
        BasicBot b2 = new BasicBot();
        b2.setForm(new Handed());
        b2.setCommon("x");
        b1.setThreat(b2, Threat.friendly);
        b2.setThreat(b1, Threat.friendly);

        Level lev = new Level(20, 20);
        lev.addRoom(new Level.Room(0, 0, 10, 10, 10, 10), true);
        lev.getSpace(5, 5).setOccupant(b1);
        lev.getSpace(6, 5).setOccupant(b2);
        NHEnvironment.setMechanics(new QuantumMechanics());
        b1.getEnvironment().approach(b2, 10, true);
        assertEquals("did not turn", Threat.kos, b2.threat(b1));
    }

    static class Weapon extends Item implements Armament {
        public String getStats() { return "so i'm sorry if i ever resisted"; }
        public String getModel() { return "i never had a doubt that you ever existed"; }
        public String getColor() { return "i only have a problem when people insist on"; }
        public String getCategory() { return "taking their hate and placing it on your name"; }
        public SlotType getSlotType() { return SlotType.hand; }
        public float getSize() { return 0f; }
        public float getWeight() { return 0f; }
        public void invoke(NHBot invoker) { }
        public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return null; }
        public String getSkill() { return null; }
        public int getPower() { return 4; }
        public int getModifiedPower() { return getPower(); }
        public int getRate() { return 70; }
        public Type getType() { return Type.melee; }
        public String getVerb() { return "null"; }
        public Item toItem() { return this; }
        public void invoke(NHBot b, NHSpace s, Attack a) {}
    }

    static class Armor extends Item implements Armament {
        public String getStats() { return "nothing"; }
        public String getModel() { return "lost"; }
        public String getColor() { return "nothing"; }
        public String getCategory() { return "gained"; }
        public SlotType getSlotType() { return SlotType.torso; }
        public float getSize() { return 0f; }
        public float getWeight() { return 0f; }
        public void invoke(NHBot invoker) { }
        public Attack invoke(NHBot attacker, NHBot defender, Attack a) { return null; }
        public String getSkill() { return null; }
        public int getPower() { return 4; }
        public int getModifiedPower() { return getPower(); }
        public int getRate() { return 70; }
        public Type getType() { return Type.melee; }
        public String getVerb() { return "null"; }
        public Item toItem() { return this; }
        public void invoke(NHBot b, NHSpace s, Attack a) {}
    }

    public static class Handed extends Form {
        public Handed() {
            super(null, null, new Slot(SlotType.hand, "hand", 50), new Slot(SlotType.hand, "hand", 50));
        }
    }

    private static class Nothing extends Form {
        public Nothing() {
            super(null, null, new Slot(SlotType.useless, "random projection", 100));
        }
    }

    private static class TBot extends DefaultNHBot {
        public void act() {
        }

        public void setEventSource(EventSource e) {
        }
    }

    private static class Auf extends DefaultNHBot {
        public void act() {
        }

        public void setEventSource(EventSource e) {
        }
    }

    private static class Achse extends DefaultNHBot {
        public void act() {
        }

        public void setEventSource(EventSource e) {
        }
    }
}
