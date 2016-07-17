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


public class Wield extends ItemAction {
    public Wield(Item i) {
        super("wield", i);
    }

    public Wield() {
        super("wield", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                return i==null || (i.getSlotType()==SlotType.hand && !i.isEquipped());
            }
        }, true);
    }

    public String getDescription() {
        return "Wield a weapon.";
    }

    protected void act() {
        try {
            getBot().setWielded(getItem(), getItem()!=null?
                Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), "wield")
                    +" "+Grammar.nonspecific(getItem())+".":
                Grammar.startToBe(getBot())+" empty-handed.");
        }
        catch(EquipFailedException e) {
            N.narrative().print(getBot(), e.getMessage());
            throw new ActionCancelledException();
        }
    }
}
