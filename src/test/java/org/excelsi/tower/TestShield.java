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


public class TestShield extends junit.framework.TestCase {
    public void testConstants() {
        Buckler b = new Buckler();
        assertEquals("skill", Shield.SKILL, b.getSkill());
        assertEquals("handed", SlotType.hand, b.getSlotType());
    }

    public void testCoverage() throws EquipFailedException {
        Buckler b = new Buckler();
        assertEquals("changed coverage", 20, b.getCoverage());
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        p.wear(b);
        Slot[] ss = p.getForm().getSlots(SlotType.hand);
        assertNull("left hand not empty", ss[1].getOccupant());
        assertEquals("buckler missing", b, ss[0].getOccupant());
        assertEquals("didn't raise hit percent", b.getSlotModifier(), ss[0].getHitPercentage());

        b.setStatus(Status.blessed);
        assertEquals("didn't raise blessed hit percent", 30, ss[0].getHitPercentage());
    }
}
