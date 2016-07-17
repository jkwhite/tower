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


public class Read extends InvokeAction {
    public Read(Scroll i) {
        super("read", i);
    }

    public Read(Book i) {
        super("read", i);
    }

    public Read() {
        super("read", new ItemFilter() {
             public boolean accept(Item i, NHBot b) {
                return i.getCategory().equals("scroll") || i.getCategory().equals("book");
             }
        });
    }

    public String getDescription() {
        return "Read a book or scroll.";
    }

    protected void act() {
        if(getBot().isBlind()) {
            N.narrative().print(getBot(), Grammar.start(getBot())+" can't see anything.");
            throw new ActionCancelledException();
        }
        else {
            super.act();
        }
    }
}
