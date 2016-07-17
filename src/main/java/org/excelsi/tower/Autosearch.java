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


public class Autosearch extends Affliction {
    private Search _s = new Search();
    public Autosearch() {
        super("autosearch", Affliction.Onset.tick);
    }

    public void setBot(NHBot b) {
        super.setBot(b);
        _s.setBot(getBot());
    }

    public void beset() {
        _s.perform();
    }

    public String getStatus() {
        return null;
    }

    public String getExcuse() {
        return null;
    }

    public void compound(Affliction a) {
    }
}
