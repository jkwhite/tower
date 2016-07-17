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


import org.excelsi.matrix.MSpaceAdapter;


public class NHSpaceAdapter extends MSpaceAdapter implements NHSpaceListener {
    public void overlayAdded(NHSpace s, Overlay o) {
    }

    public void overlayRemoved(NHSpace s, Overlay o) {
    }

    public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
    }

    public void parasiteAdded(NHSpace s, Parasite p) {
    }

    public void parasiteRemoved(NHSpace s, Parasite p) {
    }

    public void parasiteMoved(NHSpace s, NHSpace to, Parasite p) {
    }

    public void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue) {
    }
}
