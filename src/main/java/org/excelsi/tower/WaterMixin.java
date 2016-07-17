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
import java.util.List;
import org.excelsi.matrix.MSpace;


public class WaterMixin implements Mixin {
    private static int _chance = 1;
    private static int _amount = 1;
    private static int _wholeRoom = 67;
    private static int _surroundChance = 20;
    private static boolean _surround = false;
    private static boolean _passageways = false;


    public static void setChance(int chance) {
        _chance = chance;
    }

    public static void setAmount(int amount) {
        _amount = amount;
    }

    public static void setSurround(boolean surround) {
        _surround = surround;
    }

    public static void setSurroundChance(int chance) {
        _surroundChance = chance;
    }

    public static void setPassageways(boolean passageways) {
        _passageways = passageways;
    }

    public static void setWholeRoomChance(int chance) {
        _wholeRoom = chance;
    }

    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        int created = 0;
        Level level = (Level) o;
        if(_passageways) {
            for(int i=1;i<level.width()-1;i++) {
                for(int j=1;j<level.height()-1;j++) {
                    MSpace m = level.getSpace(i,j);
                    if(m instanceof Ground && m.isReplaceable()) {
                        if(m.getOccupant()==null||!((NHBot)m.getOccupant()).isPlayer()) {
                            m.replace(new Water(8,0f));
                        }
                    }
                }
            }
            for(int i=1;i<level.width()-1;i++) {
                for(int j=1;j<level.height()-1;j++) {
                    MSpace m = level.getSpace(i,j);
                    if(m==null||m.isReplaceable()) {
up:                     for(int x=i-1;x<=i+1;x++) {
                            for(int y=j-1;y<=j+1;y++) {
                                if(x==i&&y==j) {
                                    continue;
                                }
                                if(x==i||y==j) {
                                    MSpace n = level.getSpace(x,y);
                                    if(n instanceof Water && ((Water)n).getDepth()==8 && ! (m instanceof Water)) {
                                        level.setSpace(new Water(4, 0f), i, j);
                                        break up;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
        if(Rand.d100(_chance)) {
            // add pool of water
            Status s = Status.uncursed;
            {
                int r = Rand.om.nextInt(100);
                if(r<=10) {
                    s = Status.cursed;
                }
                else if(r<=15) {
                    s = Status.blessed;
                }
            }
            List<Level.Room> rooms = level.getRooms();
            for(Level.Room r:rooms) {
                //System.err.println("EXAMINING: "+r);
                if(!r.isSpecial()&&r.width()>=4&&r.height()>=4) {
                    int x1, y1, x2, y2;
                    if(Rand.d100(_wholeRoom)||r.height()<=5||r.width()<=5) {
                        x1 = r.getX1(); x2 = r.getX2();
                        y1 = r.getY1(); y2 = r.getY2();
                    }
                    else {
                        x1 = r.getX1()+2; x2 = r.getX2()-2;
                        y1 = r.getY1()+2; y2 = r.getY2()-2;
                    }
                    boolean approved = true;
fast:               for(int y=y1;y<=y2;y++) {
                        for(int x=x1;x<=x2;x++) {
                            if(!(level.getSpace(x, y) instanceof Floor) &&!(level.getSpace(x,y) instanceof Wall)&& !(level.getSpace(x,y) instanceof Doorway)) {
                                // don't overwrite structures
                                approved = false;
                                break fast;
                            }
                        }
                    }
                    if(!approved) {
                        continue;
                    }
                    float cyc = 0f;
                    for(int y=y1;y<=y2;y++) {
                        for(int x=x1;x<=x2;x++) {
                            if((x==x1&&y==y1)||(x==x1&&y==y2)
                                ||(x==x2&&y==y1)||(x==x2&&y==y2)) {
                                if(x1==r.getX1()) {
                                    level.getSpace(x,y).replace(null);
                                }
                                continue;
                            }
                            int d = (x==x1||y==y1||x==x2||y==y2)?4:8;
                            if(x1==r.getX1()&&((x==x1+1&&y==y1+1)||(x==x2-1&&y==y1+1)||(x==x1+1&&y==y2-1)||(x==x2-1&&y==y2-1))) {
                                d = 4;
                            }
                            Water w = new Water(d, cyc);
                            w.setStatus(s);
                            level.getSpace(x, y).replace(w);
                        }
                        if(cyc==0f) {
                            cyc = 1f;
                        }
                        else {
                            cyc = 0f;
                        }
                        //cyc += 0.2f;
                    }
                    ++created;
                    if(created==_amount||created==level.getRooms().size()) {
                        break;
                    }
                }
            }
        }
        if(_surround) {
            for(int i=1;i<level.width()-1;i++) {
                for(int j=1;j<level.height()-1;j++) {
                    MSpace m = level.getSpace(i,j);
                    if(m!=null && m.isReplaceable() && (m instanceof Ground || m instanceof Wall)) {
                        if(Rand.d100(_surroundChance)) {
                            m.replace(new Water(4, 0f));
                        }
                    }
                }
            }
            for(int build=0;build<level.width()*level.height();build++) {
                int i = Rand.om.nextInt(level.width()-1)+1;
                int j = Rand.om.nextInt(level.height()-1)+1;
                MSpace m = level.getSpace(i,j);
                if(m==null) {
                    int dw = 0, sw = 0, other = 0;
                    for(int x=i-1;x<=i+1;x++) {
                        for(int y=j-1;y<=j+1;y++) {
                            MSpace n = level.getSpace(x,y);
                            if(n != null) {
                                if(n instanceof Water) {
                                    if(((Water)n).getDepth()<=4) {
                                        sw++;
                                    }
                                    else {
                                        dw++;
                                    }
                                }
                                else {
                                    other++;
                                }
                            }
                        }
                    }
                    Water replacement = null;
                    if(sw>3||dw>0) {
                        replacement = new Water(8, 0f);
                    }
                    else if(sw>0) {
                        replacement = new Water(4, 0f);
                    }
                    if(replacement!=null) {
                        level.setSpace(replacement, i, j);
                    }
                }
            }
        }
    }
}
