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


import org.excelsi.matrix.MSpace;


public class FollowPath implements ProgressiveAction {
    private final NHBot _b;
    private final MSpace _dest;
    private int _max = 20;


    public FollowPath(final NHBot b, final MSpace dest) {
        _b = b;
        _dest = dest;
    }

    @Override public boolean iterate() {
        ((DefaultNHBot)_b).approach((NHSpace)_dest, 20);
        return _max-->0 && !_b.getEnvironment().getMSpace().equals(_dest);
    }

    @Override public int getInterruptRate() {
        return 100;
    }

    @Override public void stopped() {
        //System.err.println("FOLLOW STPOPED");
        //Thread.dumpStack();
    }

    @Override public void interrupted() {
        //System.err.println("FOLLOW INTERRUPT");
    }

    @Override public String getExcuse() {
        return "walking along";
    }
}
