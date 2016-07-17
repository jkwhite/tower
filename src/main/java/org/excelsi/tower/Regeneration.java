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


public class Regeneration extends Affliction {
    private int _time = 0;
    private int _period = 20;
    private int _amount = 1;
    private String _excuse;


    public Regeneration(String name, String excuse) {
        this(name, excuse, 20);
    }

    public Regeneration(String name, String excuse, int period) {
        this(name, excuse, 20, 1);
    }

    public Regeneration(String name, String excuse, int period, int amount) {
        super(name, Affliction.Onset.tick);
        _excuse = excuse;
        _period = period;
        _amount = amount;
    }

    public void setAmount(int amt) {
        _amount = amt;
    }

    public int getAmount() {
        return _amount;
    }

    public void setPeriod(int per) {
        _period = per;
    }

    public void beset() {
        if(++_time==_period) {
            NHBot m = getBot();
            m.setHp(Math.max(0,Math.min(m.getMaxHp(),m.getHp()+_amount)));
            _time = 0;
        }
    }

    public String getStatus() {
        return null;
    }

    public String getExcuse() {
        return _excuse;
    }

    public void compound(Affliction a) {
        _time += ((Regeneration)a)._time;
    }

    public boolean equals(Object o) {
        if(super.equals(o)) {
            Regeneration r = (Regeneration) o;
            return _period==r._period&&_amount==r._amount;
        }
        return false;
    }
}
