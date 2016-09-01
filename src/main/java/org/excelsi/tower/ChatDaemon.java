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
import static org.excelsi.aether.Brain.*;
import java.util.logging.Logger;


public class ChatDaemon extends Daemon {
    private Chemical _basic;
    private int _frequency = 4;
    private String[] _vocals;


    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public void setVocalizations(String[] vocals) {
        _vocals = vocals;
    }

    public String[] getVocalizations() {
        return _vocals;
    }

    public int getFrequency() {
        return _frequency;
    }

    public void setFrequency(int frequency) {
        _frequency = frequency;
    }

    public void poll(final Context c) {
        strength = -1;
        if(in.important!=null&&(in.b.threat(in.important)!=Threat.kos)) {
            if(Rand.d100(_frequency)) {
                String v = _vocals[Rand.om.nextInt(_vocals.length)];
                N.narrative().print(in.b, "\""+v+"\"");
            }
        }
    }

    @Override public void perform(final Context c) {
    }

    public Chemical getChemical() {
        return _basic;
    }
}
