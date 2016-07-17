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


public class FaintingAction implements ProgressiveAction {
    private int _time;
    private boolean _fromHunger;
    private NHBot _b;


    public FaintingAction(NHBot b) {
        this(b, true, Rand.om.nextInt(5)+1);
    }

    public FaintingAction(NHBot b, boolean fromHunger, int time) {
        _b = b;
        _fromHunger = fromHunger;
        _time = time;
    }

    public int getInterruptRate() {
        return 0;
    }

    public boolean iterate() {
        if(--_time<0||(_fromHunger&&Hunger.Degree.degreeFor(getBot().getHunger())!=Hunger.Degree.fainting)) {
            N.narrative().print(getBot(), Grammar.start(getBot(), "regain")+" consciousness.");
            return false;
        }
        return true;
    }

    public void stopped() {
    }

    public void interrupted() {
    }

    public String getExcuse() {
        return "fainted";
    }

    private NHBot getBot() {
        return _b;
    }
}
