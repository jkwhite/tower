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


public class TestIdentify extends junit.framework.TestCase {
    public void testCursed() {
        Patsy p = new Patsy();
        p.setForm(new Humanoid());
        Potion potion = new Potion(new Strength());
        potion.setIdentified(true);
        potion.setClassIdentified(true);
        p.getInventory().add(potion);
        ScrollOfIdentify s = new ScrollOfIdentify();
        s.setStatus(Status.cursed);
        p.getInventory().add(s);
        s.invoke(p);
        assertFalse("potion should not be ident", potion.isIdentified());
        assertFalse("potion class should not be ident", potion.isClassIdentified());
        assertTrue("didn't revert name", potion.toString().endsWith(potion.getColor()+" potion"));
    }

    public void testUncursed() {
        Patsy p = new Patsy();
        Potion potion = new Potion(new Strength());
        potion.setIdentified(false);
        potion.setClassIdentified(false);
        p.getInventory().add(potion);
        ScrollOfIdentify s = new ScrollOfIdentify();
        p.getInventory().add(s);
        s.invoke(p);
        assertEquals("didn't identify", "an uncursed potion of strength", potion.toString());
    }

    public void testBlessed() {
        Patsy p = new Patsy();
        Potion potion1 = new Potion(new Strength());
        Potion potion2 = new Potion(new Agility());
        potion1.setIdentified(false);
        potion1.setClassIdentified(false);
        potion2.setIdentified(false);
        potion2.setClassIdentified(false);
        Item i0 = new Broadsword();
        i0.setClassIdentified(true);
        i0.setIdentified(true);
        p.getInventory().add(i0);
        p.getInventory().add(potion1);
        p.getInventory().add(potion2);
        ScrollOfIdentify s = new ScrollOfIdentify();
        s.setStatus(Status.blessed);
        p.getInventory().add(s);
        try {
            s.invoke(p);
        }
        catch(ActionCancelledException ok) {
        }
        assertEquals("didn't identify 1", "an uncursed potion of strength", potion1.toString());
        assertEquals("didn't identify 2", "an uncursed potion of agility", potion2.toString());
    }
}
