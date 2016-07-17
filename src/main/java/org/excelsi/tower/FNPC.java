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


/**
 * FNPC provides customizable stat gain rates for the standard familiars.
 */
public class FNPC extends TNPC {
    private int _statGainRate;


    public int getStatGainRate() {
        return _statGainRate;
    }

    public void setStatGainRate(int rate) {
        _statGainRate = rate;
    }

    public void setMaxHp(int hp) {
        int cur = getMaxHp();
        super.setMaxHp(hp);
        boolean doit = false;
        if(cur<20&&hp>=20) {
            doit = true;
        }
        else if(cur<40&&hp>=40) {
            doit = true;
        }

        else if(cur<80&&hp>=80) {
            doit = true;
        }
        if(doit) {
            Size s = Size.small;
            if(hp>=80) {
                s = Size.huge;
            }
            else if(hp>=40) {
                s = Size.large;
            }
            else if(hp>=20) {
                s = Size.medium;
            }
            setSize(s);
        }
    }

    protected boolean canOccupyHoly() {
        return true;
    }
}
