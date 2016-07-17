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


public class Wear extends ItemAction {
    public Wear(Item i) {
        super("wear", i);
    }

    public Wear() {
        super("wear", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                //return i.getSlotType()!=SlotType.hand && !bot.isEquipped(i);
                return (i instanceof Armor||(i instanceof Armament&&i.getSlotType()!=SlotType.hand&&i.getSlotType()!=SlotType.none)) && !bot.isEquipped(i);
            }
        });
    }

    public String getDescription() {
        return "Put on a piece of clothing or armor.";
    }

    protected void act() {
        try {
            if(getBot().isPlayer()&&getItem().getSlotType()!=SlotType.hand) {
                Slot[] slots = getBot().getForm().getSlots(getItem().getSlotType());
                if(slots.length>0&&slots[0].getOccupant()!=null) {
                    if(N.narrative().confirm(getBot(), "Remove "+Grammar.pronoun(getBot(), slots[0].getOccupant())+"?")) {
                        Takeoff t = new Takeoff();
                        t.setBot(getBot());
                        t.setItem(slots[0].getOccupant());
                        t.perform();
                    }
                }
            }
            getBot().wear(getItem(),
                Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), "wear")
                    +" "+Grammar.nonspecific(getItem())+".");
        }
        catch(EquipFailedException e) {
            N.narrative().print(getBot(), e.getMessage());
            throw new ActionCancelledException();
        }
    }
}
