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


public class Secrets implements Mixin {
    private static int _gchance = 1;
    private static int _dchance = 10;


    public static void setGroundChance(int chance) {
        _gchance = chance;
    }

    public static void setDoorChance(int chance) {
        _dchance = chance;
    }

    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        Level level = (Level) o;
        for(int i=0;i<level.width();i++) {
            for(int j=0;j<level.height();j++) {
                NHSpace m = level.getSpace(i,j);
                if(m instanceof Ground) {
                    int sur = 0;
                    final int maxallowed = 3;
                    for(MSpace s:m.surrounding()) {
                        if(s!=null) {
                            sur++;
                            if(sur>=maxallowed) {
                                break;
                            }
                        }
                    }
                    if(sur<maxallowed&&Rand.d100(1)) {
                        Hidden s = new Hidden(m);
                        m.replace(s);
                    }
                }
                else if(m instanceof Doorway) {
                    if(Rand.d100(10)) {
                        m.replace(new SecretDoorway((Doorway)m));
                    }
                }
            }
        }
    }

    public static final class SecretDoorway extends Wall implements Secret {
        private Doorway _d;


        public SecretDoorway(Doorway d) {
            _d = d;
        }

        public void reveal() {
            _d.setOpen(false);
            replace(_d);
        }
    }
}
