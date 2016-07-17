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


public class WritingMixin implements Mixin {
    private String[] _writings;

    private static final String[] WRITINGS = new String[]{
        "Orbis terrarum est speculum Ludi",
        "Sleeping is giving in",
        "They say cake occurs naturally.",
        "They say canines love handouts.",
        "They say felines are powerless against yarn."
    };

    public WritingMixin() {
        this(WRITINGS);
    }

    public WritingMixin(String[] writings) {
        _writings = writings;
    }

    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        Level level = (Level) o;
        int count = 0;
        while(Rand.d100(10)&&++count<10) {
            NHSpace s;
            do {
                s = (NHSpace) level.findEmptierSpace();
                if(s==null) {
                    return; // out of room
                }
            } while(hasWriting(s)||s.getClass()!=Floor.class);
            s.addParasite(new Writing(_writings[Rand.om.nextInt(_writings.length)], Rand.om.nextInt(10)+90));
        }
    }

    private boolean hasWriting(NHSpace s) {
        for(Parasite p:s.getParasites()) {
            if(p instanceof Writing) {
                return true;
            }
        }
        return false;
    }
}
