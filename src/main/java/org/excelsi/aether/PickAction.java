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


import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Typed;
import org.excelsi.matrix.Direction;


public class PickAction extends DefaultNHBotAction {
    private final Typed _picked;


    public PickAction(Typed picked) {
        _picked = picked;
    }

    @Override public String getDescription() { return "Picks an object."; }

    public Typed getPicked() {
        return _picked;
    }

    @Override public void perform() {
        if(_picked instanceof NHSpace) {
            //((DefaultNHBot)getBot()).approach((NHSpace)_picked, 10);
            getBot().start(new FollowPath(getBot(), (NHSpace)_picked));
        }
    }
}
