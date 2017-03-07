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


import org.excelsi.matrix.MSpace;
import org.excelsi.aether.*;
import java.util.*;


public class Stands implements Mixin<Level> {
    private int _chance = 10;


    public void setChance(int chance) {
        _chance = chance;
    }

    public boolean match(Class c) {
        return c==Level.class;
    }

    @Override public void mix(Level level) {
        final int max = (int)(Math.sqrt(level.width()*level.height())/10);
        System.err.println("adding "+max+" stands");
        for(int i=0;i<max;i++) {
            final NHSpace r = (NHSpace) level.findRandom((s)->{
                if(s instanceof Grass) {
                    final MSpace[] sur = s.cardinal();
                    return !((NHSpace)s).hasParasite(Plant.class)
                        && (sur[0] instanceof Grass
                        || sur[1] instanceof Grass
                        || sur[2] instanceof Grass
                        || sur[3] instanceof Grass);
                }
                else {
                    return false;
                }
            });
            if(r!=null) {
                int size = Rand.om.nextInt(50)+3;
                List<NHSpace> frontier = new LinkedList<>();
                frontier.add(r);
                while(size-->0 && !frontier.isEmpty()) {
                    final NHSpace s = frontier.remove(0);
                    if(!s.hasParasite(Plant.class)) {
                        ((NHSpace)s).addParasite(new CamphorTree());
                    }
                    for(MSpace m:s.surrounding()) {
                        if(m instanceof Grass && !((NHSpace)m).hasParasite(Plant.class)) {
                            frontier.add((NHSpace)m);
                        }
                    }
                }
            }
        }
    }
}
