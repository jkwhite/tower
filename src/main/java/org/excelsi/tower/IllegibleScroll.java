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


public class IllegibleScroll extends Scroll {
    public Item[] getIngredients() {
        return null;
    }

    public String getName() {
        return "illegible scroll";
    }

    public int getFindRate() {
        return 15;
    }

    public void invoke(final NHBot b) {
        if(b.isPlayer()) {
            N.narrative().printfm(b, "This scroll is hard to make out, but you're pretty sure you've got it.");
        }
        b.getInventory().consume(this);
        BookOfSands.randomInfliction(b);
    }
}
