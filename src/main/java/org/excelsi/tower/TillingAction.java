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


public class TillingAction implements ProgressiveAction {
    private NHBot _b;
    private NHSpace _s;
    private Direction _d;
    private int _remaining;
    private Item _instrument;


    public TillingAction(NHBot b, NHSpace s, int time, Item instrument) {
        _b = b;
        _s = s;
        _remaining = time;
        _instrument = instrument;
    }

    public NHBot getBot() {
        return _b;
    }

    public int getInterruptRate() {
        return 100;
    }

    public Item getInstrument() {
        return _instrument;
    }

    public boolean iterate() {
        if(_s!=null&&!_s.isReplaceable()) {
            N.narrative().print(_b, "This ground is too hard to dig into.");
            return false;
        }
        if(_d!=null&&(_b.getEnvironment().getLevel()==0||_b.getEnvironment().getLevel()>899)) {
            N.narrative().print(_b, "This ground is too hard to dig into.");
            return false;
        }
        if(--_remaining>0) {
            return true;
        }
        else {
            if(_s!=null) {
                if(_s.isOccupied()) {
                    N.narrative().print(_b, "There's something there.");
                    throw new ActionCancelledException();
                }
                NHSpace g = (NHSpace) _s.replace(new Soil());
                //N.narrative().print(_b, Grammar.start(_b, "till")+" the earth.");
                if(Rand.d100(10)) {
                    Rock r = new Rock();
                    r.setCount(Rand.om.nextInt(5)+1);
                    g.add(r);
                }
                if(Rand.d100(20)) {
                    SmallStone s = new SmallStone();
                    s.setCount(Rand.om.nextInt(7)+1);
                    g.add(s);
                }
                if(Rand.d100(5)) {
                    Item gem = Universe.getUniverse().createItem(new ItemFilter() {
                        public boolean accept(Item item, NHBot bot) {
                            return !(item instanceof SmallStone) && !item.isUnique() && item instanceof Gem && !(item instanceof WorthlessPieceOfGlass);
                        }
                    }, false);
                    g.add(gem);
                }
                if(Rand.d100(3)) {
                    g.add(new Gold(Rand.om.nextInt(33)+3));
                }
                if(Rand.d100(2)) {
                    N.narrative().print(_b, "Buried treasure!");
                    Item i = Universe.getUniverse().createItem(new ItemFilter() {
                        public boolean accept(Item item, NHBot bot) {
                            return item.getFindRate()>0&&item.getFindRate()<10
                                &&!(item instanceof BucketOfPaint)
                                &&(!(item instanceof WandOfWishing)||Rand.d100(10));
                        }
                    }, false);
                    g.add(i);
                }
                if(Rand.d100(1)&&_instrument.getStatus()==Status.cursed) {
                    N.narrative().print(_b, "You've tilled too deep!");
                    _b.getEnvironment().unhide();
                    g.setOccupant(Universe.getUniverse().createBot("balrog"));
                }
                else if(Rand.d100(5)) {
                    NHBot b = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                        public boolean accept(NHBot b) {
                            return (b.getForm() instanceof Rodent || b.getForm() instanceof Reptile)
                                && b.getSize()==Size.small;
                        }
                    });
                    g.setOccupant(b);
                    _b.getEnvironment().unhide();
                    N.narrative().printf(g, "You've disturbed %a!", b);
                }
            }
            else {
                //MatrixMSpace cur = (MatrixMSpace) _b.getEnvironment().getMSpace();
                NHSpace m = (NHSpace)_b.getEnvironment().getMSpace();
                if(m.getDepth()>4) {
                    if(m instanceof Water) {
                        m.replace(new Whirlpool());
                    }
                    else {
                        m.replace(new Hole());
                    }
                }
                else {
                    m.replace(new Pit());
                }
            }
            return false;
        }
    }

    public void stopped() {
    }

    public void interrupted() {
        N.narrative().print(_b, Grammar.start(_b, "stop")+ " digging.");
    }

    public String getExcuse() {
        return null;
    }
}
