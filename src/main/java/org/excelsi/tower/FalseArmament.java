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


public class FalseArmament implements Armament {
    private Infliction _inf;
    private String _skill;
    private Stat[] _stats;
    private String _color = "red";

    public FalseArmament(Infliction i, String skill, Stat[] stats) {
        _inf = i;
        _skill = skill;
        _stats = stats;
        if(i instanceof Fermionic) {
            _color = ((Fermionic)i).getColor();
        }
    }

    public int getPower() { return 0; }
    public int getModifiedPower() { return 0; }
    public int getRate() { return 100; }
    public int getHp() { return 0; }
    public void setHp(int hp) { }
    public String getVerb() { return "hit"; }
    public String getAudio() { return null; }
    public String getSkill() { return _skill; }
    public Attack invoke(NHBot attacker, NHBot defender, Attack a) { _inf.inflict(defender); return null; }
    public void invoke(NHBot attacker, NHSpace s, Attack a) { _inf.inflict(s); }
    public Item toItem() { return null; }
    public String getColor() { return _color; }
    public String getModel() { return "-"; }
    public Type getType() { return Type.missile; }
    public Stat[] getStats() { return _stats; }
}
