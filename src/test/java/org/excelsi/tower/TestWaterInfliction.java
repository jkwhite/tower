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


public class TestWaterInfliction extends junit.framework.TestCase {
    public void testCursedInflict() {
        WaterInfliction s = new WaterInfliction();
        s.setStatus(Status.cursed);
        Patsy p = new Patsy();
        s.inflict(p);
    }

    public void testUncursedInflict() {
        WaterInfliction s = new WaterInfliction();
        Patsy p = new Patsy();
        s.inflict(p);
    }

    public void testBlessedInflict() {
        WaterInfliction s = new WaterInfliction();
        s.setStatus(Status.blessed);
        Patsy p = new Patsy();
        s.inflict(p);
    }

    public void testCursedApply() {
        apply(Status.cursed, Status.cursed, Status.cursed);
        apply(Status.cursed, Status.uncursed, Status.cursed);
        apply(Status.cursed, Status.blessed, Status.uncursed);
    }

    public void testUncursedApply() {
        apply(Status.uncursed, Status.cursed, Status.cursed);
        apply(Status.uncursed, Status.uncursed, Status.uncursed);
        apply(Status.uncursed, Status.blessed, Status.blessed);
    }

    public void testBlessedApply() {
        apply(Status.blessed, Status.cursed, Status.uncursed);
        apply(Status.blessed, Status.uncursed, Status.blessed);
        apply(Status.blessed, Status.blessed, Status.blessed);
    }

    private void apply(Status water, Status onto, Status result) {
        WaterInfliction w = new WaterInfliction();
        w.setStatus(water);
        BowlOfRamen r = new BowlOfRamen();
        r.setStatus(onto);
        w.apply(r, new Patsy());
        assertEquals("wrong status for "+water+" + "+onto, result, r.getStatus());
    }
}
