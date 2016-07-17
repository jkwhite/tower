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
import java.util.*;


public class Farming implements Mixin {
    private static int _chance = 10;


    static {
        Extended.addCommand("plant", new PlantAction());
    }

    public static void setChance(int chance) {
        _chance = chance;
    }

    public boolean match(Class c) {
        return c==Level.class;
    }

    public void mix(Object o) {
        Level level = (Level) o;
        List<Level.Room> normals = level.normalRooms();
        Collections.shuffle(normals);
        int amt = 1;
        int count = 0;
        for(Level.Room r:normals) {
            if(Rand.d100(_chance)&&!r.contains(level, Stairs.class)) {
                Plantable p = (Plantable) Universe.getUniverse().createItem(new ItemFilter() {
                    public boolean accept(Item item, NHBot bot) {
                        return item instanceof Plantable;
                    }
                });
                for(int i=r.getX1()+1;i<r.getX2();i++) {
                    for(int j=r.getY1()+1;j<r.getY2();j++) {
                        Soil s = new Soil();
                        level.setSpace(s, i, j);
                        if(Rand.d100(33)) {
                            s.addParasite(p.toSeed());
                        }
                    }
                }
                if(++count>=amt) {
                    break;
                }
            }
        }
    }
}
