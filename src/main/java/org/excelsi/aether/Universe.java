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

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;


public class Universe implements BotFactory, ItemFactory, java.io.Serializable {
    private static Universe _universe;
    private String _story;
    private String _conclusion;
    private Map<String,String> _colormap;
    private Set<String> _hiddenColors;
    private Map<String,String> _charmap;
    private Map<String,String> _keymap;
    private Map<String,String> _actionmap;
    private Map<String,Action> _actions;
    private Map<String,Map<String,String>> _overlaykeys = new HashMap<String,Map<String,String>>();
    private List<Mixin> _mixins = new ArrayList<Mixin>();
    private List<Item> _items = new ArrayList<Item>(1024);
    private Set<Class> _uniques = new HashSet<Class>(1024);
    private List<NHBot> _bots = new ArrayList<NHBot>(1024);
    private Set<Class> _afflictions = new HashSet<Class>();
    private Set<Class> _structures = new HashSet<Class>();
    private Map<Threat, List<List<String>>> _threats;
    private Map<String,Integer> _catOccurs;
    private CatOccurrence[] _cats;
    private Game _g;


    public Universe() {
    }

    public static void setUniverse(Universe u) {
        _universe = u;
    }

    public static Universe getUniverse() {
        return _universe;
    }

    public void setMixins(List<Mixin> mixins) {
        _mixins = mixins;
    }

    public List<Mixin> getMixins() {
        return _mixins;
    }

    public void add(Mixin m) {
        _mixins.add(m);
    }

    public List<Mixin> mixinsFor(Class c) {
        ArrayList<Mixin> m = new ArrayList<Mixin>();
        for(Mixin mixin:getMixins()) {
            if(mixin.match(c)) {
                m.add(mixin);
            }
        }
        return m;
    }

    public void setStructures(Set structures) {
        HashSet<Class> set = new HashSet<Class>();
        for(Object o:structures) {
            if(o instanceof Class) {
                set.add((Class)o);
            }
            else if(o instanceof String) {
                try {
                    set.add(Class.forName(o.toString()));
                }
                catch(Exception e) {
                    throw new Error(e);
                }
            }
        }
        _structures = set;
    }

    public Set<Class> getStructures() {
        return _structures;
    }

    public void setAfflictions(Set<Class> afflictions) {
        _afflictions = afflictions;
    }

    public Set<Class> getAfflictions() {
        return _afflictions;
    }

    public void add(Item i) {
        i.setIdentified(true);
        _items.add(i);
    }

    public List<Item> getItems() {
        return _items;
    }

    public void setItems(List<Item> items) {
        for(Item i:items) {
            i.setIdentified(true);
        }
        _items = items;
    }

    public void addItems(List<Item> items) {
        for(Item i:items) {
            i.setIdentified(true);
        }
        _items.addAll(items);
    }

    public void setFinds(Map<String, Integer> rates) {
        _catOccurs = rates;
    }

    public Map<String,Integer> getFinds() {
        return _catOccurs;
    }

    public void addFinds(String category, Integer rate) {
        if(_catOccurs==null) {
            _catOccurs = new HashMap<String,Integer>();
        }
        _catOccurs.put(category, rate);
    }

    public String randomCategory() {
        if(_cats==null) {
            if(_catOccurs==null) {
                throw new IllegalStateException("no finds specified");
            }
            List<CatOccurrence> cats = new ArrayList<CatOccurrence>();
            int total = 0;
            for(Map.Entry<String,Integer> e:_catOccurs.entrySet()) {
                CatOccurrence co = new CatOccurrence();
                co.category = e.getKey();
                co.rate = e.getValue();
                total += co.rate;
                cats.add(co);
            }
            _cats = (CatOccurrence[]) cats.toArray(new CatOccurrence[cats.size()]);
            for(CatOccurrence co:_cats) {
                co.rate = (int) (co.rate*(100f/total));
            }
        }
        int rand = Rand.d100();
        for(CatOccurrence co:_cats) {
            rand -= co.rate;
            if(rand<=0) {
                return co.category;
            }
        }
        return _cats[_cats.length-1].category;
    }

    public void setBots(NHBot[] bots) {
        _bots = new ArrayList(Arrays.asList(bots));
    }

    public void addBots(NHBot[] bots) {
        _bots.addAll(Arrays.asList(bots));
    }

    public NHBot[] getBots() {
        return (NHBot[]) _bots.toArray(new NHBot[_bots.size()]);
    }

    public NHBot[] getNPCs() {
        List<NHBot> play = new ArrayList<NHBot>();
        for(NHBot b:getBots()) {
            if(! (b instanceof Patsy)) {
                play.add(createBot(b));
            }
        }
        return (NHBot[]) play.toArray(new NHBot[play.size()]);
    }

    public Patsy[] getPlayable() {
        List<Patsy> play = new ArrayList<Patsy>();
        for(NHBot b:getBots()) {
            if(b instanceof Patsy && !((Patsy)b).isHidden() && (Boolean.getBoolean("tower.creator")||!((Patsy)b).isDebug())) {
                play.add((Patsy)createBot(b));
            }
        }
        return (Patsy[]) play.toArray(new Patsy[play.size()]);
    }

    public NHBot createBot(final String common) {
        return createBot(new BotFactory.Constraints() {
            public boolean accept(NHBot b) {
                return b.getCommon().equals(common);
            }

            public String toString() {
                return common;
            }
        });
    }

    public NHBot createBot(BotFactory.Constraints c) {
        List<NHBot> filtered = new ArrayList<NHBot>(128);
        boolean nonZero = false;
        for(NHBot b:_bots) {
            if(c.accept(b)) {
                filtered.add(b);
                if(b.getRarity()>0) {
                    nonZero = true;
                }
            }
        }
        if(filtered.isEmpty()) {
            throw new NoSuchBotException("no bot matches '"+c.toString()+"'");
        }
        if(!nonZero) {
            throw new NoSuchBotException("all filtered bots had zero rarity");
        }
        int i = Rand.om.nextInt(filtered.size()), j = i;
        while(true) {
            if(Rand.d100(filtered.get(j).getRarity())) {
                return createBot(filtered.get(j));
            }
            if(++j==filtered.size()) {
                j = 0;
            }
        }
    }

    private NHBot createBot(NHBot b) {
        if(b instanceof NPC) {
            ((NPC)b).getAi().verify();
        }
        NHBot bot = DefaultNHBot.copy(b);
        bot.setHp(bot.getMaxHp());
        if(!bot.isPlayer()) {
            for(Item i:bot.getInventory().getItem()) {
                i.randomize();
                mix(i);
            }
        }
        mix(bot);
        return bot;
    }

    private void mix(Object o) {
        for(Mixin m:mixinsFor(o.getClass())) {
            m.mix(o);
        }
    }

    public Item createItem(ItemFilter f) {
        return createItem(f, true);
    }

    public Item createItem(ItemFilter f, boolean useFindRate) {
        if(_items.size()==0||_catOccurs==null) {
            return null;
        }

        // randomize item order
        List<Item> filtered = new ArrayList<Item>();
        for(Item i:_items) {
            if(_uniques.contains(i.getClass())) {
                continue;
            }
            if(f.accept(i, null)) {
                filtered.add(i);
            }
        }
        if(filtered.size()==0) {
            return null;
        }
        Item[] choices = (Item[]) filtered.toArray(new Item[filtered.size()]);
        for(int i=0;i<choices.length;i++) {
            int idx1 = Rand.om.nextInt(choices.length), idx2 = Rand.om.nextInt(choices.length);
            Item t = choices[idx2];
            choices[idx2] = choices[idx1];
            choices[idx1] = t;
        }
        for(;;) {
            boolean nonZero = false;
            for(Item i:choices) {
                nonZero = nonZero || (i.getOccurrence()>0);
                if(i.isObtainable()&&(!useFindRate||Rand.om.nextInt(100)<i.getOccurrence())) {
                    Item item = Item.copy(i);
                    item.setIdentified(false);
                    item.randomize();
                    mix(item);
                    if(item.isUnique()) {
                        _uniques.add(item.getClass());
                    }
                    return item;
                }
            }
            if(!nonZero) {
                throw new IllegalStateException("no matching items");
            }
        }
    }

    public void add(NHBot b) {
        _bots.add(b);
    }

    public void setCharmap(Map<String,String> charmap) {
        _charmap = clean(charmap);
    }

    public Map<String,String> getCharmap() {
        return _charmap;
    }

    public void setColormap(Map<String,String> colormap) {
        _hiddenColors = new HashSet<String>();
        _colormap = new HashMap<String,String>();
        for(Map.Entry<String,String> e:colormap.entrySet()) {
            String k = e.getKey();
            if(k.startsWith("-")) {
                k = k.substring(1);
                _hiddenColors.add(k);
            }
            _colormap.put(k, e.getValue());
        }
    }

    public Map<String,String> getColormap() {
        return _colormap;
    }

    public Map<String,String> getPublicColormap() {
        if(_colormap==null) {
            return null;
        }
        Map<String,String> pc = new HashMap<String,String>();
        for(Map.Entry<String,String> e:_colormap.entrySet()) {
            if(!_hiddenColors.contains(e.getKey())) {
                pc.put(e.getKey(), e.getValue());
            }
        }
        return pc;
    }

    public void setOverlaykeys(Map<String,Map<String,String>> overlayKeys) {
        _overlaykeys = overlayKeys;
    }

    public Map<String,Map<String,String>> getOverlaykeys() {
        return _overlaykeys;
    }

    public void setKeymap(Map<String,String> keymap) {
        _keymap = clean(keymap);
    }

    public Map<String,String> getKeymap() {
        return _keymap;
    }

    public String keyFor(String action) {
        for(Map.Entry<String,String> e:_keymap.entrySet()) {
            if(e.getValue().equals(action)) {
                return e.getKey();
            }
        }
        return null;
    }

    public void setActions(Map<String,Action> actions) {
        _actions = actions;
    }

    public Map<String,Action> getActions() {
        return _actions;
    }

    public void setActionmap(Map<String,String> actionmap) {
        _actionmap = actionmap;
        for(String val:_actionmap.values()) {
            try {
                Class.forName(val);
            }
            catch(Exception e) {
            }
        }
    }

    public Map<String,String> getActionmap() {
        return _actionmap;
    }

    public void setStory(String story) {
        _story = story;
    }

    public String getStory() {
        return _story;
    }

    public void setConclusion(String conclusion) {
        _conclusion = conclusion;
    }

    public String getConclusion() {
        return _conclusion;
    }

    public void setGame(Game g) {
        _g = g;
    }

    public Game getGame() {
        return _g;
    }

    public void setThreats(Map<Threat, List<List<String>>> threats) {
        // copy to ensure cache consistency
        _threats = new HashMap<Threat, List<List<String>>>(threats);
        _cachedThreats = new HashMap<String, Threat>();
        for(Map.Entry<Threat, List<List<String>>> e:_threats.entrySet()) {
            Object ot = e.getKey();
            Threat t;
            // work around jyaml bug
            if(ot instanceof Threat) {
                t = (Threat) ot;
            }
            else {
                t = Enum.valueOf(Threat.class, ot.toString());
            }
            for(List<String> set:e.getValue()) {
                for(int i=0;i<set.size();i++) {
                    for(int j=0;j<set.size();j++) {
                        String one = set.get(i);
                        String two = set.get(j);
                        if(one.length()>1&&one.endsWith("*")) {
                            one = one.substring(0, one.length()-1);
                            _cachedThreats.put(one+":"+one, t);
                        }
                        if(two.length()>1&&two.endsWith("*")) {
                            two = two.substring(0, two.length()-1);
                            _cachedThreats.put(two+":"+two, t);
                        }
                        if(i!=j) {
                            _cachedThreats.put(one+":"+two, t);
                        }
                    }
                }
            }
        }
    }

    public Map<Threat, List<List<String>>> getThreats() {
        // copy to ensure cache consistency
        return new HashMap<Threat, List<List<String>>>(_threats);
    }

    private Map<String, Threat> _cachedThreats;
    public Threat con(NHBot a, NHBot b) {
        if(_cachedThreats==null) {
            return Threat.kos;
        }
        Threat t = _cachedThreats.get(a.getCommon()+":"+b.getCommon());
        if(t==null) {
            t = _cachedThreats.get(a.getCommon()+":*");
            if(t==null) {
                t = _cachedThreats.get("*:"+b.getCommon());
                if(t==null) {
                    t = _cachedThreats.get("*:*");
                }
            }
        }
        return t!=null?t:Threat.none;
    }

    public String modelFor(String model) {
        return _charmap.get(model);
    }

    public String actionFor(String kb) {
        return _actionmap.get(kb);
    }

    private int occurrence(Item i) {
        //int oc = i.getOccurrence();
        if(_catOccurs==null) {
            throw new IllegalStateException("missing category find rates");
        }
        if(!_catOccurs.containsKey(i.getCategory())) {
            throw new IllegalStateException("no category find rate for '"+i.getCategory()+"'");
        }
        int mod = (int) (i.getOccurrence()*_catOccurs.get(i.getCategory())/100f);
        return mod;
        //return mod==0?oc:mod;
    }

    /**
     * Prints this universe to stderr. As a side effect,
     * all items are un-identified.
     */
    public void print() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        List<Item> its = new ArrayList<Item>(_items);
        Collections.sort(its, new Comparator<Item>() {
            public int compare(Item i1, Item i2) {
                //return occurrence(i1) - occurrence(i2);
                return (int) (100f*i1.getLevelWeight() - 100f*i2.getLevelWeight());
            }

            public boolean compare(Object o) {
                return false;
            }
        });
        pw.printf("%9s %3s %-4s %-4s %-5s %-12s %-2s %-3s %s\n",
                  "OCC","RTE","LV","SZ", "WT", "COLOR", "MO", "CT", "NAME");
        for(Item i:its) {
            boolean oi = i.isClassIdentified();
            i.setClassIdentified(true);
            String name = i.toString();
            if(name.startsWith("an uncursed ")) {
                name = name.substring("an uncursed ".length());
            }
            pw.printf("%3d (%3d) %3d %3.2f %3.2f %5.2f %-12s %-2s %-3s %s\n", i.getOccurrence(), occurrence(i), i.getFindRate(),
                    i.getLevelWeight(), i.getSize(), i.getWeight(), i.getColor(), i.getModel(), i.getCategory().substring(0,2), name);
            i.setClassIdentified(false);
        }
        pw.println();
        pw.printf("%3s %3s %-7s %-10s %-8s %-19s %s\n",
                  "POW", "RTE", "TYPE", "VERB", "STATS", "SKILL", "NAME");
        Map<String, List<Armament>> arms = new HashMap<String, List<Armament>>();
        for(Item i:its) {
            if(i instanceof Armament) {
                Armament a = (Armament) i;
                List<Armament> cat = arms.get(a.getSkill());
                if(cat==null) {
                    cat = new ArrayList<Armament>();
                    arms.put(a.getSkill(), cat);
                }
                cat.add(a);
            }
        }
        for(List<Armament> cat:arms.values()) {
            Collections.sort(cat, new Comparator<Armament>() {
                public int compare(Armament a1, Armament a2) {
                    return a1.getPower()-a2.getPower();
                }

                public boolean compare(Object o) {
                    return false;
                }
            });
            for(Armament a:cat) {
                String name = a.toString();
                if(name.startsWith("an uncursed ")) {
                    name = name.substring("an uncursed ".length());
                }
                StringBuilder stats = new StringBuilder();
                if(a.getStats()!=null) {
                    for(Stat s:a.getStats()) {
                        stats.append(s);
                        stats.append("/");
                    }
                    stats.setLength(stats.length()-1);
                }
                else {
                    stats.append("-");
                }
                pw.printf("%3d %3d %-7s %-10s %-8s %-19s %s\n", a.getPower(), a.getRate(), a.getType(), a.getVerb(), stats, a.getSkill(), name);
            }
        }

        pw.println();
        pw.printf("%-20s %5s %-2s %-15s %4s %3s %3s %3s %3s\n",
                  "NAME", "LEV", "M", "COLOR", "HP", "ST", "CO", "QU", "AG");
        NHBot[] bots = getBots();
        Arrays.sort(bots, new Comparator<NHBot>() {
            public int compare(NHBot a, NHBot b) {
                return b.getMinLevel()-a.getMinLevel();
            }
        });
        for(NHBot b:bots) {
            String lev = b.getMinLevel()==0?"-":(b.getMinLevel()+"-"+b.getMaxLevel());
            pw.printf("%-20s %5s %-2s %-15s %4d %3d %3d %3d %3d\n",
                b.getCommon(), lev, b.getModel(), b.getColor(), b.getMaxHp(), b.getStrength(), b.getConstitution(), b.getQuickness(), b.getAgility());
        }

        pw.close();
        System.err.println(sw.toString());
    }

    private static Map<String,String> clean(Map map) {
        Map<String,String> clean = new HashMap<String,String>();
        for(Iterator i=map.entrySet().iterator();i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            if(e.getKey()==null) {
                throw new Error("null key for "+e.getValue());
            }
            String k = e.getKey().toString();
            if(k.startsWith("\\") && k.length()==2) {
                k = k.substring(1);
            }
            clean.put(k, e.getValue().toString());
        }
        return clean;
    }

    private static class CatOccurrence {
        public String category;
        public int rate;
    }
}
