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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class WandOfDigging extends Wand {
    public static final int LENGTH = 6;


    public void invoke(NHBot b) {
        if(discharge(b)) {
            Direction chosen = getDirection();
            if(chosen==null) {
                chosen = N.narrative().direct(b, "Which direction?");
            }
            dig(b, chosen);
        }
    }

    public int getFindRate() {
        return 2;
    }

    public void dig(NHBot b, Direction d) {
        if(d==null) {
            N.narrative().print(b, Grammar.start(b, "shoot")+" a hole in "+Grammar.possessive(b)+" chest.");
            b.setHp(0);
            b.die("Suicide by wand of digging");
        }
        else if(d==Direction.down) {
            if(b.getEnvironment().getLevel()==0||b.getEnvironment().getLevel()>899) {
                N.narrative().print(b, "Ineffective.");
                throw new ActionCancelledException();
            }
            else {
                b.getEnvironment().getMSpace().replace(new Hole());
            }
        }
        else if(d==Direction.up) {
            MatrixMSpace m = (MatrixMSpace) b.getEnvironment().getMSpace();
            Level up = b.getEnvironment().getFloor(b.getEnvironment().getLevel()+1);
            up.setSpace(new Hole(), m.getI(), m.getJ());
            N.narrative().print(b, Grammar.start(b, "shoot")+" a hole in the ceiling!");
        }
        else {
            MSpace s = b.getEnvironment().getMSpace().creator();
            for(int i=0;s!=null&&i<LENGTH;i++) {
                s = s.move(d);
                if(s!=null&&!s.isTransparent()) {
                    if(!s.isReplaceable()) {
                        break;
                    }
                    s = s.replace(new Ground());
                }
            }
        }
    }
}
