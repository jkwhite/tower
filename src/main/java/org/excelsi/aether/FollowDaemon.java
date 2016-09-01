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


import static org.excelsi.aether.Brain.*;
import org.excelsi.matrix.Filter;
import org.excelsi.matrix.MSpace;


public class FollowDaemon extends Daemon implements Filter {
    private Chemical _basic;


    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public boolean accept(MSpace m) {
        return true;
    }

    public void poll(final Context c) {
        if(in.important!=null&&in.b.threat(in.important)==Threat.friendly) {
            //strength = 1;
            strength = Rand.om.nextInt(2);
        }
        else {
            strength = -1;
        }
    }

    @Override public void perform(final Context c) {
        if(in.b.getEnvironment().getMSpace().distance(in.important.getEnvironment().getMSpace())>4) {
            ((NPC)in.b).approach(in.important, 6, false, this);
        }
    }

    public Chemical getChemical() {
        return _basic;
    }
}
