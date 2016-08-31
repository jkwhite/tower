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


public class SurvivalDaemon extends Daemon {
    boolean _act = false;
    private Chemical _flight;
    private int _factor = 7;


    public void setFactor(int factor) {
        _factor = factor;
    }

    public int getFactor() {
        return _factor;
    }

    public void init(java.util.Map<String,Chemical> chems) {
        _flight = chems.get("flight");
    }

    public String getChemicalSpec() {
        return "flight";
    }

    public void poll() {
        if(in.attack==null) {
            //if("debug".equals(in.b.getName())) {
                //System.err.println("ACT: "+_act+", hp="+in.b.getHp()+"/"+in.b.getMaxHp()+", flight="+_flight.isActive());
            //}
            if((in.b.getHp()<in.b.getMaxHp()/_factor||(in.b.getMaxHp()>2&&in.b.getHp()<=2))&&in.important!=null&&in.b.threat(in.important)==Threat.kos) {
                if(!_flight.isActive()) {
                    _flight.activate();
                    _act = true;
                }
            }
            else if(in.b.getHp()>in.b.getMaxHp()/5&&_act) {
                _flight.deactivate();
                _act = false;
            }
        }
        // this daemon never fires, only serves to
        // activate other flight daemons.
        strength=-1;
    }

    @Override public void perform(final Context c) {
    }

    public Chemical getChemical() {
        return _flight;
    }
}
