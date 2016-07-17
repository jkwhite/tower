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


public class TestPhysicals extends junit.framework.TestCase {
    static Class[] TEST = new Class[]{Fire.class, Lightning.class, Cold.class};

    public void testUnidentifiedNames() throws Exception {
        for(Class c:TEST) {
            Physical t = (Physical) c.newInstance();
            t.setIdentified(false);
            t.setClassIdentified(false);
            assertEquals("and if your head explodes: "+t, Fragment.GrammarType.adjective, t.getPartOfSpeech());
            if(t instanceof Fire) {
                assertEquals("wrong fire name", "flaming", t.getText());
            }
            else {
                assertEquals("with dark forbodings too: "+t, t.getColor(), t.getText());
            }
        }
    }

    public void testIdentifiedNames() throws Exception {
        for(Class c:TEST) {
            Physical t = (Physical) c.newInstance();
            t.setIdentified(true);
            t.setClassIdentified(true);
            assertEquals("and if the cloudbursts: "+t, Fragment.GrammarType.nounPhrase, t.getPartOfSpeech());
            assertEquals("thunder in your ear: "+t, t.getName(), t.getText());
        }
    }

    public void testAttributes() throws Exception {
        for(Class c:TEST) {
            Physical p = (Physical) c.newInstance();
            if(p instanceof Armament) {
                Armament t = (Armament) p;
                assertTrue("bad "+t, t.getPower()>=0);
                assertTrue("bad "+t, t.getModifiedPower()>=0);
                assertTrue("bad "+t, t.getRate()>=0);
                assertNotNull("bad "+t, t.getType());
                assertTrue("bad "+t, t.getHp()>=0);
                assertNotNull("bad "+t, t.getSkill());
                assertNotNull("bad "+t, t.getModel());
                assertNull("bad "+t, t.toItem());
            }
        }
    }
}
