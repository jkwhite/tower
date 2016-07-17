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


public class Coinage implements Mixin {
    private static int _chance = 20;


    public static void setChance(int chance) {
        _chance = chance;
    }

    public boolean match(Class c) {
        return NPC.class.isAssignableFrom(c)
            || Level.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        if(o instanceof NPC) {
            NPC b = (NPC) o;
            int chance = 6;
            if(b.getForm() instanceof Humanoid) {
                chance = 40;
            }
            if(Rand.d100(chance)) {
                b.getInventory().add(new Gold(Rand.om.nextInt(b.getMaxHp())+1));
            }
        }
        else {
            Level lev = (Level) o;
            while(Rand.d100(_chance)) {
                MSpace m = null;
                int x = 0, y = 0;
                int tries = 0;
                do {
                    x = Rand.om.nextInt(lev.width());
                    y = Rand.om.nextInt(lev.height());
                    m = lev.getSpace(x, y);
                } while(m instanceof Wall || m instanceof Doorway);
                if(m==null) {
                    Blank b = new Blank();
                    lev.setSpace(b, x, y);
                    m = b;
                }
                ((NHSpace)m).add(new Gold(Rand.om.nextInt(10*(1+lev.getFloor()))+25));
            }
            if(Rand.d100(3)) {
                int[] xy = lev.findNull(6,6);
                if(xy!=null) {
                    for(int x=xy[0]+1;x<=xy[0]+4;x++) {
                        for(int y=xy[1]+1;y<=xy[1]+4;y++) {
                            if(x==xy[0]+1||x==xy[0]+4||y==xy[1]+1||y==xy[1]+4) {
                                lev.setSpace(new Wall(), x, y);
                            }
                            else {
                                Floor f = new Floor();
                                lev.setSpace(f, x, y);
                                f.add(new Gold(Rand.om.nextInt(300)+42));
                                while(Rand.d100(33)) {
                                    Item gem = Universe.getUniverse().createItem(new ItemFilter() {
                                        public boolean accept(Item item, NHBot bot) {
                                            return !(item instanceof SmallStone) && !item.isUnique() && item instanceof Gem && !(item instanceof WorthlessPieceOfGlass);
                                        }
                                    }, false);
                                    f.add(gem);
                                }
                                if(Rand.d100(25)) {
                                    f.setOccupant(Universe.getUniverse().createBot("guard"));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
