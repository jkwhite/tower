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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class BoltAttack implements Attack {
    private NHBot _a;
    private Source _s;
    private Armament _weapon;


    public BoltAttack(Armament weapon, NHBot attacker, Source s) {
        _weapon = weapon;
        _a = attacker;
        _s = s;
    }

    public Source getSource() {
        return _s;
    }

    public NHBot getAttacker() { return _a; }

    public Type getType() {
        return Type.bolt;
    }

    public boolean isPhysical() {
        return false;
    }

    public Armament getWeapon() {
        return _weapon;
    }

    public int getRadius() {
        return 1;
    }

    public boolean affectsAttacker() {
        return false;
    }
}
