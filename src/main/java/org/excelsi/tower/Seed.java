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


public abstract class Seed extends Parasite {
    private long _creation = Time.now();
    private long _sprout = Rand.om.nextInt(40)+40;


    public boolean isMoveable() {
        return false;
    }

    public String getModel() {
        return "'";
    }

    public String getColor() {
        return "light-brown";
    }

    public void attacked(Armament a) {
    }

    public int getHeight() {
        return 0;
    }

    public boolean notice(NHBot b) {
        if(b.isPlayer()) {
            Context.c().n().print(getSpace(), "There is "+Grammar.nonspecific(getName())+" here.");
        }
        return true;
    }

    public void trigger(NHBot b) {
    }

    public void update() {
        long t = Time.now();
        if(t>_creation+_sprout) {
            getSpace().removeParasite(this);
            getSpace().addParasite(next());
        }
    }

    protected abstract Parasite next();

    protected abstract String getName();
}
