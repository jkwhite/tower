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
import static org.excelsi.aether.Brain.*;


/**
 * A daemon that chases after dropped comestibles.
 */
public class ChaserDaemon extends Daemon {
    private EventSource _e;
    private NHSpace _dest;
    private Chemical _hunger;


    public ChaserDaemon() {
    }

    public void init(java.util.Map<String,Chemical> chems) {
        _hunger = chems.get("hunger");
    }

    public String getChemicalSpec() {
        return "hunger";
    }

    public void setEventSource(EventSource e) {
        _e = e;
        _e.addContainerListener(new ContainerAdapter(){
            public void itemDropped(Container space, Item item, int idx, boolean incremented) {
                checkItem(space, item);
            }

            public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder) {
                checkItem(space, item);
            }

            private void checkItem(Container space, Item item) {
                if(item instanceof Comestible) {
                    _dest = (NHSpace) space;
                    strength = 9;
                }
            }
        });
    }

    public Chemical getChemical() {
        return _hunger;
    }

    public void poll() {
        if(strength>-1) {
            strength--;
        }
    }

    @Override public void perform(final Context c) {
        if(_dest!=null) {
            if(_dest==in.b.getEnvironment().getMSpace()) {
                /*
                Consume c = new Consume();
                c.setBot(in.b);
                c.perform();
                */
                //BASIC_NEEDS.activate();
                strength = 0;
            }
            else {
                ((NPC)in.b).approach(_dest, 10);
            }
        }
    }
}
