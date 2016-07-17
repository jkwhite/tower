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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.excelsi.matrix.*;


public class CALevelGenerator implements LevelGenerator {
    static final MatrixMSpace[][] PATTERNS = {
        { new Floor(),  new Floor(),  new Floor(),  new Floor() },
        { new Floor(),  new Floor(),  new Ground(), new Ground() },
        { new Floor(),  new Ground(), new Floor(),  new Floor() },
        { new Floor(),  new Ground(), new Ground(), new Floor() },
        { new Ground(), new Floor(),  new Floor(),  new Ground() },
        { new Ground(), new Floor(),  new Ground(), new Floor() },
        { new Ground(), new Ground(), new Floor(),  new Floor() },
        { new Ground(), new Ground(), new Ground(), new Floor() },
    };

    public void generate(Level level, MatrixMSpace player) {
        for(int i=0;i<level.width();i++) {
            level.setSpace(new Floor(), i, 0);
        }
        for(int i=1;i<level.height();i++) {
            for(int j=0;j<level.width();j++) {
                MatrixMSpace s = null;
                NHSpace[] above = new NHSpace[3];
                above[0] = j>0?level.getSpace(j-1, i-1):new Floor();
                above[1] = level.getSpace(j, i-1);
                above[2] = j<level.width()-1?level.getSpace(j+1, i-1):new Floor();
                for(int k=0;k<PATTERNS.length;k++) {
                    if(match(PATTERNS[k], above)) {
                        s = PATTERNS[k][3].clone();
                        break;
                    }
                }
                level.setSpace(s, j, i);
            }
        }
    }

    private static boolean match(MatrixMSpace[] pattern, NHSpace[] above) {
        for(int i=0;i<above.length;i++) {
            if(above[i]==null||pattern[i].getClass()!=above[i].getClass()) {
                return false;
            }
        }
        return true;
    }
}
