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


public class TestScores extends junit.framework.TestCase {
    public void testMax() {
        Scores s = new Scores(1);
        for(int i=0;i<=10;i++) {
            Patsy p = new Patsy();
            p.setName(i+" spread the ashes of the colors");
            p.setProfession(i+" over this heart of mine");
            p.addScore(i);
            s.insert(p, "tunnels");
        }
        assertEquals("wrong size", 1, s.getScores().size());
        Scores.Score s1 = s.getScores().get(0);
        assertEquals("score", 10, s1.getScore());
        assertEquals("floor", 0, s1.getFloor());
        assertEquals("cause", "tunnels", s1.getCause());
        assertTrue("name", s1.getName().startsWith("10 s"));
        assertTrue("prof", s1.getProfession().startsWith("10 o"));
    }
}
