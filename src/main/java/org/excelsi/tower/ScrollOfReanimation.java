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


public class ScrollOfReanimation extends Scroll {
    public int score() { return 120; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Emerald()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        if(checkSacrifice(b)) {
            return;
        }
        MSpace[] sur = b.getEnvironment().getMSpace().surrounding();
        int j = Rand.om.nextInt(sur.length);
        for(int i=0;i<sur.length;i++) {
            if(sur[j]!=null&&!sur[j].isOccupied()) {
                for(Item it:((NHSpace)sur[j]).getItem()) {
                    if(it instanceof Corpse) {
                        NHBot lucky = ((Corpse)it).getSpirit();
                        if(lucky!=null) {
                            lucky.setDead(false);
                            lucky.setHp(lucky.getMaxHp());
                            sur[j].setOccupant(lucky);
                            ((NHSpace)sur[j]).consume(it);
                            postMod(b, lucky, false);
                            return;
                        }
                    }
                }
            }
            if(++j==sur.length) {
                j = 0;
            }
        }
        N.narrative().print(b, "The air shimmers for a moment.");
        b.getInventory().consume(this);
    }

    private static final String[] UNINTENDED = {"morl", "urki", "xuun", "murg"};

    public void sacrifice(NHBot bot, Altar a) {
        super.sacrifice(bot, a);
        int tot = 0;
        NHBot[] bots = Universe.getUniverse().getBots();
        List<String> commons = new ArrayList<String>(bots.length);
        for(NHBot b:bots) {
            commons.add(b.getCommon());
        }
        for(Item i:a.getItem()) {
            if(i instanceof Corpse) {
                Corpse c = (Corpse) i;
                a.destroy(c);
                if(c.getSpirit()!=null) {
                    String common = c.getSpirit().getCommon();
                    tot += c.getCount()*commons.indexOf(common);
                }
            }
        }
        NHBot creation = Universe.getUniverse().createBot(commons.get(tot%commons.size()));
        if(creation.isPlayer()) {
            creation = Universe.getUniverse().createBot(UNINTENDED[Rand.om.nextInt(UNINTENDED.length)]);
        }
        a.setOccupant(creation);
        if(creation.getSociality()==Sociality.pack) {
            int add = Rand.om.nextInt(3);
            MSpace[] sur = a.surrounding();
            int x = 0;
            for(int i=0;i<add;i++) {
                while(sur[x].isOccupied()) {
                    x++;
                }
                if(x<sur.length) {
                    NHBot p2 = Universe.getUniverse().createBot(creation.getCommon());
                    sur[x].setOccupant(p2);
                }
            }
        }

        postMod(bot, creation, true);
    }

    private void postMod(NHBot b, NHBot lucky, boolean create) {
        if(create) {
            N.narrative().print(b, Grammar.first(Grammar.nonspecific(lucky))+" flashes into existence!");
        }
        switch(getStatus()) {
            case cursed:
                if(Rand.om.nextBoolean()) {
                    N.narrative().print(b, Grammar.start(b, "sneeze")+" while reading the scroll! Something looks terribly wrong with "+Grammar.specific(lucky)+"...");
                }
                else {
                    N.narrative().print(b, Grammar.first(Grammar.possessive(b)+" unspeakable experiment goes horribly awry!"));
                }
                lucky.setThreat(b, Threat.kos);
                //lucky.setStrength(2*lucky.getStrength());
                //lucky.setMaxHp(2*lucky.getMaxHp());
                //lucky.setHp(2*lucky.getHp());
                lucky.addAffliction(new Frenzied(new Modifier(lucky.getStrength()), 2));
                break;
            case uncursed:
                if(create) {
                }
                else {
                    N.narrative().print(b, Grammar.start(lucky)+" suddenly "+Grammar.conjugate(lucky, "spring")+" to life!");
                }
                break;
            case blessed:
                if(create) {
                }
                else {
                    N.narrative().print(b, Grammar.start(lucky)+" suddenly "+Grammar.conjugate(lucky, "spring")+" to life!");
                }
                if(lucky.threat(b)!=Threat.familiar) {
                    N.narrative().print(b, Grammar.start(lucky, "look")+" friendly.");
                    lucky.setThreat(b, Threat.friendly);
                }
                break;
        }
    }
}
