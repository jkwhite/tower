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
import static org.excelsi.aether.Brain.*;


public class ShopkeeperDaemon extends Daemon {
    private Chemical _nop = new Chemical("nop");
    private NHSpace _move;
    private NHSpace _origin;


    public void poll(final Context c) {
        if(_origin==null) {
            _origin = in.b.getEnvironment().getMSpace();
        }

        strength = -1;
        if(in.attack==null) {
            _move = null;
            NHBot p = in.b.getEnvironment().getPlayer();
            MSpace self = in.b.getEnvironment().getMSpace();
            if(in.b.getEnvironment().getVisibleBots().contains(p)) {
                MSpace s = p.getEnvironment().getMSpace();
                if(s.isAdjacentTo(self)) {
                    for(MSpace sur:self.surrounding()) {
                        if(sur!=null&&!sur.isOccupied()&&sur.isWalkable()&&sur instanceof ShopFloor) {
                            if(_move==null||!sur.isAdjacentTo(s)) {
                                _move = (NHSpace) sur;
                                strength = 5;
                            }
                        }
                    }
                }
            }
            if(_move==null) {
                if(!(self instanceof ShopFloor)) {
                    _move = _origin;
                    strength = 4;
                }
            }
        }
    }

    public void setEventSource(EventSource e) {
        e.addNHSpaceListener(new NHSpaceAdapter() {
            public void occupied(MSpace s, Bot b) {
                if(b!=in.b) {
                    handle(null, s, (NHBot)b);
                }
            }

            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(b!=in.b&&source==from) {
                    handle(from, to, (NHBot)b);
                }
            }
        });

        e.addNHEnvironmentListener(new NHEnvironmentAdapter() {
            public void actionStarted(NHBot b, ProgressiveAction action) {
                if(b.isPlayer()) {
                    if(action instanceof Consume.Consuming) {
                        Consume.Consuming c = (Consume.Consuming) action;
                        if(c.getComestible().hasFragment(PriceTag.class)) {
                            if(Rand.d100(b.getModifiedPresence())) {
                                in.b.getEnvironment().face(b);
                                N.narrative().print(in.b, Grammar.start(in.b, "say")+" \"Hey, those aren't free, you know.\"");
                            }
                            else {
                                handleThievery(b, c.getComestible(), false);
                            }
                        }
                    }
                    else if(action instanceof PriceTag.DisruptAction) {
                        handleThievery(b, ((PriceTag.DisruptAction)action).getItem(), true);
                    }
                }
            }

            public void equipped(NHBot b, Item i) {
                if(b.isPlayer()) {
                    if(i.hasFragment(PriceTag.class)) {
                        PriceTag tag = (PriceTag) i.getFragment(PriceTag.NAME);
                        if(!tag.isDiscounted()) {
                            if(Rand.d100(50)&&Rand.d100(b.getModifiedPresence())) {
                                int off = Rand.om.nextInt(5)+1;
                                int mult = Rand.om.nextInt(2)+1;
                                int discount = (5*Rand.om.nextInt(5)+1) *
                                    Rand.om.nextInt(2)+1;
                                N.narrative().print(in.b, Grammar.start(in.b, "say")+" \"Hey, you like that? It's "+discount+"% off!\"");
                                tag.setDiscount(discount);
                            }
                            else {
                                // don't allow multiple tries for same item
                                tag.setDiscount(0);
                            }
                        }
                    }
                }
            }
        });

        e.addMatrixListener(new MatrixListener() {
            public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
            }

            public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
            }

            public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
                if(in.b==null||in.b.isDead()) {
                    return;
                }
                boolean wall = false;
                boolean floor = false;
                boolean rfid = false;
                for(MSpace s:spaces) {
                    if(s instanceof Wall) {
                        wall = true;
                    }
                    else if(s instanceof ShopFloor) {
                        floor = true;
                    }
                    else if(s instanceof RFIDTransponder) {
                        rfid = true;
                    }
                }
                if(wall) {
                    N.narrative().print(in.b, Grammar.start(in.b, "yell")+" \"Hey, my favorite wall!\"");
                }
                else if(floor) {
                    N.narrative().print(in.b, Grammar.start(in.b, "yell")+" \"Hey, my floor!\"");
                }
                else if(rfid) {
                    N.narrative().print(in.b, Grammar.start(in.b, "yell")+" \"Hey, that was expensive!\"");
                }
                if((wall||floor||rfid)&&Actor.current()!=null) {
                    in.b.setThreat((NHBot)Actor.current(), Threat.kos);
                }
            }
        });

        e.addContainerListener(new ContainerAdapter() {
            public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
                if(in.b!=null&&!in.b.isDead()&&adder.isPlayer()) {
                    if(space instanceof ShopFloor) {
                        if(item.hasFragment(PriceTag.class)) {
                            return;
                        }
                        float coef = 0.5f+0.1f*adder.getModifiedPresence()/20f;
                        PriceTag p = new PriceTag(item);
                        p.setAmount((int)(p.getAmount()*coef));
                        if(!item.isClassIdentified()) {
                            p.setAmount(p.getAmount()/2);
                        }
                        if(p.getAmount()==0) {
                            p.setAmount(1);
                        }
                        if(adder.isInvisible()||adder.isDead()||N.narrative().confirm(adder, Grammar.start(in.b, "offer")+" you "+p.getAmount()+" gold for "+Grammar.nounp(item)+". Pay?")) {
                            Gold g = new Gold();
                            g.setCount(p.getAmount());
                            adder.getInventory().add(g);
                            p.setAmount(2*p.getAmount());
                            item.addFragment(p);
                            if(adder.isInvisible()||adder.isDead()) {
                                N.narrative().print(in.b, "Ah, extra inventory!");
                            }
                            else {
                                N.narrative().print(in.b, "Thanks!");
                            }
                        }
                        //N.narrative().clear();
                    }
                    else {
                        handleThievery(adder, item, false);
                    }
                }
            }
        });

    }

    private boolean handleThievery(NHBot b, Item i, boolean assume) {
        //System.err.println("ASSUME: "+assume+" PLAYER: "+b.isPlayer());
        if(b.isPlayer()) { // too complex otherwise right now
            if(assume||i.hasFragment(PriceTag.class)) {
                N.narrative().print(in.b, Grammar.start(in.b, "yell")+" \"Stop! Thief!\"");
                in.b.setThreat(b, Threat.kos);
                for(Bot v:in.b.getEnvironment().getVisibleBots()) {
                    if(((NHBot)v).threat(b)==Threat.familiar) {
                        // don't let pets stand in the way of apprehending a thief
                        in.b.setThreat(((NHBot)v), Threat.none);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void handle(MSpace from, MSpace to, NHBot b) {
        if(in.b!=null&&in.b.isDead()) {
            return;
        }
        if(b.isInvisible()) {
            return;
        }
        if(to instanceof Doorway) {
            if(from instanceof ShopFloor) {
                for(Item i:b.getInventory().getItem()) {
                    if(handleThievery(b, i, false)) {
                        return;
                    }
                }
            }
            if(in.b.threat(b)!=Threat.kos) {
                if(from instanceof ShopFloor) {
                    if(Rand.om.nextBoolean()) {
                        N.narrative().print(in.b, "\"Goodbye!\"");
                    }
                    else {
                        N.narrative().print(in.b, "\"Come again!\"");
                    }
                }
                else {
                    String name = b.getName();
                    if(name==null) {
                        name = b.getCommon();
                    }
                    N.narrative().print(in.b, "\"Welcome, "+name+"!\"");
                }
            }
        }
    }

    @Override public void perform(final Context c) {
        if(_move!=null) {
            ((NPC)in.b).approach(_move, 999);
        }
    }

    public Chemical getChemical() {
        return _nop;
    }
}

