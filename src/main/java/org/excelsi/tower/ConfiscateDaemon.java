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
import static org.excelsi.aether.Brain.*;
import java.util.Arrays;
import java.util.Comparator;


public class ConfiscateDaemon extends PickupDaemon {
    @Override public void perform(final Context c) {
        super.perform(c);
        if(getItem()!=null) {
            if(in.b.getInventory().contains(getItem())) {
                in.b.getInventory().destroy(getItem());
            }
        }
    }

    public int getChance() {
        return 90;
    }

    protected boolean accept(NHSpace s, Item i) {
        return !s.isSpecial() && super.accept(s, i);
    }
}
