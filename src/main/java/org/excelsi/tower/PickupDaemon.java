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


public class PickupDaemon extends Daemon {
    private Chemical _basic;
    private Item _item;


    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public Chemical getChemical() {
        return _basic;
    }

    public int getChance() {
        return 10;
    }

    public void poll() {
        strength = -1;
        _item = null;
        if(in.attack==null&&!in.b.isLevitating()&&!in.b.isAirborn()) {
            NHSpace s = in.b.getEnvironment().getMSpace();
            if(s.numItems()>0) {
                if(Rand.d100(getChance())) {
                    Item[] its = s.getItem();
                    Arrays.sort(its, new Comparator<Item>() {
                        public boolean equals(Object o) { return false; }
                        public int compare(Item i1, Item i2) {
                            return (int) (i2.getWeight()-i1.getWeight());
                        }
                    });
                    for(Item i:its) {
                        if(accept(s, i)) {
                            _item = i;
                            break;
                        }
                    }
                    if(_item!=null) {
                        strength = 5;
                    }
                }
            }
        }
    }

    public void run() {
        NHSpace s = in.b.getEnvironment().getMSpace();
        if(s.contains(_item)) {
            DefaultNHBot.Pickup p = new DefaultNHBot.Pickup();
            p.setItem(_item);
            p.setBot(in.b);
            p.perform();
        }
    }

    protected Item getItem() {
        return _item;
    }

    protected boolean accept(NHSpace s, Item i) {
        switch(EncumbranceMixin.Degree.degreeFor(in.b, i, null)) {
            case unencumbered:
            case burdened:
                return true;
            default:
                return false;
        }
    }
}
