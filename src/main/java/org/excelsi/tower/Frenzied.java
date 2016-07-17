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


public class Frenzied extends TemporalAffliction {
    public static final String NAME = "Frenzy";
    private Modifier _m;
    private float _hpMult;


    public Frenzied(Modifier m, float hpMultiplier) {
        super(NAME, Affliction.Onset.tick, Rand.om.nextInt(30)+10);
        _m = m;
        _hpMult = hpMultiplier;
    }

    public Frenzied(Modifier m, float hpMultiplier, int time) {
        super(NAME, Affliction.Onset.tick, time);
        _m = m;
        _hpMult  = hpMultiplier;
    }

    public void setBot(NHBot b) {
        super.setBot(b);
        b.addModifier(_m);
        b.setMaxHp((int)(_hpMult*b.getMaxHp()));
        b.setHp((int)(_hpMult*b.getHp()));
    }

    protected void afflict() {
    }

    protected void finish() {
        getBot().removeModifier(_m);
        //getBot().setStrength(getBot().getStrength()/2);
        getBot().setHp((int)(getBot().getHp()/_hpMult));
        getBot().setMaxHp((int)(getBot().getMaxHp()/_hpMult));
        if(getBot().isPlayer()) {
            N.narrative().print(getBot(), "The rage subsides.");
        }
        else {
            N.narrative().print(getBot(), Grammar.start(getBot(), "calm")+" down.");
        }
    }

    public String getStatus() {
        return "Frenzy";
    }

    public String getExcuse() {
        return "frenzied";
    }
}
