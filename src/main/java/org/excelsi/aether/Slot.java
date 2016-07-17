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


public final class Slot implements java.io.Serializable {
    private Item _item;
    private String _name;
    private SlotType _type;
    private int _hitPercentage;


    public Slot(SlotType type, String name, int hitPercentage) {
        _type = type;
        _name = name.intern();
        _hitPercentage = hitPercentage;
    }

    public String getName() {
        return _name;
    }

    public int getHitPercentage() {
        int hit = _hitPercentage;
        if(_item!=null) {
            hit += _item.getSlotModifier();
        }
        return hit;
    }

    public Item getOccupant() {
        return _item;
    }

    public boolean isOccupied() {
        return _item!=null;
    }

    public void vacate() {
        if(_item==null) {
            throw new IllegalStateException("not holding anything in "+_name);
        }
        _item = null;
    }

    public void occupy(Item i) {
        if(_item!=null) {
            throw new IllegalStateException("can't put "+i+" on "+_name+": already holding "+_item);
        }
        if(_type.equals(i.getSlotType())) {
            _item = i;
        }
        else {
            throw new IllegalArgumentException("can't put "+i+" on "+_name);
        }
    }

    public SlotType getSlotType() {
        return _type;
    }
}
