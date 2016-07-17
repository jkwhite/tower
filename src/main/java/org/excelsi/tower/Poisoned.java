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
import java.util.ArrayList;
import java.util.List;


public class Poisoned extends TemporalAffliction {
    public static final String NAME = "Poison";
    private List<Poisons> _types = new ArrayList<Poisons>(1);
    private String _source;


    public Poisoned(Poisons type) {
        this(type, Rand.om.nextInt(10)+10);
    }

    public Poisoned(Poisons type, int time) {
        this(type, time, null);
    }

    public Poisoned(Poisons type, int time, String source) {
        super(NAME, Affliction.Onset.tick, time==-1?Rand.om.nextInt(10)+10:time);
        _types.add(type);
        _source = source;
    }

    protected void afflict() {
        Stat[] stats = null;
        NHBot b = getBot();
        for(Poisons type:_types) {
            switch(type) {
                case nervous:
                    stats = new Stat[]{Stat.re, Stat.ag, Stat.qu};
                    break;
                case circulatory:
                    stats = new Stat[]{Stat.st, Stat.co};
                    break;
                case respiratory:
                    stats = new Stat[]{Stat.st, Stat.co};
                    break;
                case luck:
                    if(Rand.d100(10)) {
                        if(getBot().isPlayer()) {
                            N.narrative().print(getBot(), "Your luck has run out.");
                            N.narrative().more();
                        }
                        getBot().die("Killed by poison");
                    }
                    break;
            }
            if(stats!=null) {
                for(Stat s:stats) {
                    if(Rand.d100(50)) {
                        getBot().setStat(s, Math.max(0, getBot().getStat(s)-1));
                    }
                    if(s==Stat.co&&getBot().getStat(Stat.co)==0) {
                        getBot().die("Killed by poison");
                    }
                }
            }
            int dmg = (int) Math.max(1f, b.getHp()*Rand.d100()/1000f);
            b.setHp(Math.max(0, b.getHp()-dmg));
            if(b.getHp()==0&&!b.isDead()) {
                N.narrative().print(b, "The poison overtakes "+Grammar.noun(b)+".");
                b.removeAffliction(NAME);
                if(_source==null) {
                    b.die("Killed by poison");
                }
                else {
                    b.die("Killed by "+_source);
                }
            }
            if(b.isDead()) {
                break;
            }
        }
    }

    public void compound(Affliction a) {
        _types.addAll(((Poisoned)a)._types);
        setRemaining(getRemaining()+Math.max(1,6-_types.size()));
    }

    protected void finish() {
        getNarrative().print(getBot(), Grammar.start(getBot())+" "+Grammar.toBe(getBot())+" no longer poisoned.");
    }

    public String getStatus() {
        return "Poisoned";
    }

    public String getExcuse() {
        return "poisoned";
    }
}
