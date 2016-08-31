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


public class ComplainDaemon extends Daemon {
    private Chemical _flight;
    private int _deactivate = -1;


    public void init(java.util.Map<String,Chemical> chems) {
        _flight = chems.get("flight");
    }

    public String getChemicalSpec() {
        return "flight";
    }

    public Chemical getChemical() {
        return _flight;
    }

    public void poll() {
        strength = -1;
        if(in.attack==null) {
            NHSpace s = in.b.getEnvironment().getMSpace();
            if(s.numItems()>0) {
                for(Item i:s.getItem()) {
                    if(i.getStatus()==Status.cursed) {
                        //NARRATIVE
                        N.narrative().print(in.b, Grammar.start(in.b, in.b.getForm().getComplain())+".");
                        if(!_flight.isActive()) {
                            _flight.activate();
                            _deactivate = 2;
                        }
                    }
                }
            }
        }
        if(_deactivate>0) {
            if(--_deactivate==0) {
                _flight.deactivate();
            }
        }
    }

    @Override public void perform(final Context c) {
    }
}
