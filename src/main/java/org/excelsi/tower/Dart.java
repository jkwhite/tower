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


public class Dart extends Thrown implements Missile {
    public Dart() {
        setCount(Rand.om.nextInt(5)+1);
    }

    public float getLevelWeight() { return 0.1f; }

    public float getSize() {
        return 0.2f;
    }

    public float getWeight() {
        return 0.01f;
    }

    public int getPower() {
        return 4;
    }

    public int getRate() {
        return 70;
    }

    public StackType getStackType() {
        return StackType.stackable;
    }

    public Type getType() {
        return Type.missile;
    }

    public int getFindRate() {
        return 70;
    }

    public boolean matches(Armament launcher) {
        return launcher instanceof DartGun;
    }
}
