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


import org.excelsi.aether.ProgressiveAction;
import org.excelsi.aether.N;
import org.excelsi.aether.Grammar;
import org.excelsi.aether.Ground;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Rand;


public class Stun implements ProgressiveAction {
    private NHBot _b;
    private int _remaining;


    public Stun(NHBot b, int time) {
        _b = b;
        _remaining = time;
    }

    public int getInterruptRate() {
        return 0;
    }

    public boolean iterate() {
        if(--_remaining>0) {
            return true;
        }
        else {
            return false;
        }
    }

    public void stopped() {
        N.narrative().print(_b, Grammar.startToBe(_b)+" no longer stunned.");
    }

    public void interrupted() {
    }

    public String getExcuse() {
        return "stunned";
    }
}
