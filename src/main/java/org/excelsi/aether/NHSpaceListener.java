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


public interface NHSpaceListener extends org.excelsi.matrix.MSpaceListener {
    void overlayAdded(NHSpace s, Overlay o);
    void overlayRemoved(NHSpace s, Overlay o);
    void parasiteAdded(NHSpace s, Parasite p);
    void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue);
    void parasiteRemoved(NHSpace s, Parasite p);
    void parasiteMoved(NHSpace s, NHSpace to, Parasite p);
    void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue);
}
