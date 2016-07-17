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


public class TestTransformatives extends junit.framework.TestCase {
    static Class[] TEST = new Class[]{Strength.class, Agility.class, Weakness.class, Concussion.class,
            Frenzy.class, Rewiring.class, Cyanide.class, Poison.class, Healing.class,
            Confusion.class, Polymorph.class, Levitation.class, Tunneling.class, Speed.class,
            Sloth.class, WaterInfliction.class};

    public void testUnidentifiedNames() throws Exception {
        for(Class c:TEST) {
            Transformative t = (Transformative) c.newInstance();
            t.setIdentified(false);
            t.setClassIdentified(false);
            assertEquals("your face your lips your hips your eyes they meet: "+t, Fragment.GrammarType.adjective, t.getPartOfSpeech());
            assertEquals("but you're not hungry though: "+t, t.getColor(), t.getText());
        }
    }

    public void testIdentifiedNames() throws Exception {
        for(Class c:TEST) {
            Transformative t = (Transformative) c.newInstance();
            t.setIdentified(true);
            t.setClassIdentified(true);
            assertEquals("one year twenty years forty years fifty years: "+t, Fragment.GrammarType.nounPhrase, t.getPartOfSpeech());
            assertEquals("down the road in your life: "+t, t.getName(), t.getText());
        }
    }
}
