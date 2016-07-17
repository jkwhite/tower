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
import org.excelsi.matrix.Direction;


public class ExpensiveCamera extends Tool implements Chargeable, Createable {
    private int _charges;


    public ExpensiveCamera() {
        _charges = 12;
    }

    public Item[] getIngredients() {
        return new Item[]{new Flashbulb(), new Battery(), new Lens(), new PieceOfPlastic()};
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public float getShininess() {
        return 4f;
    }

    public int getFindRate() {
        return 8;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public Maneuver getDifficulty() {
        return Maneuver.hard;
    }

    public void setCharges(int charges) {
        _charges = 3*charges;
    }

    public int getCharges() {
        return _charges;
    }

    public float getSize() {
        return 0.3f;
    }

    public float getWeight() {
        return 0.1f;
    }

    public void use(NHBot b) {
        Direction d = N.narrative().direct(b, "Which direction?");
        flash(b, d);
    }

    public boolean discharge(NHBot b, Container c) {
        return false;
    }

    public void flash(NHBot b, Direction d) {
        b.getEnvironment().face(d);
        if(_charges>0) {
            _charges--;
            N.narrative().print(b, "FLASH!!");
        }
        else {
            N.narrative().print(b, "Nothing happens.");
            return;
        }
        NHSpace s = (NHSpace) b.getEnvironment().getMSpace().move(d, true);
        NHBot o = (NHBot) s.getOccupant();
        if(getStatus()==Status.cursed) {
            o = b;
        }
        if(o!=null) {
            if(o.getForm().hasSlot(SlotType.eyes) && !o.isBlind()) {
                new Blindness("camera flash").inflict(o);
                if(o.isPlayer()) {
                    N.narrative().print(b, "Oops! You were holding the camera backward!");
                }
                else {
                    N.narrative().print(b, Grammar.start(o, "look")+" dazed.");
                }
            }
        }
    }
}
