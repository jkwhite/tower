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


public class ContainerAdapter implements ContainerListener {
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

    public void itemsDestroyed(Container container, Item[] items) {
    }
}
