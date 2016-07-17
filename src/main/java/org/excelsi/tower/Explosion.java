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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;


public class Explosion extends Item implements Armament {
    private String _color = "gray";
    private String _model = "+";
    private int _power = 5;
    private Infliction _f;
    private Item _i;
    public Explosion(Infliction f) {
        addFragment(f);
        _f = f;
        if(f instanceof Transformative) {
            _color = ((Transformative)f).getColor();
        }
    }

    public Explosion(Item i) {
        _i = i;
        _color = i.getColor();
        _model = i.getModel();
    }

    public int getPower() { return _power; }
    public int getModifiedPower() { return _power; }
    public int getRate() { return 1000; } // no skill
    public int getHp() { return 0; }
    public void setHp(int hp) { }
    public String getVerb() { return "hit"; }
    public String getSkill() { return null; }
    public Attack invoke(NHBot attacker, NHBot defender, Attack a) { if(_i!=null) _i.invoke(defender); else _f.inflict(defender); return null; }
    public void invoke(NHBot attacker, NHSpace s, Attack a) { }
    public Item toItem() { return null; }
    public String getColor() { return _color; }
    public String getModel() { return _model; }
    public Type getType() { return Type.missile; }
    public Stat[] getStats() { return null; }
    public float getWeight() { return 0f; }
    public float getSize() { return 0f; }
    public void invoke(NHBot b) {}
    public SlotType getSlotType() { return SlotType.none; }
    public String getCategory() { return null; }
}
