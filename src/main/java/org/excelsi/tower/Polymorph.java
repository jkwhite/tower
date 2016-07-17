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


public class Polymorph extends Transformative {
    static {
        Basis.claim(new Basis(Basis.Type.disks, 1));
    }

    public boolean inflict(NHSpace s) {
        if(s.numItems()>0) {
            List<Item> news = new ArrayList<Item>(s.numItems());
            for(Item i:s.getItem()) {
                final Item.StackType st = i.getStackType();
                int count = i.getCount();
                s.destroy(i);
                Item p = Universe.getUniverse().createItem(new ItemFilter() {
                    public boolean accept(Item i, NHBot b) {
                        return i.getStackType()==st;
                    }
                });
                p.setCount(count);
                news.add(p);
            }
            for(Item i:news) {
                s.add(i);
            }
        }
        return false;
    }

    public boolean inflict(final NHBot b) {
        final String common;
        if(getStatus()==Status.blessed&&b.isPlayer()) {
            String sel = N.narrative().reply(b, "What would you like to polymorph into?");
            if(sel.startsWith("a ")) {
                sel = sel.substring("a ".length());
            }
            else if(sel.startsWith("an ")) {
                sel = sel.substring("an ".length());
            }
            common = sel;
        }
        else {
            common = null;
        }
        try {
            NHBot to = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                public boolean accept(NHBot bot) {
                    return !bot.isPlayer()&&(common==null||bot.getCommon().equals(common));
                }

                public String toString() {
                    return common;
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
                N.narrative().print(b, Grammar.start(b)+" "+Grammar.conjugate(b, "suddenly find")+" "+Grammar.pronoun(b)+" "+Grammar.toBe(b)+" "+name+". How odd.");
            }
            else {
                N.narrative().print(b, "The "+oldCommon+" "+Grammar.conjugate(b, "transform")+" into "+name+"!");
            }
            if(getStatus()==Status.uncursed) {
                b.addAffliction(new TemporalAffliction("polymorph", Affliction.Onset.tick, Rand.om.nextInt(100)+300) {
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
                        b.addAffliction(this);
                        //b.setModel(oldModel);
                        //b.setForm(oldForm);
                        //b.setAirborn(oldAirborn);
                        //b.setCommon(oldCommon);
                        N.narrative().print(b, Grammar.start(b, "change")+" back into "+Grammar.nonspecific(b)+".");
                    }

                    public String getStatus() {
                        return null;
                    }

                    public String getExcuse() {
                        return "polymorphed into a "+getBot().getCommon();
                    }
                });
            }
        }
        catch(NoSuchBotException e) {
            N.narrative().print(b, Grammar.start(b, "tingle")+" all over for a second.");
        }
        catch(IllegalArgumentException e) {
            N.narrative().print(b, Grammar.start(b, "tingle")+" all over for a second.");
        }
        return false;
    }

    public int getOccurrence() {
        return 20;
    }
}
