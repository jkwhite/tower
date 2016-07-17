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
import java.util.List;
import java.util.ArrayList;
import org.excelsi.matrix.MSpace;
import java.util.logging.Logger;


public class BookOfSands extends Book {
    public BookOfSands() {
    }

    public String getName() {
        if(isClassIdentified()) {
            return "Book of Sands";
        }
        else {
            return "cloth-bound book";
        }
    }

    public void invoke(NHBot b) {
        N.narrative().print(b, Grammar.start(b, "flip")+" to a random page.");
        randomInfliction(b);
    }

    public static void randomInfliction(NHBot b) {
        Class[] classes = Basis.getRegisteredClasses();
        Object effect;
        try {
            effect = classes[Rand.om.nextInt(classes.length)].newInstance();
        }
        catch(Exception e) {
            Logger.global.severe(e.toString());
            N.narrative().printf(b, "Nothing happens.");
            return;
        }
        if(effect instanceof Infliction) {
            Infliction i = (Infliction) effect;
            List<NHBot> bots = new ArrayList<NHBot>();
            bots.add(b);
            for(MSpace m:b.getEnvironment().getMSpace().surrounding()) {
                if(m!=null&&m.getOccupant()!=null) {
                    bots.add((NHBot)m.getOccupant());
                }
            }
            NHBot lucky = bots.get(Rand.om.nextInt(bots.size()));
            i.setStatus(Status.random());
            i.inflict(lucky);
        }
    }

    public boolean isUnique() {
        return true;
    }

    public int getFindRate() {
        return 0;
    }
}
