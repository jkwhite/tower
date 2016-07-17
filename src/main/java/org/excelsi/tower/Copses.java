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


public class Copses extends RoomMixin {
    private static int _chance = 10;


    public static void setChance(int chance) {
        _chance = chance;
    }

    protected int getChance() {
        return _chance;
    }

    protected int[] getMinDimensions() {
        return new int[]{6,6};
    }

    protected void build(Level l, Level.Room r) {
        for(int x=r.getX1();x<=r.getX2();x++) {
            for(int y=r.getY1();y<=r.getY2();y++) {
                if((x==r.getX1()&&y==r.getY1())||(x==r.getX1()&&y==r.getY2())
                    ||(x==r.getX2()&&y==r.getY1())||(x==r.getX2()&&y==r.getY2())) {
                    l.getSpace(x,y).replace(null);
                    continue;
                }
                boolean add = false;
                if(l.getSpace(x,y) instanceof Doorway) {
                    add = true;
                }
                Grass g = new Grass();
                g.setStatus(Status.blessed);
                l.setSpace(g, x, y);
                if(add) {
                    final MSpace a = l.getSpace(x,y);
                    a.addMSpaceListener(new MSpaceAdapter() {
                        public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                            if(to==a && ((NHBot)b).isPlayer()) {
                                N.narrative().print((NHBot)b, "A deep calmness washes over you.");
                                N.narrative().print((NHBot)b, "You stand in a grove of trees.");
                                a.removeMSpaceListener(this);
                            }
                        }
                    });
                }
                if(x==r.getX1()||x==r.getX2()||y==r.getY1()||y==r.getY2()) {
                    l.getSpace(x,y).addParasite(new CamphorTree());
                }
            }
        }
        l.setSpace(new Water(8, Status.blessed), r.centerX(), r.centerY());
        l.setSpace(new Water(4, Status.blessed), r.centerX()-1, r.centerY());
        l.setSpace(new Water(4, Status.blessed), r.centerX()+1, r.centerY());
        l.setSpace(new Water(4, Status.blessed), r.centerX(), r.centerY()-1);
        l.setSpace(new Water(4, Status.blessed), r.centerX(), r.centerY()+1);
        l.getSpace(r.getX1()+1, r.getY1()+1).addParasite(new CamphorTree());
        l.getSpace(r.getX2()-1, r.getY2()-1).addParasite(new CamphorTree());
        l.getSpace(r.getX1()+1, r.getY2()-1).addParasite(new CamphorTree());
        l.getSpace(r.getX2()-1, r.getY1()+1).addParasite(new CamphorTree());
    }
}
