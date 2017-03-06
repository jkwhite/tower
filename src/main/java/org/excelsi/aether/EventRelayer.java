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


import java.util.List;
import java.util.ArrayList;
import org.excelsi.matrix.*;
import java.util.Arrays;


public class EventRelayer extends EverythingAdapter implements EventSource {
    private List<ContainerListener> _clisteners = new ArrayList<ContainerListener>();
    private List<NHEnvironmentListener> _nhelisteners = new ArrayList<NHEnvironmentListener>();
    private List<NHSpaceListener> _nhslisteners = new ArrayList<NHSpaceListener>();
    protected List<MatrixListener> _mlisteners = new ArrayList<MatrixListener>();


    public void addNHEnvironmentListener(NHEnvironmentListener l) {
        _nhelisteners.add(l);
    }

    public void addNHSpaceListener(NHSpaceListener l) {
        _nhslisteners.add(l);
    }

    public void addContainerListener(ContainerListener l) {
        _clisteners.add(l);
    }

    public void addMatrixListener(MatrixListener l) {
        _mlisteners.add(l);
    }

    public void removeNHEnvironmentListener(NHEnvironmentListener l) {
        _nhelisteners.remove(l);
    }

    public void removeNHSpaceListener(NHSpaceListener l) {
        _nhslisteners.remove(l);
    }

    public void removeContainerListener(ContainerListener l) {
        _clisteners.remove(l);
    }

    public void removeMatrixListener(MatrixListener l) {
        _mlisteners.remove(l);
    }

    public void overlayMoved(Overlay o, NHSpace from, NHSpace to) {
    }

    public void overlayRemoved(Overlay o) {
    }

    public void occupied(MSpace s, Bot b) {
        for(NHSpaceListener l:_nhslisteners) {
            l.occupied(s, b);
        }
    }

    public void unoccupied(MSpace s, Bot b) {
        for(NHSpaceListener l:_nhslisteners) {
            l.unoccupied(s, b);
        }
    }

    public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
        for(NHSpaceListener l:_nhslisteners) {
            l.moved(source, from, to, b);
        }
    }

    public void parasiteAdded(NHSpace s, Parasite p) {
        for(NHSpaceListener l:_nhslisteners) {
            l.parasiteAdded(s, p);
        }
    }

    public void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue) {
        for(NHSpaceListener l:_nhslisteners) {
            l.parasiteAttributeChanged(s, p, attr, oldValue, newValue);
        }
    }

    public void parasiteRemoved(NHSpace s, Parasite p) {
        for(NHSpaceListener l:_nhslisteners) {
            l.parasiteRemoved(s, p);
        }
    }

    public void parasiteMoved(NHSpace s, NHSpace to, Parasite p) {
        for(NHSpaceListener l:_nhslisteners) {
            l.parasiteMoved(s, to, p);
        }
    }

    public void overlayAdded(NHSpace n, Overlay o) {
    }

    public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
    }

    public void overlayRemoved(NHSpace n, Overlay o) {
    }

    public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
        //System.err.println("GOT 2 REMOVE: "+Arrays.asList(spaces));
        internalSpacesRemoved(m, spaces);
        for(MatrixListener l:_mlisteners) {
            l.spacesRemoved(m, spaces, b);
        }
    }

    protected void internalSpacesRemoved(Matrix m, MSpace[] spaces) {
        for(MSpace ms:spaces) {
            NHSpace n = (NHSpace) ms;
            n.removeContainerListener(this);
            n.removeMSpaceListener(this);
        }
    }

    public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
        internalSpacesAdded(m, spaces);
        for(MatrixListener l:_mlisteners) {
            l.spacesAdded(m, spaces, b);
        }
    }

    protected void internalSpacesAdded(Matrix m, MSpace[] spaces) {
        for(MSpace ms:spaces) {
            NHSpace n = (NHSpace) ms;
            n.addContainerListener(this);
            n.addMSpaceListener(this);
        }
    }

    public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
    }

    public void ascended(Game g) {
    }

    public void descended(Game g) {
    }

    public void afflicted(NHBot b, Affliction a) {
    }

    public void cured(NHBot b, Affliction a) {
    }

    public void faced(Bot b, Direction old, Direction d) {
        for(NHEnvironmentListener e:_nhelisteners) {
            e.faced(b, old, d);
        }
    }

    public void moved(Bot b) {
    }

    public void forgot(Bot b, List<MSpace> s) {
        //System.err.println("FORGOT");
        for(NHEnvironmentListener e:_nhelisteners) {
            e.forgot(b, s);
        }
    }

    public void discovered(Bot b, List<MSpace> s) {
        //System.err.println("DISCOVERED");
        for(NHEnvironmentListener e:_nhelisteners) {
            e.discovered(b, s);
        }
    }

    public void seen(Bot b, List<MSpace> s) {
        //System.err.println("SEEN");
        for(NHEnvironmentListener e:_nhelisteners) {
            e.seen(b, s);
        }
    }

    public void obscured(Bot b, List<MSpace> s) {
        //System.err.println("OBSCURED");
        for(NHEnvironmentListener e:_nhelisteners) {
            e.obscured(b, s);
        }
    }

    public void noticed(Bot b, List<Bot> bots) {
        //System.err.println("NOTICED");
        for(NHEnvironmentListener e:_nhelisteners) {
            e.noticed(b, bots);
        }
    }

    public void missed(Bot b, List<Bot> bots) {
        //System.err.println("MISSED");
        for(NHEnvironmentListener e:_nhelisteners) {
            e.missed(b, bots);
        }
    }

    public void died(Bot b, MSource s) {
        for(NHEnvironmentListener n:_nhelisteners) {
            n.died(b, s);
        }
    }

    public void collided(Bot active, Bot passive) {
    }

    public void attacked(NHBot b, Outcome outcome) {
    }

    public void attacked(NHBot b, NHBot attacked) {
        for(NHEnvironmentListener n:_nhelisteners) {
            n.attacked(b, attacked);
        }
    }

    public void attackedBy(NHBot b, NHBot attacker) {
    }

    public void actionStarted(NHBot b, ProgressiveAction action) {
        for(NHEnvironmentListener n:_nhelisteners) {
            n.actionStarted(b, action);
        }
    }

    public void actionStopped(NHBot b, ProgressiveAction action) {
        for(NHEnvironmentListener n:_nhelisteners) {
            n.actionStopped(b, action);
        }
    }

    public void attributeChanged(Bot b, String attribute, Object newValue) {
        //UNSTABLE
        for(NHEnvironmentListener n:_nhelisteners) {
            n.attributeChanged(b, attribute, newValue);
        }
    }

    public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
    }

    public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
    }

    public void itemDropped(Container space, Item item, int idx, boolean incremented) {
        for(ContainerListener c:_clisteners) {
            c.itemDropped(space, item, idx, incremented);
        }
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        for(ContainerListener c:_clisteners) {
            c.itemAdded(space, item, idx, incremented);
        }
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        for(ContainerListener c:_clisteners) {
            c.itemAdded(space, item, idx, incremented, adder, origin);
        }
    }

    public void itemTaken(Container space, Item item, int idx) {
        for(ContainerListener c:_clisteners) {
            c.itemTaken(space, item, idx);
        }
    }

    public void itemDestroyed(Container space, Item item, int idx) {
        for(ContainerListener c:_clisteners) {
            c.itemDestroyed(space, item, idx);
        }
    }

    public void itemsDestroyed(Container space, Item[] items) {
        for(ContainerListener c:_clisteners) {
            c.itemsDestroyed(space, items);
        }
    }
}
