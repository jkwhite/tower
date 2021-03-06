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


@FunctionalInterface
public interface ItemFilter extends java.io.Serializable {
    boolean accept(Item item, NHBot bot);

    public static ItemFilter named(final String name) {
        return (i,b)->{ return name.equals(i.getName()); };
    }

    public static ItemFilter cat(final String category) {
        return (i,b)->{ return category.equals(i.getCategory()); };
    }

    public static ItemFilter randomCategory(final ItemFactory f) {
        return (i,b)->{ return f.randomCategory().equals(i.getCategory()); };
    }
}
