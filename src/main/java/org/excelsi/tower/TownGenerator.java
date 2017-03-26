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
import org.excelsi.matrix.*;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import static org.excelsi.tower.Shops.Building;
import static org.excelsi.aether.Skelevel.Partition;
import static org.excelsi.aether.Skelevel.Layout;
import static org.excelsi.aether.Skelevel.Pattern;


public class TownGenerator {
    private final boolean _shops;
    private final boolean _stairs;


    public TownGenerator(boolean shops, boolean stairs) {
        _shops = shops;
        _stairs = stairs;
    }

    public void generateTown(final Level level, final String guardian) {
        final int min = 9;
        final int max = level.width()-10;
        for(Level.Room r:level.normalRooms()) {
            List<Level.Room.Connector> cs = Arrays.asList(r.getConnectors());
            Collections.shuffle(cs, Rand.om);
            for(Level.Room.Connector c:cs) {
                if(c.away()[0]<=min||c.away()[0]>=max||c.away()[1]<=4||c.away()[1]>=level.height()-4) {
                    continue;
                }
                if(level.getSpace(c.away()[0], c.away()[1])==null) {
                    TDoorway td = new TDoorway();
                    td.setVertical(c.isVertical());
                    level.setSpace(td, c.coord()[0], c.coord()[1]);
                    break;
                }
            }
        }
        // first, remove stairways from buildings and place outside
        List<MSpace> stairs = level.findAll(Stairs.class);
        for(MSpace m:stairs) {
            m.replace(new Floor());
        }
        if(_stairs) {
            for(MSpace m:stairs) {
                boolean found = false;
                do {
                    int i = Rand.om.nextInt(max-min)+min;
                    int j = Rand.om.nextInt(level.height());
                    if(level.getSpace(i,j)!=null) {
                        continue;
                    }
up:                 for(int x=i-2;x<=i+2;x++) {
                        for(int y=j-2;y<=j+2;y++) {
                            if(level.getSpace(x,y) instanceof Wall) {
                                level.setSpace((MatrixMSpace)m, i, j);
                                found = true;
                                break up;
                            }
                        }
                    }
                } while(!found);
            }
        }

        // for each room,
        //   create path to other room,
        //   fill with floor.
        //   and draw single row of floor around this room
        //   then fill null border spaces with walls
        Filter f = new Filter() {
            public boolean accept(MSpace s) {
                MatrixMSpace sp = (MatrixMSpace) s;
                if(sp.getI()<min||sp.getJ()<0||sp.getI()>max||sp.getJ()>level.height()) {
                    return false;
                }
                return true;
            }
        };
        for(Level.Room r1:level.normalRooms()) {
            for(Level.Room r2:level.normalRooms()) {
                if(r1==r2) {
                    continue;
                }
                MSpace start = level.getSpace(r1.centerX(), r1.centerY());
                MSpace end = level.getSpace(r2.centerX(), r2.centerY());
                MSpace[][] paths = start.paths(end, true, 1, f, 1f, null);
                for(MSpace m:paths[Rand.om.nextInt(1)]) {
                    if(m.isNull()) {
                        m.replace(new Floor());
                    }
                }
            }
        }

        for(int i=min+1;i<max-1;i++) {
            for(int j=1;j<level.height()-1;j++) {
                int tot = 0;
                if(level.getSpace(i,j)!=null) {
                    continue;
                }
                for(int x=i-1;x<=i+1;x++) {
                    for(int y=j-1;y<=j+1;y++) {
                        MSpace q = level.getSpace(x,y);
                        if(q instanceof Wall || q instanceof Stairs) {
                            tot++;
                        }
                    }
                }
                if(tot>0) {
                    level.setSpace(new Floor(), i, j);
                }
            }
        }
        for(int i=min;i<max;i++) {
            for(int j=1;j<level.height()-1;j++) {
                if(level.getSpace(i,j)!=null) {
                    continue;
                }
                int totf = 0;
                int totn = 0;
                for(int x=i-1;x<=i+1;x++) {
                    for(int y=j-1;y<=j+1;y++) {
                        if(level.getSpace(x,y) instanceof Floor) {
                            totf++;
                        }
                        else if(level.getSpace(x,y)==null) {
                            totn++;
                        }
                    }
                }
                if(totf>0&&totn>0) {
                    level.setSpace(guardian!=null?new IrreplaceableWall():new Wall(), i, j);
                }
            }
        }
        if(_shops) {
            Shops.setShuffle(false);
            Shops.setChance(101);
            Shops.setAmount(100);
            Shops s = new Shops();
            if(level.getFloor()==25) {
                s.getTypes().add(0,
                    new Store(new CategoryFilter("we're all sinking in our own mud"), null, "The Clocktower") {
                        public boolean hasKeeper() { return false; }

                        public void modulate(Building build) {
                            MSpace s = level.getSpace((build.x1+build.x2)/2, (build.y1+build.y2)/2);
                            s.replace(new Stairs(true, 1));
                        }
                    });
                s.getTypes().add(0,
                    new Store(new Multifilter(new CategoryFilter("scroll"), new CategoryFilter("book")), null, "The Library") {
                        public void modulate(Building build) {
                            MSpace s = level.getSpace((build.x1+build.x2)/2, (build.y1+build.y2)/2);
                            s.replace(new Stairs(false, 1));
                        }
                    });
            }
            s.mix(level);
        }
        /*
        if(guardian!=null) {
            for(int i=0;i<10;i++) {
                MSpace rs = level.findRandomEmptySpace();
                if(rs!=null) {
                    rs.setOccupant(Universe.getUniverse().createBot(guardian));
                }
            }
            BotMixin.setCoefficient(0.1f);
        }
        */
        //BotMixin bm = new TBotMixin();
        //bm.setWandering(false);
        //bm.mix(level);
    }

}
