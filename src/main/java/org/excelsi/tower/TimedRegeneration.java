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


import java.util.List;
import java.util.ArrayList;
import org.excelsi.aether.*;
import org.excelsi.matrix.MSpace;


public class TimedRegeneration extends Regeneration {
    private int _exp;


    public TimedRegeneration(int time, String name, String excuse, int period, int amount) {
        super(name, excuse, period, amount);
        _exp = time;
    }

    public void beset() {
        if(--_exp<=0) {
            getBot().removeAffliction(this);
        }
        else {
            if(getAmount()>0&&getBot().getHp()==getBot().getMaxHp()) {
                int amt = 1;
                getBot().setMaxHp(getBot().getMaxHp()+amt);
                getBot().setHp(getBot().getHp()+amt);
            }
            else {
                getBot().setHp(Math.max(getBot().getMaxHp(), getBot().getHp()+getAmount()));
            }
        }
    }

    public String getStatus() {
        return "Regen";
    }
}
