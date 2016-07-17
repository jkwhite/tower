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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class Ice extends Floor {
    private static final long serialVersionUID = 1L;
    private Water _w;
    private long _liquify;


    public Ice() {
    }

    public Ice(Water w, int time) {
        _w = w;
        _liquify = Time.now()+time;
    }

    public void update() {
        super.update();
        if(_w!=null) {
            long t = Time.now();
            if(t>=_liquify) {
                N.narrative().print(this, "The ice melts!");
                liquify();
            }
            else if(t==_liquify-5) {
                N.narrative().print(this, "Cracks dart and finger along the ice.");
            }
        }
    }

    public void liquify() {
        replace(_w);
    }

    public String getModel() {
        return "~";
    }

    public String getColor() {
        return "frozen";
    }
}
