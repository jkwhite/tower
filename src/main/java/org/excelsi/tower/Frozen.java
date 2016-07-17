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


public class Frozen implements ProgressiveAction {
    private int _rem = Rand.om.nextInt(9)+4;
    private NHBot _b;
    private int _sub;
    private String _color;


    public Frozen(NHBot b) {
        this(b, false);
    }

    public Frozen(NHBot b, boolean changeColor) {
        _b = b;
        _sub = _b.getQuickness()/2;
        _b.setQuickness(_b.getQuickness()-_sub);
        if(changeColor) {
            _color = b.getColor();
            b.setColor("frozen");
        }
    }

    public int getInterruptRate() {
        return 0;
    }

    public boolean iterate() {
        if(--_rem==0) {
            return false;
        }
        return true;
    }

    public void stopped() {
        if(!_b.isDead()) {
            N.narrative().print(_b, Grammar.start(_b)+" can move again.");
        }
        _b.setQuickness(_b.getQuickness()+_sub);
        if(_color!=null) {
            _b.setColor(_color);
        }
    }

    public void interrupted() {
    }

    public String getExcuse() {
        return "frozen";
    }
}

