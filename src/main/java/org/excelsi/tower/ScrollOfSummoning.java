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
import org.excelsi.matrix.MSpace;
import java.util.Map;


public class ScrollOfSummoning extends Scroll {
    public int score() { return 66; }

    public Item[] getIngredients() {
        return new Item[]{new BlankParchment(), new Ruby()};
    }

    public void invoke(final NHBot b) {
        super.invoke(b);
        if(checkSacrifice(b)) {
            return;
        }
        final int level;
        if(b.isPlayer()) {
            level = ((Patsy)b).getLevel().getFloor();
        }
        else {
            level = (b.getMinLevel()+b.getMaxLevel())/2;
        }
        NHBot s = null;
        if(getStatus()==Status.blessed) {
            final String sum = N.narrative().reply(b, "What do you want to summon?");
            try {
                s = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                    public boolean accept(NHBot b) {
                        return !b.isPlayer() && b.getCommon().equals(sum);
                    }
                });
            }
            catch(IllegalArgumentException e) {
                // summon a random if they don't choose
            }
            catch(NoSuchBotException e) {
                // summon a random if they don't choose
            }
            catch(ActionCancelledException e) {
                // summon a random if they don't choose
            }
        }
        else if(getStatus()==Status.cursed) {
            if(Rand.d100(1)) {
                s = Universe.getUniverse().createBot("black reaver");
            }
        }
        if(s==null) {
            s = Universe.getUniverse().createBot(new BotFactory.Constraints() {
                public boolean accept(NHBot b) {
                    return !b.isPlayer() && !b.isUnique() && b.getMinLevel()>0 && b.getMinLevel()<=9+level;
                }
            });
        }
        MSpace[] sur = b.getEnvironment().getMSpace().surrounding();
        int i = Rand.om.nextInt(sur.length), j = i;
        do {
            if(sur[j]!=null&&!sur[j].isOccupied()&&sur[j].isWalkable()) {
                sur[j].setOccupant(s);
                return;
            }
            if(++j==sur.length) {
                j = 0;
            }
        } while(j!=i);
        N.narrative().print(b, "A fog gathers and dissipates.");
    }

    public void sacrifice(NHBot b, Altar a) {
        int sk = b.getSkill("alchemy");
        //if(Rand.d100((sk+b.getModifiedIntuition())/2)) {
            super.sacrifice(b, a);
            NHBot chimera = null;
            int complexity = 0;
            boolean msg = false;
            for(Item i:a.getItem()) {
                if(i instanceof Corpse) {
                    if(!msg) {
                        N.narrative().print(b, "A bright light flashes from the altar!");
                        msg = true;
                    }
                    Corpse c = (Corpse) i;
                    a.destroy(c);
                    complexity += 2;
                    NHBot sp = c.getSpirit();
                    if(sp!=null) {
                        if(chimera==null) {
                            chimera = sp;
                            chimera.setDead(false);
                            chimera.setHp(sp.getMaxHp());
                            chimera.setMp(sp.getMaxMp());
                            chimera.getInventory().destroyAll();
                        }
                    }
                    else {
                        int[] sps = sp.getStats();
                        int[] stats = chimera.getStats();
                        for(int j=0;j<sps.length;j++) {
                            stats[j] = Math.max(stats[j], 2*sps[j]);
                        }
                        chimera.setStats(stats);
                        chimera.setHp(chimera.getMaxHp());
                        chimera.setMp(chimera.getMaxMp());
                        for(Map.Entry<String,Integer> e:sp.getSkills().entrySet()) {
                            int skill = chimera.getSkill(e.getKey());
                            if(skill<2*e.getValue()) {
                                chimera.setSkill(e.getKey(), 2*e.getValue());
                            }
                        }
                        for(Brain.Daemon da:((NPC)sp).getAi().getDaemons()) {
                            if(!((NPC)chimera).getAi().containsDaemon(da)) {
                                ((NPC)chimera).getAi().addDaemon(da);
                            }
                        }
                        if(sp.isAirborn()) {
                            chimera.setAirborn(true);
                        }
                    }
                }
            }
            if(chimera==null) {
                N.narrative().print(b, "Nothing happens.");
                return;
            }
            for(Item i:a.getItem()) {
                if(i instanceof Potion) {
                    a.destroy(i);
                    for(Fragment f:i.getFragments()) {
                        if(f instanceof Infliction) {
                            ++complexity;
                            ((Infliction)f).setPermanent(true);
                            ((Item)chimera.getForm().getNaturalWeapon()).addFragment(f);
                            if(f instanceof Fermionic) {
                                N.narrative().print(b, "The light flashes "+((Fermionic)f).getColor()+"!");
                            }
                        }
                    }
                }
            }
            chimera.setCommon("chimera");
            chimera.setModel("X");
            String[] colors = Universe.getUniverse().getPublicColormap().keySet().toArray(new String[0]);
            chimera.setColor(colors[Rand.om.nextInt(colors.length)]);
            a.setOccupant(chimera);
            //N.narrative().more();
            N.narrative().print(b, "A hideous amalgam rises from the sacrifice. It's alive! IT'S ALIVE!!!");
            //N.narrative().more();
            if(Rand.d100((sk+b.getModifiedIntuition())/2-complexity)) {
                ((NPC)chimera).setFamiliar(b);
                N.narrative().print(b, Grammar.start(chimera, "lower")+" its gaze.");
            }
            else {
                chimera.setThreat(b, Threat.kos);
                N.narrative().print(b, Grammar.start(chimera, "fix")+" its fiery gaze on you.");
            }
            b.statGain(Stat.in);
            b.skillUp("alchemy");
        //}
        //else {
            //N.narrative().print(b, "Nothing happens.");
        //}
    }
}
