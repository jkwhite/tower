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


import org.excelsi.matrix.*;


public class Items implements Mixin {
    private static final long serialVersionUID = 1L;
    private int _offset;


    public Items() {
        this(0);
    }

    public Items(int offset) {
        _offset = offset;
    }

    public boolean match(Class c) {
        return c==Level.class;
    }

    public void mix(Object o) {
        Level level = (Level) o;
        for(int i=0;i<level.getRooms().size()+_offset;i++) {
            final String cat = Universe.getUniverse().randomCategory();
            float chance = level.getFloor()/77f;
            if(level.getFloor()>77||level.getFloor()<1) {
                chance = 1f;
            }
            Item it = null;
            do {
                it = Universe.getUniverse().createItem(new ItemFilter() {
                    public boolean accept(Item i, NHBot bot) {
                        return i.getCategory().equals(cat);
                    }
                });
            } while(1f-(Math.abs(chance-it.getLevelWeight()))<Rand.om.nextFloat());
            NHSpace ms = (NHSpace) level.findRandomEmptySpace();
            while(!(ms instanceof Floor)) {
                ms = (NHSpace) level.findRandomEmptySpace();
            }
            ms.add(it);
        }
    }
}
