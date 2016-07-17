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


import org.excelsi.matrix.*;
import java.util.logging.Logger;
import java.util.List;


public class BotMixin implements Mixin {
    private static float _coefficient = 1f;
    private int _offset = 0;
    private boolean _wandering = true;


    public BotMixin() {
        this(0);
    }

    public BotMixin(int offset) {
        _offset = offset;
    }

    public static void setCoefficient(float coeff) {
        _coefficient = coeff;
    }

    public void setOffset(int offset) {
        _offset = offset;
    }

    public boolean isWandering() {
        return _wandering;
    }

    public void setWandering(boolean wandering) {
        _wandering = wandering;
    }

    private static final long serialVersionUID = 1L;

    public boolean match(Class c) {
        return c==Level.class || NHBot.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        if(o instanceof Level) {
            mixLevel((Level)o);
        }
        else {
            mixBot((NHBot)o);
        }
    }

    protected void mixLevel(Level level) {
        if(_wandering) {
            int count = 0;
            int max = (int) (1*_coefficient*level.normalRooms().size()) + _offset;
            try {
                while(count<max) {
                    count += addBot(level, false);
                }
            }
            catch(NoSuchBotException e) {
                Logger.global.fine("can't mix bots: "+e.getMessage());
            }
        }
        level.setSpace(new SpawnSpace(_coefficient), 0, 0);
    }

    protected MSpace spaceFor(Level level, NHBot b, boolean anywhere) {
        MSpace m = null;
        NHBot p = level.getPlayer();
        int tries = 0;
        if(anywhere) {
            List<MSpace> stairs = level.findAll(Stairs.class);
            if(stairs.size()>0) {
                for(MSpace t:stairs) {
                    if(!t.isOccupied()&&(p==null||!p.getEnvironment().getVisible().contains(t))) {
                        m = t;
                        break;
                    }
                }
            }
            else {
                do {
                    m = level.findEmptierSpace(!anywhere);
                } while(++tries<5000&&p!=null&&p.getEnvironment().getVisible().contains(m));
            }
        }
        else {
            do {
                m = level.findEmptierSpace(!anywhere);
            } while(++tries<5000&&p!=null&&p.getEnvironment().getVisible().contains(m));
        }
        return m;
    }

    protected int addBot(final Level level, boolean anywhere) {
        int count = 0;
        BotFactory.Constraints c = new BotFactory.Constraints() {
            public boolean accept(NHBot b) {
                return !b.isUnique()&&!b.isPlayer()&&level.getFloor()>=b.getMinLevel()&&level.getFloor()<=b.getMaxLevel();
            }
        };
        final NHBot b = Universe.getUniverse().createBot(c);
        mixBot(b);
        MSpace ms = spaceFor(level, b, anywhere); //level.findEmptierSpace();
        if(ms==null) {
            return count;
            //continue;
        }
        ms.setOccupant(b);
        count++;
        if(b.getSociality()==Sociality.pack) {
            int psize = Rand.om.nextInt(3);
            Class mc = ms.getClass();
            if(mc==Stairs.class) {
                mc = Floor.class;
            }
            while(--psize>=0) {
                ms = level.findNearestEmpty(mc, (MatrixMSpace)ms);
                NHBot p = Universe.getUniverse().createBot(b.getCommon());
                ms.setOccupant(p);
                mixBot(p);
                count++;
            }
        }
        return count;
    }

    protected void mixBot(NHBot b) {
        Inventory i = b.getInventory();
        int num = 0;
        while(Rand.d100()<10) {
            num++;
        }
        while(--num>=0&&i.size()>0) {
            Item it = i.getItem()[Rand.om.nextInt(i.size())];
            if(b.isEquipped(it)) {
                try {
                    if(b.getWielded()==it) {
                        b.setWielded(null);
                    }
                    else {
                        b.takeOff(it);
                    }
                }
                catch(EquipFailedException e) {
                    // cursed
                    continue;
                }
            }
            i.remove(it);
        }
        num = b.getLoot();
        while(Rand.d100()<10) {
            num++;
        }
        while(--num>=0) {
            i.add(Universe.getUniverse().createItem(createItemFilter(b)));
        }
    }

    protected ItemFilter createItemFilter(NHBot bot) {
        return new ItemFilter() {
            public boolean accept(Item i, NHBot b) {
                return true;
            }
        };
    }

    public class SpawnSpace extends DefaultNHSpace {
        private int _rate;


        public SpawnSpace(float coefficient) {
            super("gray");
            _rate = (int) (10f*coefficient);
        }

        public void update() {
            try {
                if(Rand.d100(10)&&Rand.d100(_rate)) {
                    int tries = 0;
                    while(++tries<10&&addBot((Level)getMatrix(), true)==0);
                }
            }
            catch(NoSuchBotException e) {
                // no bots for this level, apparently
            }
        }

        public MatrixMSpace union(MatrixMSpace m) {
            if(!(m instanceof SpawnSpace)) {
                return super.union(m);
            }
            return this;
        }

        public boolean isTransparent() {
            return false;
        }

        public boolean isWalkable() {
            return false;
        }

        public String getModel() {
            return " ";
        }

        public String getColor() {
            return "white";
        }
    }
}
