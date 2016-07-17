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


public class QuiverAction extends ItemAction {
    public QuiverAction(Item i) {
        super("quiver", i);
    }

    public QuiverAction() {
        super("quiver", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                return i==null || i instanceof Missile;
            }
        }, true);
    }

    public String getDescription() {
        return "Ready a missile for use.";
    }

    public String toString() {
        return "Quiver";
    }

    protected void act() {
        try {
            String message;
            if(getItem()==null) {
                message = Grammar.start(getBot(), "empty")+" "+Grammar.possessive(getBot())+" quiver.";
            }
            else {
                message = Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), "ready")
                    +" "+Grammar.nonspecific(getItem())+".";
            }
            getBot().setQuivered(getItem(), message);
        }
        catch(EquipFailedException e) {
            N.narrative().print(getBot(), e.getMessage());
            throw new ActionCancelledException();
        }
    }
}
