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


public class GenerateDaemon extends Daemon {
    private Chemical _basic;


    public GenerateDaemon() {
    }

    @Override public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    @Override public String getChemicalSpec() {
        return "basic";
    }

    @Override public Chemical getChemical() {
        return _basic;
    }

    @Override public void poll(final Context c) {
        if(in.attack!=null) {
            strength = -1;
            return;
        }
        else {
            strength = Rand.d100(10) ? 5 : 0;
        }
    }

    @Override public void perform(final Context c) {
        Item it = c.getUniverse().createItem((i,b)->{ return true; });
        System.err.println("****** ITEM: "+it);
        if(it==null) {
            it = new AngelArm();
        }
        in.b.getEnvironment().getMSpace().add(it, in.b);
    }
}
