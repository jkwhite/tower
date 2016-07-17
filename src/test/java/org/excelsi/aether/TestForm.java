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


public class TestForm extends junit.framework.TestCase {
    public void testVacateFail() {
        Slot s = new Slot(SlotType.hand, "hand", 100);
        try {
            s.vacate();
            fail("can't vacate");
        }
        catch(IllegalStateException good) {
        }
    }

    public void testOccupyFail() {
        Slot s = new Slot(SlotType.hand, "hand", 100);
        Item w = new TestDefaultNHBot.Weapon();
        s.occupy(w);
        try {
            s.occupy(w);
            fail("can't hold two things");
        }
        catch(IllegalStateException good) {
        }
        s = new Slot(SlotType.arm, "arm", 100);
        try {
            s.occupy(w);
            fail("slot type mismatch");
        }
        catch(IllegalArgumentException good) {
        }
    }

    public void testPercentage() {
        try {
            Form f = new Form(null, null,
                new Slot(SlotType.hand, "hand", 25),
                new Slot(SlotType.hand, "hand", 25),
                new Slot(SlotType.leg, "leg", 25));
            fail("shouldn't be able to create");
        }
        catch(IllegalArgumentException good) {
        }
    }

    public void testSlots() {
        try {
            new Form(null, null);
            fail("no slots");
        }
        catch(IllegalArgumentException good) {
        }
        Form f = new Form(null, null,
            new Slot(SlotType.hand, "hand", 25),
            new Slot(SlotType.hand, "hand", 25),
            new Slot(SlotType.leg, "leg", 25),
            new Slot(SlotType.head, "head", 25));
        assertEquals("wrong count", 2, f.getSlots(SlotType.hand).length);
        for(int i=0;i<10;i++) {
            assertNotNull("null slot", f.getRandomSlot());
        }
    }

    public void testEquip() throws EquipFailedException {
        Form f = new Form(null, null,
            new Slot(SlotType.hand, "hand", 100));
        Item w = new TestDefaultNHBot.Weapon();
        assertFalse("isn't equipped", f.isEquipped(w));
        f.equip(w);
        assertTrue("is equipped", f.isEquipped(w));

        Item w2 = new TestDefaultNHBot.Weapon();
        try {
            f.equip(w2);
            fail("no free hands");
        }
        catch(EquipFailedException good) {
        }
        f.unequip(w);
        try {
            f.unequip(w2);
            fail("wasn't equipped");
        }
        catch(IllegalArgumentException good) {
        }
    }

    public void testNatural() {
        Form f = new Form(new TestDefaultNHBot.Weapon(), new TestDefaultNHBot.Weapon(),
            new Slot(SlotType.hand, "hand", 100));
        assertTrue("weapon", f.getNaturalWeapon() instanceof TestDefaultNHBot.Weapon);
        assertTrue("armor", f.getNaturalArmor() instanceof TestDefaultNHBot.Weapon);
        f.setNaturalWeapon(null);
        f.setNaturalArmor(null);
    }

    public void testSlotType() {
        assertFalse("wear/wield", SlotType.hand.getVerb().equals(SlotType.torso.getVerb()));
    }
}
