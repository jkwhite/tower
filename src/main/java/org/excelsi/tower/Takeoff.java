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


public class Takeoff extends ItemAction {
    public Takeoff(Item i) {
        super("take off", i);
    }

    public Takeoff() {
        super("take off", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                //return i.getSlotType()!=SlotType.hand && bot.isEquipped(i);
                //return i instanceof Armor && bot.isEquipped(i);
                return (i instanceof Armor||(i instanceof Armament&&i.getSlotType()!=SlotType.hand)) && bot.isEquipped(i);
            }
        });
    }

    public String getDescription() {
        return "Remove a piece of clothing or armor.";
    }

    protected void act() {
        try {
            getBot().takeOff(getItem());
            N.narrative().print(getBot(), Grammar.start(getBot(), "take")
                    +" off "+Grammar.possessive(getBot())+" "+getItem().getName()+".");
        }
        catch(EquipFailedException e) {
            N.narrative().print(getBot(), e.getMessage());
            throw new ActionCancelledException();
        }
    }
}
