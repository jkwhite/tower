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


import org.excelsi.aether.NHBot;
import org.excelsi.aether.Item;
import org.excelsi.aether.Container;


public interface Soluble {
    /**
     * Dissolves this soluble into a solution.
     *
     * @return <code>true</code> if the solution was consumed
     */
    public boolean dissolve(NHBot b, Container c, Item solution, boolean deliberate, boolean all);
}
