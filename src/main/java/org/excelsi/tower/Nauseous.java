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


public class Nauseous extends TemporalAffliction {
    public static final String NAME = "Nauseous";

    public Nauseous() {
        super(NAME, Affliction.Onset.move, Rand.om.nextInt(30)+20);
    }

    public Nauseous(int time) {
        super(NAME, Affliction.Onset.move, time);
    }

    public void setBot(NHBot b) {
        super.setBot(b);
        if(getBot().isPlayer()) {
            N.narrative().print(getBot(), Grammar.start(getBot())+" don't feel so good.");
        }
        else {
            N.narrative().print(getBot(), Grammar.start(getBot(), "look")+" sick.");
        }
    }

    protected void afflict() {
        getBot().setConfused(true);
    }

    protected void finish() {
        getBot().getEnvironment().getMSpace().add(new Consume.Vomit());
        N.narrative().print(getBot(), Grammar.start(getBot(), "vomit")+".");
        getBot().setHunger(getBot().getHunger()+Hunger.RATE/2);
        N.narrative().print(getBot(), Grammar.start(getBot(), "feel")+" better.");
        getBot().setConfused(false);
    }

    public String getStatus() {
        return NAME;
    }

    public String getExcuse() {
        return "nauseous";
    }
}
