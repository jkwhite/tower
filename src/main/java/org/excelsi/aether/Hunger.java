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


import org.excelsi.matrix.Bot;
import org.excelsi.aether.*;
import java.util.EnumSet;


public class Hunger extends Affliction implements Mixin {
    public static final int RATE = 400;

    public enum Degree {
        american("Popped", -2*RATE),
        satiated("Satiated", -RATE),
        normal(null, RATE),
        hungry("Hungry", 2*RATE),
        famished("Famished", 3*RATE),
        starving("Starving", 4*RATE),
        fainting("Fainting", (int) (4.9f*RATE)),
        dead("Dead", 5*RATE);

        private String _name;
        private int _threshold;

        Degree(String name, int threshold) {
            _name = name;
            _threshold = threshold;
        }

        public String toString() {
            return _name;
        }

        public static Degree degreeFor(int hunger) {
            for(Degree d:EnumSet.allOf(Degree.class)) {
                if(hunger<d._threshold) {
                    return d;
                }
            }
            return Degree.satiated;
        }
    };


    public Hunger() {
        super("hunger", Onset.tick);
    }

    public Hunger(NHBot b) {
        this();
        b.addAffliction(this);
        b.addListener(new NHEnvironmentAdapter() {
            public void attributeChanged(Bot b, String attribute, Object oldValue) {
                if("hunger".equals(attribute)) {
                    int oldh = ((Integer)oldValue).intValue();
                    int newh = getBot().getHunger();
                    boolean inc = newh>oldh;
                    Degree od = Degree.degreeFor(oldh);
                    Degree nd = Degree.degreeFor(newh);
                    if(od!=nd) {
                        if(nd==Hunger.Degree.dead) {
                            N.narrative().print(getBot(), Grammar.start(getBot(), "die")+" of starvation.");
                            getBot().die("Starved to death");
                            return;
                        }
                        else if(nd==Hunger.Degree.fainting) {
                            getBot().addAffliction(new Fainting());
                        }
                        else if(nd==Hunger.Degree.american) {
                            N.narrative().print(getBot(), "Pop! "+Grammar.start(getBot(), "explode")+" in a panoply of tasty vittles.");
                            getBot().die("Succumbed to gluttony");
                            return;
                        }
                        else {
                            if(getBot().isAfflictedBy(Fainting.NAME)) {
                                getBot().removeAffliction(Fainting.NAME);
                            }
                        }
                        if(inc) {
                            if(getBot().isPlayer()) {
                                if(nd.toString()!=null) {
                                    N.narrative().print(getBot(), "You are "+nd.toString().toLowerCase()+".");
                                }
                                else {
                                    N.narrative().print(getBot(), "You are no longer "+od.toString().toLowerCase()+".");
                                }
                            }
                        }
                        else {
                            if(getBot().isPlayer()) {
                                switch(od) {
                                    case normal:
                                        if(nd.toString()!=null) {
                                            N.narrative().print(getBot(), "You are "+nd.toString().toLowerCase()+".");
                                            break;
                                        }
                                    case starving:
                                    case famished:
                                    case hungry:
                                    case satiated:
                                        N.narrative().print(getBot(), "You are no longer "+od.toString().toLowerCase()+".");
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean match(Class c) {
        return NHBot.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        NHBot b = (NHBot) o;
        if(b.getForm().getSustenance()==EnergySource.comestibles) {
            new Hunger(b);
        }
    }

    public void beset() {
        int h = getBot().getHunger()+getBot().getModifiedHungerRate();
        getBot().setHunger(h);
    }
    
    public String getStatus() {
        return Degree.degreeFor(getBot().getHunger()).toString();
    }

    public String getExcuse() {
        return null;
    }

    public void compound(Affliction a) {
    }
}
