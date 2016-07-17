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


public class ScrollOfMerging extends Scroll {
    public int score() { return 180; }

    public void invoke(final NHBot b) {
        int time = 50+Rand.om.nextInt(50);
        switch(getStatus()) {
            case blessed:
                time *= 2;
            case uncursed:
                N.narrative().printf(b, "%V from view!", b, "fade");
                b.addAffliction(new Invisible(time));
                break;
            case cursed:
                break;
        }
    }
}
