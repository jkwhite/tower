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
import org.excelsi.matrix.MSpace;


public class TBotMixin extends BotMixin {
    private static final long serialVersionUID = 1L;


    protected MSpace spaceFor(Level level, NHBot b, boolean anywhere) {
        if(b.isAquatic()||b.getForm().getHabitat()==Habitat.aquatic) {
            return level.findRandom(new Level.SpaceFilter() {
                public boolean accept(MSpace m) {
                    return m instanceof Water && !m.isOccupied();
                }
            });
        }
        else {
            MSpace s = null;
            int tries = 0;
            do {
                s = super.spaceFor(level, b, anywhere);
            } while(++tries<100&&(s instanceof ShopFloor||s instanceof Water));
            if(s==null) {
                s = level.findRandom(new Level.SpaceFilter() {
                    public boolean accept(MSpace m) {
                        return !(m instanceof Water) && m.isWalkable() && !m.isOccupied();
                    }
                });
            }
            return s;
            //return super.spaceFor(level, b, anywhere);
        }
    }

    protected ItemFilter createItemFilter(final NHBot bot) {
        return new ItemFilter() {
            public boolean accept(Item i, NHBot b) {
                if("quivering blob".equals(bot.getCommon())) {
                    return i.getOccurrence()<5;
                }
                return true;
            }
        };
    }
}
