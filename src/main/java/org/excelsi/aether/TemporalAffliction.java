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


import java.util.logging.Logger;


public abstract class TemporalAffliction extends Affliction {
    private int _time;


    public TemporalAffliction(String name, Onset onset) {
        this(name, onset, Rand.om.nextInt(20)+10);
    }

    public TemporalAffliction(String name, Onset onset, int time) {
        super(name, onset);
        if(time<=0) {
            throw new IllegalArgumentException("time must be > 0");
        }
        _time = time;
    }

    public final int getRemaining() {
        return _time;
    }

    public final void beset() {
        Logger.global.fine("besetting affliction "+getName()+" on "+getBot());
        afflict();
    }

    public final void tick() {
        if(--_time==0) {
            Logger.global.fine("removing affliction "+getName()+" from "+getBot());
            finish();
            getBot().removeAffliction(this);
        }
    }

    public void compound(Affliction a) {
        Logger.global.fine("compounding affliction "+getName()+" on "+getBot());
        _time += ((TemporalAffliction)a).getRemaining();
    }

    protected final void setRemaining(int remaining) {
        _time = remaining;
    }

    abstract protected void afflict();
    abstract protected void finish();

    public String toString() {
        return super.toString()+" @ "+getRemaining();
    }
}
