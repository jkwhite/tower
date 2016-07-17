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
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * POV is an event relayer that restricts propagated events
 * to those within the purvey of a given bot. In other words
 * it provides an event system from the bot's point of view.
 */
public class POV extends EventRelayer {
    private NHBot _b;
    private EventSource _e;
    private Set<NHSpace> _visibleSpaces;
    private Set _visibleBots;


    public POV(NHBot b, EventSource e) {
        _b = b;
        _e = e;
        _visibleSpaces = _b.getEnvironment().getVisible();
        _visibleBots = _b.getEnvironment().getVisibleBots();
        e.addContainerListener(this);
        e.addNHSpaceListener(this);
        e.addNHEnvironmentListener(this);
        e.addMatrixListener(this);
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        if(_visibleSpaces.contains(space)) {
            super.itemAdded(space, item, idx, incremented);
        }
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        if(_visibleSpaces.contains(space)) {
            super.itemAdded(space, item, idx, incremented, adder, origin);
        }
    }

    public void itemTaken(Container space, Item item, int idx) {
        if(_visibleSpaces.contains(space)) {
            super.itemTaken(space, item, idx);
        }
    }

    public void itemDestroyed(Container space, Item item, int idx) {
        if(_visibleSpaces.contains(space)) {
            super.itemDestroyed(space, item, idx);
        }
    }

    public void itemsDestroyed(Container space, Item[] items) {
        if(_visibleSpaces.contains(space)) {
            super.itemsDestroyed(space, items);
        }
    }

    public void actionStarted(NHBot b, ProgressiveAction action) {
        _visibleBots = _b.getEnvironment().getVisibleBots();
        if(_visibleBots.contains(b)) {
            super.actionStarted(b, action);
        }
    }

    public void actionStopped(NHBot b, ProgressiveAction action) {
        if(_visibleBots.contains(b)) {
            super.actionStopped(b, action);
        }
    }

    public void occupied(MSpace s, Bot b) {
        if(_visibleSpaces.contains(s)) {
            super.occupied(s, b);
        }
    }

    public void unoccupied(MSpace s, Bot b) {
        if(_visibleSpaces.contains(s)) {
            super.unoccupied(s, b);
        }
    }

    public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
        if(_visibleSpaces.contains(from)||_visibleSpaces.contains(to)) {
            super.moved(source, from, to, b);
        }
    }

    public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
        //System.err.println("0: "+_b+": GOT REMOVE: "+Arrays.asList(spaces));
        //internalSpacesAdded(m, spaces);
        List<MSpace> ss = new ArrayList<MSpace>(spaces.length);
        for(MSpace s:spaces) {
            if(_visibleSpaces.contains(s)) {
                ss.add(s);
            }
        }
        if(ss.size()>0) {
            MSpace[] sp = (MSpace[]) ss.toArray(new MSpace[ss.size()]);
            for(MatrixListener l:_mlisteners) {
                l.spacesAdded(m, sp, b);
            }
        }
    }

    public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
        //System.err.println("0: "+_b+": GOT REMOVE: "+Arrays.asList(spaces));
        //internalSpacesRemoved(m, spaces);
        List<MSpace> ss = new ArrayList<MSpace>(spaces.length);
        for(MSpace s:spaces) {
            if(_visibleSpaces.contains(s)) {
                ss.add(s);
            }
        }
        if(ss.size()>0) {
            MSpace[] sp = (MSpace[]) ss.toArray(new MSpace[ss.size()]);
            for(MatrixListener l:_mlisteners) {
                l.spacesRemoved(m, sp, b);
            }
        }
    }

    public void attacked(NHBot b, NHBot attacked) {
        if(_visibleBots.contains(b) || _visibleBots.contains(attacked)) {
            super.attacked(b, attacked);
        }
    }
}
