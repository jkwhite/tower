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
import org.excelsi.matrix.*;


public class EverythingAdapter implements NHEnvironmentListener, ContainerListener, MatrixListener, NHSpaceListener, OverlayListener, MechanicsListener {
    public void overlayMoved(Overlay o, NHSpace from, NHSpace to) {
    }

    public void overlayRemoved(Overlay o) {
    }

    public void occupied(MSpace s, Bot b) {
    }

    public void unoccupied(MSpace s, Bot b) {
    }

    public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
    }

    public void overlayAdded(NHSpace n, Overlay o) {
    }

    public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
    }

    public void overlayRemoved(NHSpace n, Overlay o) {
    }

    public void parasiteAdded(NHSpace s, Parasite p) {
    }

    public void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue) {
    }

    public void parasiteRemoved(NHSpace s, Parasite p) {
    }

    public void parasiteMoved(NHSpace s, NHSpace to, Parasite p) {
    }

    public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
    }

    public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
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

    public void modifierAdded(NHBot b, Modifier m) {
    }

    public void modifierRemoved(NHBot b, Modifier m) {
    }

    public void equipped(NHBot b, Item i) {
    }

    public void unequipped(NHBot b, Item i) {
    }

    public void itemModified(NHBot b, Item i) {
    }

    public void faced(Bot b, Direction old, Direction d) {
    }

    public void moved(Bot b, MSpace from, MSpace to) {
    }

    public void forgot(Bot b, List<MSpace> s) {
    }

    public void discovered(Bot b, List<MSpace> s) {
    }

    public void seen(Bot b, List<MSpace> s) {
    }

    public void obscured(Bot b, List<MSpace> s) {
    }

    public void noticed(Bot b, List<Bot> bots) {
    }

    public void missed(Bot b, List<Bot> bots) {
    }

    public void died(Bot b, MSource s) {
    }

    public void collided(Bot active, Bot passive) {
    }

    public void attacked(NHBot b, Outcome outcome) {
    }

    public void attacked(NHBot b, NHBot attacked) {
    }

    public void attackedBy(NHBot b, NHBot attacker) {
    }

    public void actionStarted(NHBot b, ProgressiveAction action) {
    }

    public void actionStopped(NHBot b, ProgressiveAction action) {
    }

    public void actionPerformed(NHBot b, InstantaneousAction action) {
    }

    public void attributeChanged(Bot b, String attribute, Object newValue) {
    }

    public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
    }

    public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
    }

    public void itemDropped(Container space, Item item, int idx, boolean incremented) {
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented) {
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
    }

    public void itemTaken(Container space, Item item, int idx) {
    }

    public void itemDestroyed(Container space, Item item, int idx) {
    }

    public void itemsDestroyed(Container space, Item[] item) {
    }
}
