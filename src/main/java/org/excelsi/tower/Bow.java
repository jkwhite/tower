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


import org.excelsi.aether.Stat;


public abstract class Bow extends Weapon2H {
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.ag, Stat.ag};
    public final String getSkill() {
        return BOWS;
    }

    public float getLevelWeight() { return 0.1f; }

    public Type getType() {
        return Type.missile;
    }

    public Stat[] getStats() {
        return STATS;
    }

    public String getColor() {
        return "dark-brown";
    }
}
