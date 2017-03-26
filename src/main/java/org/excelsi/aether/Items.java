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


public class Items implements Mixin<Level> {
    public enum Strategy { rooms, area };

    private static final long serialVersionUID = 1L;
    private final ItemFactory _f;
    private final int _offset;
    private final ItemFilter _filter;
    private Strategy _s = Strategy.rooms;


    public Items() {
        this(0);
    }

    public Items(Strategy s) {
        this(s, 0);
    }

    public Items(Strategy s, int offset) {
        this(Universe.getUniverse(), s, offset);
    }

    public Items(ItemFactory f, Strategy s, int offset) {
        this(f, offset);
        _s = s;
    }

    public Items(ItemFactory f, Strategy s, int offset, ItemFilter filter) {
        this(f, offset, filter);
        _s = s;
    }

    public Items(int offset) {
        this(Universe.getUniverse(), offset);
    }

    public Items(ItemFactory f, int offset) {
        this(f, offset, ItemFilter.randomCategory(f));
    }

    public Items(ItemFactory f, int offset, ItemFilter filter) {
        _f = f;
        _offset = offset;
        _filter = filter;
    }

    public boolean match(Class c) {
        return c==Level.class;
    }

    public void mix(Level level) {
        final int max = _s==Strategy.rooms?
            level.getRooms().size()+_offset
            : (int)Math.sqrt(level.width()*level.height())+_offset;

        for(int i=0;i<max;i++) {
            //final String cat = _f.randomCategory();
            /*
            float chance = level.getFloor()/77f;
            if(level.getFloor()>77||level.getFloor()<1) {
                chance = 1f;
            }
            */
            Item it = null;
            //do {
                it = _f.createItem(_filter);
                /*
                it = _f.createItem(new ItemFilter() {
                    public boolean accept(Item i, NHBot bot) {
                        return i.getCategory().equals(cat);
                    }
                });
                */
            //} while(1f-(Math.abs(chance-it.getLevelWeight()))<Rand.om.nextFloat());
            NHSpace ms = (NHSpace) level.findRandomEmptySpace();
            int tries = 5;
            while(!(ms instanceof Floor) && tries-->0) {
                ms = (NHSpace) level.findRandomEmptySpace();
            }
            //System.err.println("adding to "+ms+": "+it);
            ms.add(it);
        }
    }
}
