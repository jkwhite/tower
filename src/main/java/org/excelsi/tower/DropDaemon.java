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


public class DropDaemon extends Daemon {
    private Chemical _basic;


    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public Chemical getChemical() {
        return _basic;
    }

    public void poll() {
        strength = -1;
        if(in.attack==null) {
            if(in.b.getInventory().numItems()>0&&Rand.d100(10)) {
                strength = 9;
            }
        }
    }

    public void run() {
        Item[] its = in.b.getInventory().getItem();
        DefaultNHBot.Drop d = new DefaultNHBot.Drop();
        d.setItem(its[Rand.om.nextInt(its.length)]);
        d.setBot(in.b);
        d.perform();
    }

    protected boolean accept(Item i) {
        return true;
    }
}
