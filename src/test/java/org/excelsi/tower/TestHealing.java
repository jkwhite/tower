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


public class TestHealing extends junit.framework.TestCase {
    public void testCursed() {
        Healing s = new Healing();
        s.setStatus(Status.cursed);
        Patsy p = new Patsy();
        p.setMaxHp(10);
        s.inflict(p);
        assertTrue("did not get hp boost", p.getHp()>0);
        p.setMaxHp(1000);
        p.setHp(p.getMaxHp());
        s.inflict(p);
        assertTrue("did not get max hp boost", p.getMaxHp()>1000);
    }

    public void testUncursed() {
        Healing s = new Healing();
        Patsy p = new Patsy();
        p.setMaxHp(10);
        s.inflict(p);
        assertTrue("did not get hp boost", p.getHp()>0);
        p.setMaxHp(1000);
        p.setHp(p.getMaxHp());
        s.inflict(p);
        assertTrue("did not get max hp boost", p.getMaxHp()>1000);
    }

    public void testBlessed() {
        Healing s = new Healing();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        p.setMaxHp(10);
        s.inflict(p);
        assertTrue("did not get hp boost", p.getHp()>0);
        p.setMaxHp(1000);
        p.setHp(p.getMaxHp());
        s.inflict(p);
        assertTrue("did not get max hp boost", p.getMaxHp()>1000);
    }
}
