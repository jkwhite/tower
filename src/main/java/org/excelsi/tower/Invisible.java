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


public class Invisible extends TemporalAffliction {
    public static final String NAME = "Invisible";


    public Invisible() {
        this(Rand.om.nextInt(100)+54);
    }

    public Invisible(String name) {
        super(name, Affliction.Onset.tick, Integer.MAX_VALUE);
    }

    public Invisible(int time) {
        super(NAME, Affliction.Onset.tick, time);
    }

    public void setBot(NHBot b) {
        super.setBot(b);
        getBot().setInvisible(getBot().getInvisible()+1);
    }

    protected void afflict() {
    }

    protected void finish() {
        getBot().setInvisible(getBot().getInvisible()-1);
    }

    public String getStatus() {
        return NAME;
    }

    public String getExcuse() {
        return "invisible";
    }
}
