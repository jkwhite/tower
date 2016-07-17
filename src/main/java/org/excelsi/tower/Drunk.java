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


public class Drunk extends TemporalAffliction {
    public static final String NAME = "Drunk";
    private boolean _over = false;

    public Drunk() {
        super(NAME, Affliction.Onset.move, Rand.om.nextInt(30)+20);
    }

    public Drunk(int time) {
        super(NAME, Affliction.Onset.move, time);
    }

    protected void afflict() {
        getBot().setConfused(Rand.d100(60));
        if(getBot().isPlayer()&&getBot().isConfused()&&Rand.d100(10)) {
            String phrase = null;
            switch(Rand.om.nextInt(2)) {
                case 0:
                    phrase = "Someone keeps moving the floor!";
                    break;
                case 1:
                    phrase = "The world is spinning...";
                    break;
            }
            N.narrative().print(getBot(), phrase);
        }
        _over = checkOverdose(getBot(), getName(), _over, getRemaining());
    }

    public static boolean checkOverdose(NHBot b, String name, boolean over, int remaining) {
        if(!over) {
            int mc = b.getModifiedConstitution();
            if(remaining>3*mc) {
                b.die("Overdosed on "+name+".");
            }
            else if(remaining>2*mc) {
                over = true;
                N.narrative().print(b, Grammar.start(b, "overdose")+".");
                N.narrative().print(b, Grammar.start(b, "start")+" convulsing.");
                if(Rand.d100(50)) {
                    b.addAffliction(new Confused());
                }
                else {
                    b.start(new FaintingAction(b, false, 40));
                }
            }
        }
        else if(remaining<=b.getModifiedConstitution()) {
            over = false;
        }
        return over;
    }

    protected void finish() {
        if(getBot().isPlayer()) {
            N.narrative().print(getBot(), Grammar.start(getBot(), "feel")+" steadier.");
        }
        else {
            N.narrative().print(getBot(), Grammar.start(getBot(), "look")+" steadier.");
        }
        getBot().setConfused(false);
    }

    public String getStatus() {
        return "Drunk";
    }

    public String getExcuse() {
        return "drunk";
    }
}
