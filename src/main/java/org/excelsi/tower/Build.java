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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Build extends DefaultNHBotAction {
    private Createable _c;


    static {
        Extended.addCommand("build", new Manufacture());
    }

    public String getDescription() {
        return "Build a structure or item from raw materials.";
    }

    public void setCreateable(Createable c) {
        _c = c;
    }

    public Createable getCreateable() {
        return _c;
    }

    public void perform() {
        Createable create = getCreateable();
        if(create==null) {
            create = Build.selectType(getBot(), "What do you want to build?");
        }
        if(create==null) {
            N.narrative().print(getBot(), "You're going to need more dupapleters for that!");
            throw new ActionCancelledException();
        }
        NHSpace repl = null;
        checkBuild(create, getBot());
        if(create instanceof Parasite||create instanceof NHSpace||create instanceof CreateableBot) {
            Direction d = N.narrative().direct(getBot(), "Which direction?");
            getBot().getEnvironment().face(d);
            repl = (NHSpace) getBot().getEnvironment().getMSpace().move(d, true);
            if((!repl.isReplaceable()&&!repl.isTransparent())||repl.isOccupied()) {
                N.narrative().print(getBot(), "Not enough room there.");
                throw new ActionCancelledException();
            }
        }
        final NHSpace rep = repl;
        final Createable c = create;
        build(c, getBot(), new Finish() {
            public void done() {
                N.narrative().print(getBot(), "Your work is done.");
                //N.narrative().more();
                if(c instanceof NHSpace) {
                    NHSpace place = rep;
                    while(place.isOccupied()) {
                        Direction d = N.narrative().direct(getBot(), "Which direction?");
                        getBot().getEnvironment().face(d);
                        place = (NHSpace) getBot().getEnvironment().getMSpace().move(d, true);
                    }
                    place.replace((NHSpace)c);
                }
                else if(c instanceof Parasite) {
                    rep.addParasite((Parasite)c);
                }
                else if(c instanceof Item) {
                    Item item = (Item) c;
                    if(getBot().isPlayer()) {
                        ((Patsy)getBot()).analyze(item);
                    }
                    getBot().getInventory().add(item);
                    N.narrative().print(getBot(), Grammar.key(getBot().getInventory(), (Item)c));
                }
                else if(c instanceof CreateableBot) {
                    NHBot b = ((CreateableBot)c).getBot();
                    ((NPC)b).setFamiliar(getBot());
                    getBot().setThreat(b, Threat.familiar);
                    NHSpace place = rep;
                    while(place.isOccupied()) {
                        Direction d = N.narrative().direct(getBot(), "Which direction?");
                        getBot().getEnvironment().face(d);
                        place = (NHSpace) getBot().getEnvironment().getMSpace().move(d, true);
                    }
                    place.setOccupant(b);
                }
            }

            public void fail() {
                N.narrative().print(getBot(), "This doesn't look right at all... you abandon your efforts.");
            }
        });
    }

    static Createable selectType(NHBot b, String query) {
        String t = N.narrative().reply(b, query);
        Createable create = null;
        for(Class c:Universe.getUniverse().getStructures()) {
            if(c.getName().toLowerCase().endsWith(t)) {
                try {
                    create = (Createable) c.newInstance();
                }
                catch(Exception e) {
                }
                break;
            }
        }
        if(create==null) {
            try {
                create = (Createable) Class.forName("org.excelsi.tower."+Character.toUpperCase(t.charAt(0))+t.substring(1)).newInstance();
            }
            catch(Exception e) {
            }
            if(create==null) {
                try {
                    NHBot mech = null;
                    for(NHBot bot:Universe.getUniverse().getBots()) {
                        if(bot.getCommon().equals(t)&&bot.getForm() instanceof Mech) {
                            mech = bot;
                            break;
                        }
                    }
                    if(mech!=null) {
                        create = new CreateableBot(mech, (Item[])DefaultNHBot.deepCopy(mech.getPack()), "robotics");
                    }
                }
                catch(Exception e) {
                }
            }
        }
        return create;
    }

    interface Finish {
        void done();
        void fail();
    }

    static List<List<Item>> checkBuild(Createable t, NHBot bot) {
        return checkBuild(t, bot, false);
    }

    static List<List<Item>> checkBuild(final Createable t, final NHBot bot, boolean suppress) {
        Item[] ings = t.getIngredients();
        if(ings==null) {
            if(!suppress) {
                N.narrative().print(bot, "You're pretty sure "+Grammar.pluralize(t.getName())+" are atomic.");
            }
            throw new ActionCancelledException();
        }
        List<List<Item>> toRemove = new ArrayList<List<Item>>(ings.length);
        List<Item> missing = new ArrayList<Item>(ings.length);
        for(Item ing:ings) {
            List<Item> match = new ArrayList<Item>(1);
            toRemove.add(match);
            boolean found = false;
            for(Item i:bot.getInventory().getItem()) {
                if(i.getName().endsWith(ing.getTrueName())) {
                    match.add(i);
                    ing.setCount(ing.getCount()-i.getCount());
                    if(ing.getCount()<=0) {
                        found = true;
                        break;
                    }
                }
            }
            if(!found) {
                missing.add(ing);
            }
        }
        if(missing.size()>0) {
            if(!suppress) {
                StringBuilder b = new StringBuilder("You're going to need ");
                for(int i=0;i<missing.size();i++) {
                    Item it = missing.get(i);
                    b.append(Grammar.nonspecific(it));
                    if(i<missing.size()-1&&missing.size()>2) {
                        b.append(", ");
                    }
                    if(i==missing.size()-2) {
                        if(missing.size()<=2) {
                            b.append(" ");
                        }
                        b.append("and ");
                    }
                }
                //b.setLength(b.length()-2);
                b.append(" first!");
                N.narrative().print(bot, b.toString());
            }
            throw new ActionCancelledException();
        }
        return toRemove;
    }

    static class Phrases {
        public final String begin;
        public final String interrupted;
        public final String excuse;

        public Phrases(String b, String i, String e) {
            begin = b;
            interrupted = i;
            excuse = e;
        }
    }

    static void build(final Createable t, final NHBot bot, final Finish f) {
        build(t, bot, f, new Phrases("constructing", "halt construction", "building"));
    }

    static void build(final Createable t, final NHBot bot, final Finish f, final Phrases ph) {
        build(t, bot, f, ph, null);
    }

    static void build(final Createable t, final NHBot bot, final Finish f, final Phrases ph, final Item[] ingredients) {
        final Item[] ling = t.getIngredients();
        final List<List<Item>> rems = checkBuild(t, bot);
        final int skill = t.getDifficulty().getBonus()+bot.getSkill(t.getCreationSkill());
        N.narrative().print(bot, "You begin "+ph.begin+" "+Grammar.nonspecific(t.getName())+"...");
        bot.start(new ProgressiveAction() {
            int time = 16;
            public int getInterruptRate() { return 100; }
            public void stopped() {}
            public void interrupted() {
                N.narrative().print(bot, "You "+ph.interrupted+".");
            }
            public String getExcuse() { return ph.excuse+" "+Grammar.nonspecific(t.getName()); }
            public boolean iterate() {
                if(--time==0) {
                    boolean success = Rand.d100(skill);
                    if(success/*||Rand.d100(10)*/) {
                        System.err.println("ingredients: "+ingredients);
                        if(ingredients!=null) {
                            for(int i=0;i<ingredients.length;i++) {
                                System.err.println("consume: "+ingredients[i]);
                                bot.getInventory().consume(ingredients[i]);
                            }
                        }
                        else {
                            System.err.println("REMS: "+rems);
                            System.err.println("LING: "+Arrays.toString(ling));
                            for(int i=0;i<rems.size();i++) {
                                List<Item> rem = rems.get(i);
                                Item ing = ling[i];
                                for(int j=0;j<ing.getCount();j++) {
                                    Item re = rem.get(0);
                                    int count = re.getCount();
                                    bot.getInventory().consume(re);
                                    if(count==1) {
                                        rem.remove(0);
                                    }
                                }
                            }
                        }
                    }
                    if(success) {
                        f.done();
                    }
                    else {
                        f.fail();
                    }
                    bot.skillUp(t.getCreationSkill());
                }
                return time!=0;
            }
        });
    }

    public static class CreateableBot implements Createable {
        private NHBot _b;
        private Item[] _ing;
        private String _skill;


        //public CreateableBot(NHBot b) {
            //this(b, (Item[]) DefaultNHBot.deepCopy(b.getPack()));
        //}

        public CreateableBot(NHBot b, Item[] ing, String skill) {
            _b = b;
            _ing = ing;
            _skill = skill;
        }

        public NHBot getBot() {
            return _b;
        }

        public Item[] getIngredients() {
            //return (Item[]) DefaultNHBot.deepCopy(_b.getPack());
            return (Item[]) DefaultNHBot.deepCopy(_ing);
        }

        public String getCreationSkill() {
            //return _b.getForm() instanceof Mech?"robotics":"alchemy";
            return _skill;
        }

        public boolean accept(NHSpace s) {
            return true;
        }

        public Maneuver getDifficulty() {
            int tot = 0;
            for(Item i:getIngredients()) {
                tot += i.getCount();
            }
            if(tot<=10) {
                return Maneuver.routine;
            }
            else if(tot<=15) {
                return Maneuver.light;
            }
            else if(tot<=30) {
                return Maneuver.medium;
            }
            else if(tot<=45) {
                return Maneuver.hard;
            }
            else if(tot<=60) {
                return Maneuver.veryhard;
            }
            else if(tot<=100) {
                return Maneuver.folly;
            }
            else {
                return Maneuver.absurd;
            }
        }

        public String getName() {
            return _b.getCommon();
        }
    }
}
