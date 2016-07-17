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


public class Blindness extends Transformative {
    private String _affliction;


    static {
        Basis.claim(new Basis(Basis.Type.wands, 3));
    }

    public Blindness() {
        this("blind");
    }

    public Blindness(String affliction) {
        _affliction = affliction;
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(!b.isAfflictedBy(_affliction)) {
            b.setBlind(1+b.getBlind());
            if(b.getBlind()==1) {
                if(b.isPlayer()) {
                    N.narrative().print(b, "The world is shrouded in darkness!");
                }
                else {
                    N.narrative().print(b, Grammar.start(b, "stumble")+" awkwardly.");
                }
            }
        }
        b.addAffliction(new Blind(_affliction, Rand.om.nextInt(40)+40));
        /*
        b.addAffliction(new TemporalAffliction(_affliction, Affliction.Onset.tick, Rand.om.nextInt(40)+40) {
            protected void afflict() {
            }

            protected void finish() {
                getBot().setBlind(getBot().getBlind()-1);
                if(getBot().isPlayer()&&!getBot().isBlind()) {
                    N.narrative().print(getBot(), "You can see again.");
                    getBot().getEnvironment().unhide();
                }
            }

            public String getStatus() {
                return "Blind";
            }

            public String getExcuse() {
                return "blinded";
            }
        });
        */
        return true;
    }
}
