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
import static org.excelsi.aether.Grammar.*;
import org.excelsi.matrix.Direction;


public class Lyophilizer extends Destructible implements Device, Chargeable {
    private Dehydratable _item;
    private Pill _storage;
    private int _charges = Rand.om.nextInt(3)+3;
    private long _start;


    public Lyophilizer() {
    }

    public String getColor() {
        return "white";
    }

    public String getModel() {
        return "a#";
    }

    public int getHeight() {
        return 4;
    }

    public boolean isMoveable() {
        return true;
    }

    public Item[] getIngredients() {
        Item[] it = new Item[]{new Battery(), new ScrapMetal(), new Condenser()};
        it[1].setCount(2);
        return it;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public String getCreationSkill() {
        return "gadgetry";
    }

    public Maneuver getDifficulty() {
        return Maneuver.hard;
    }

    public boolean notice(NHBot b) {
        N.narrative().print(getSpace(), "There is a lyophilizer here.");
        return true;
    }

    public void trigger(NHBot b) {
    }

    public String getName() {
        return "lyophilizer";
    }

    public boolean discharge(NHBot b, Container c) {
        return false;
    }

    public void use(NHBot b) {
        if(_storage!=null) {
            N.narrative().print(b, "This lyophilizer is already in use.");
            throw new ActionCancelledException();
        }
        boolean fr = false, frm = false;
        for(Item i:b.getInventory().getItem()) {
            if(i instanceof Dehydratable) {
                fr = true;
            }
            if(i instanceof Pill && ((Pill)i).isEmpty()) {
                frm = true;
            }
        }
        if(!fr) {
            N.narrative().print(b, Grammar.start(b, "have")+" nothing that can be freeze-dried.");
            throw new ActionCancelledException();
        }
        if(!frm) {
            N.narrative().print(b, Grammar.start(b, "have")+" no means to store freeze-dried substances.");
            throw new ActionCancelledException();
        }
        if(_charges==0) {
            N.narrative().print(b, "This lyophilizer needs more juice!");
            throw new ActionCancelledException();
        }
        for(;;) {
            Dehydratable chosen = (Dehydratable) N.narrative().choose(b, new ItemConstraints(
                b.getInventory(), "freeze-dry", new InstanceofFilter(Dehydratable.class)), false);
            Pill p = (Pill) N.narrative().choose(b, new ItemConstraints(
                b.getInventory(), "use for storage", new ItemFilter() {
                    public boolean accept(Item i, NHBot b) { return i instanceof Pill && ((Pill)i).isEmpty(); }
                }), false);
            chosen = (Dehydratable) b.getInventory().split((Item)chosen);
            p = (Pill) b.getInventory().split(p);
            lyophilize(chosen, p);
            N.narrative().print(b, "You place the items into the lyophilizer.");
            break;
        }
    }

    public void setCharges(int charges) {
        _charges = charges;
    }

    public int getCharges() {
        return _charges;
    }

    public void lyophilize(Dehydratable chosen, Pill p) {
        _item = chosen;
        _storage = p;
        _start = Time.now();
    }

    public boolean pickup(NHBot b) {
        return false;
    }

    public void update() {
        if(_item!=null&&Time.now()>_start+9) {
            _item.dehydate(_storage);
            _item = null;
            N.narrative().print(getSpace(), "The lyophilizer shudders to a halt.");
            getSpace().add(_storage);
            _storage = null;
            _charges--;
            if(_charges==0) {
                N.narrative().print(getSpace(), "It's eerily silent in here.");
            }
        }
    }
}
