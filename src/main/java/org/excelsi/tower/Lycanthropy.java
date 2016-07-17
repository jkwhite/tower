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
import java.util.List;
import java.util.ArrayList;


public class Lycanthropy extends Affliction {
    public static final String NAME = "lycanthropy";
    private boolean _beset = false;
    private int _timer = 0;
    private String _beast;


    public Lycanthropy(String beast) {
        super(NAME, Onset.tick);
        _beast = beast;
    }

    public void compound(Affliction a) {
    }

    public String getStatus() {
        //return "Lycanthrope";
        return null;
    }

    public void beset() {
        if(_timer==0&&!_beset&&Rand.d100(5)&&Rand.d100(10)) {
            _timer = 5;
            if(getBot().isPlayer()) {
                N.narrative().print(getBot(), Grammar.first(Grammar.possessive(getBot()))+" senses are on edge!");
            }
        }
        if(_timer>0) {
            if(--_timer==0) {
                _beset = true;
                afflict();
            }
        }
    }

    public String getExcuse() {
        return null;
    }

    public void afflict() {
        final NHBot b = getBot();
        try {
            NHBot to = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                public boolean accept(NHBot bot) {
                    return !bot.isPlayer()&&bot.getCommon().equals(_beast);
                }

                public String toString() {
                    return _beast;
                }
            });
            final String oldCommon = b.getCommon();
            //final NHBot oldCopy = DefaultNHBot.copy(b);
            final int[] oldStats = b.getStats();
            final String oldModel = b.getModel();
            final Form oldForm = b.getForm();
            final boolean oldAirborn = b.isAirborn();
            b.polymorph(to);
            String name = to.getProfession();
            if(name==null) {
                name = to.getCommon();
            }
            name = Grammar.nonspecific(name);
            if(to.isUnique()) {
                // shouldn't happen, but anyway
                name = to.getCommon();
            }
            if(b.isPlayer()) {
                N.narrative().print(b, Grammar.start(b, "undergo")+" a frightening transformation!");
                N.narrative().more();
                N.narrative().print(b, Grammar.start(b, "turn")+" into "+name+".");
            }
            else {
                N.narrative().print(b, "The "+oldCommon+" "+Grammar.conjugate(b, "transform")+" into "+name+"!");
            }
            b.addAffliction(new TemporalAffliction("lycanthropy", Affliction.Onset.tick, Rand.om.nextInt(100)+300) {
                protected void afflict() {
                }

                protected void finish() {
                    NHBot old = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                        public boolean accept(NHBot bot) {
                            return bot.getCommon().equals(oldCommon);
                        }
                    });
                    b.polymorph(old);
                    b.setStats(oldStats);
                    b.addAffliction(this); // will be removed immediately in DefaultNHBot
                    if(b.isPlayer()) {
                        N.narrative().print(b, Grammar.start(b, "feel")+" like yourself again.");
                    }
                    else {
                        N.narrative().print(b, Grammar.start(b)+" looks better.");
                    }
                    _beset = false;
                }

                public String getStatus() {
                    return null;
                }

                public String getExcuse() {
                    return "in "+getBot().getCommon()+" form";
                }
            });
        }
        catch(IllegalArgumentException e) {
            N.narrative().print(b, Grammar.start(b, "tingle")+" all over for a second.");
        }
    }

    public int getOccurrence() {
        return 20;
    }
}
