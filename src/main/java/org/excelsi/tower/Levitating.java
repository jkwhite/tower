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
import org.excelsi.matrix.MSpace;


/**
 * Levitation contains the effect of drinking a potion of levitation.
 * There should be at most one Levitation affliction per bot at a time.
 */
public class Levitating extends TemporalAffliction {
    public static final String NAME = "levitating";

    private int _time = Rand.om.nextInt(40)+40;


    public Levitating() {
        this(Rand.om.nextInt(40)+40);
    }

    public Levitating(int time) {
        super(NAME, Onset.tick, time);
    }

    public void setBot(NHBot b) {
        super.setBot(b);
        b.setLevitating(true);
    }

    /* TODO
    public boolean allow(NHBotAction a) {
        if(a instanceof Patsy.Descend || a instanceof DefaultNHBot.Pickup) {
            N.narrative().print(getBot(), Grammar.start(getBot())+" cannot reach the ground.");
            return false;
        }
        return true;
    }
    */

    protected void afflict() {
        if(getBot().getEnvironment()!=null) {
            MSpace s = getBot().getEnvironment().getMSpace();
            if(s instanceof Stairs && ((Stairs)s).isAscending()) {
                Patsy.Ascend a = new Patsy.Ascend();
                a.setBot(getBot());
                a.perform();
            }
        }
        if(getRemaining()==3) {
            N.narrative().print(getBot(), Grammar.start(getBot(), "start")+" to bobble in the air.");
        }
    }

    protected void finish() {
        Levitation.endEffect(getBot());
    }

    public String getStatus() {
        return "Levitating";
    }

    public String getExcuse() {
        return "levitating";
    }

    public void compound(Affliction a) {
        //throw new IllegalStateException("levitation cannot be compounded");
    }
}
