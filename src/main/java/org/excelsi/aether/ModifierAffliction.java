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


import java.util.List;
import java.util.ArrayList;


public abstract class ModifierAffliction extends TemporalAffliction {
    private List<Modifier> _mods = new ArrayList<Modifier>(1);


    public ModifierAffliction(String name, Onset onset, Modifier m) {
        super(name, onset);
        _mods.add(m);
    }

    public ModifierAffliction(String name, Onset onset, int time, Modifier m) {
        super(name, onset, time);
        _mods.add(m);
    }

    public void setBot(NHBot b) {
        super.setBot(b);
        for(Modifier m:_mods) {
            b.addModifier(m);
        }
    }

    public List<Modifier> getModifiers() {
        return _mods;
    }

    public void compound(Affliction a) {
        super.compound(a);
        ModifierAffliction ma = (ModifierAffliction) a;
        for(Modifier m:ma.getModifiers()) {
            getBot().addModifier(m);
        }
        _mods.addAll(ma.getModifiers());
    }

    protected void finish() {
        for(Modifier m:_mods) {
            getBot().removeModifier(m);
        }
    }
}
