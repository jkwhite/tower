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
import org.excelsi.matrix.*;


public class Reading implements ProgressiveAction {
    private Readable _r;
    private NHBot _b;
    private int _t;


    public Reading(NHBot b, Readable r) {
        _b = b;
        _r = r;
        _t = 30;
    }

    public int getInterruptRate() { return 100; }

    public boolean iterate() {
        if(--_t==0) {
            _r.read(_b);
            return false;
        }
        return true;
    }

    public void interrupted() {
        N.narrative().printf(_b, Grammar.start(_b, "stop")+" reading.");
    }

    public void stopped() {
    }

    public String getExcuse() { return "reading"; }
}
