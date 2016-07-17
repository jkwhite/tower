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


import org.excelsi.aether.Level;
import org.excelsi.aether.NHSpace;
import org.excelsi.matrix.MSpace;
import org.excelsi.aether.Mixin;
import org.excelsi.aether.Floor;


public abstract class FeatureMixin implements Mixin {
    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        Level level = (Level) o;
        //MSpace m = level.findRandomNormalEmptySpace();
        MSpace m = level.findRandom(createFilter());
        if(m!=null) {
            addFeature((NHSpace)m);
        }
    }

    protected abstract void addFeature(NHSpace space);

    protected Level.SpaceFilter createFilter() {
        return new Level.SpaceFilter() {
            public boolean accept(MSpace s) {
                for(MSpace m:s.surrounding()) {
                    if(m==null||m.getClass()!=Floor.class) {
                        return false;
                    }
                    for(MSpace o:m.surrounding()) {
                        if(o==null||o.getClass()!=Floor.class) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };
    }
}
