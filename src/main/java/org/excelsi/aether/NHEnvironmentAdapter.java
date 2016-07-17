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


public class NHEnvironmentAdapter extends org.excelsi.matrix.EnvironmentAdapter implements NHEnvironmentListener {
    public void afflicted(NHBot b, Affliction a) {
    }

    public void cured(NHBot b, Affliction a) {
    }

    public void modifierAdded(NHBot b, Modifier m) {
    }

    public void modifierRemoved(NHBot b, Modifier m) {
    }

    public void attacked(NHBot b, NHBot attacked) {
    }

    public void attackedBy(NHBot b, NHBot attacker) {
    }

    public void attacked(NHBot b, Outcome outcome) {
    }

    public void actionStarted(NHBot b, ProgressiveAction action) {
    }

    public void actionStopped(NHBot b, ProgressiveAction action) {
    }

    public void actionPerformed(NHBot b, InstantaneousAction action) {
    }

    public void equipped(NHBot b, Item i) {
    }

    public void unequipped(NHBot b, Item i) {
    }

    public void itemModified(NHBot b, Item i) {
    }
}
