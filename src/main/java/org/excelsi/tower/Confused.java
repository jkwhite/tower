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


public class Confused extends TemporalAffliction {
    public static final String NAME = "Confused";

    public Confused() {
        super(NAME, Affliction.Onset.move, Rand.om.nextInt(30)+20);
    }

    public Confused(int time) {
        super(NAME, Affliction.Onset.move, time);
    }

    protected void afflict() {
        getBot().setConfused(true);
    }

    protected void finish() {
        N.narrative().print(getBot(), Grammar.start(getBot())+" "+Grammar.toBe(getBot())+" no longer confused.");
        getBot().setConfused(false);
    }

    public String getStatus() {
        return NAME;
    }

    public String getExcuse() {
        return "confused";
    }
}
