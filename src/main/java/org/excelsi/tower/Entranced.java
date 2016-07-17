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


public class Entranced implements ProgressiveAction {
    private int _rem = Rand.om.nextInt(9)+4;
    private NHBot _b;
    private Item _i;
    private boolean _intr;


    public Entranced(NHBot b, Item i) {
        _b = b;
        _i = i;
    }

    public Item getItem() {
        return _i;
    }

    public int getInterruptRate() {
        return 10;
    }

    public boolean iterate() {
        if(--_rem==0) {
            return false;
        }
        return true;
    }

    public void stopped() {
        if(!_intr) {
            N.narrative().print(_b, Grammar.start(_b, "put")+" "+Grammar.noun(_i)+" away.");
        }
    }

    public void interrupted() {
        //N.narrative().print(_b, Grammar.start(_b, "put")+" "+Grammar.noun(_i)+" away.");
        DefaultNHBot.Drop d = new DefaultNHBot.Drop();
        d.setItem(_i);
        d.setBot(_b);
        d.perform();
        _intr = true;
    }

    public String getExcuse() {
        return "entranced by "+Grammar.nonspecific(_i);
    }
}
