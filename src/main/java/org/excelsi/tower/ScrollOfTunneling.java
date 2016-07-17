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


public class ScrollOfTunneling extends Scroll {
    public int score() { return 100; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Amethyst()};
    }

    public void invoke(NHBot b) {
        super.invoke(b);
        if(!checkSacrifice(b)) {
            Tunneling t = new Tunneling();
            t.setStatus(getStatus());
            t.inflict(b);
        }
    }

    public void sacrifice(NHBot b, Altar a) {
        super.sacrifice(b, a);
        BreathType type = null;
        Element elem = null;
        int floor = 0;
        boolean foundTunnel = false;

        for(Item i:a.getItem()) {
            if(i instanceof Corpse) {
                NHBot bot = ((Corpse)i).getSpirit();
                if(bot!=null) {
                    if(bot instanceof Elemental) {
                        for(Element e:((Elemental)bot).getElements()) {
                            elem = e;
                            a.consume(i);
                            break;
                        }
                    }
                    if(elem==null) {
                        for(Brain.Daemon d:((NPC)bot).getAi().getDaemons()) {
                            if(d instanceof BreathDaemon) {
                                type = ((BreathDaemon)d).getType();
                                a.consume(i);
                                break;
                            }
                        }
                        if(type==null) {
                            for(int n=0;n<i.getCount();n++) {
                                floor = (floor+12+bot.getMinLevel())/2;
                            }
                        }
                    }
                }
            }
            else {
                for(Fragment f:i.getFragments()) {
                    if(f instanceof Tunneling) {
                        foundTunnel = true;
                        a.consume(i);
                        break;
                    }
                }
            }
        }

        if(foundTunnel) {
            Tunnel t = null;
            if(elem!=null) {
                switch(elem) {
                    case fire:
                        floor = 900;
                        break;
                    case water:
                        floor = 904;
                        break;
                    case light:
                        floor = 902;
                        break;
                    case earth:
                        // buried in rock
                        floor = 905;
                        break;
                    case plasma:
                        floor = 906;
                        break;
                }
            }
            if(type!=null) {
                switch(type) {
                    case fire:
                        floor = 900;
                        break;
                    case cold:
                        floor = 901;
                        break;
                    case lightning:
                        floor = 902;
                        break;
                    case acid:
                        floor = 903;
                        break;
                    case plasma:
                        floor = 906;
                        break;
                }
            }
            if(floor==b.getEnvironment().getLevel()) {
                ++floor;
            }
            t = new Tunnel(b.getEnvironment().getLevel(), floor);
            N.narrative().print(b, "A mysterious portal engulfs the altar!");
            a.replace(t);
        }
    }
}
