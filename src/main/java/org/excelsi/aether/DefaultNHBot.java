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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.excelsi.matrix.*;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.WeakHashMap;
import java.util.Iterator;


/**
 * Abstract implementation of NHBot.
 */
public abstract class DefaultNHBot extends DefaultBot implements NHBot {
    private static final long serialVersionUID = 1L;
    private String _name;
    private String _profession;
    private boolean _unique;
    private String _common;
    private Gender _gender;
    private String _model;
    private String _color = "gray";
    private int _hp;
    private int _maxHp;
    private int _mp;
    private int _maxMp;
    private int _strength;
    private int _quickness;
    private int _intuition = 50;
    private int _presence = 50;
    private int _empathy = 50;
    private int _agility;
    private int _constitution;
    private int _reasoning = 80;
    private int _memory = 50;
    private int _selfdiscipline = 50;
    private int _weight = 1;
    private int _hungerRate = 1;
    private boolean _airborn;
    private boolean _aquatic;
    private boolean _slithering;
    private boolean _rolling;
    private boolean _levitating;
    private Sociality _sociality;
    private Temperament _temperament = Temperament.hostile; // bellum omnium contra omnes
    private Size _size = Size.medium;
    private boolean _dead;
    private int _hunger;
    private int _minLevel;
    private int _maxLevel;
    private int _rarity = 101;
    private int _loot = 0;
    private boolean _confused;
    private int _invisible;
    private int _blind;
    private int _vision = 4;
    private int _nightvision = 0;
    private int _audible = 0;
    private Connected _connected;
    private BotInventory _inventory = new BotInventory();
    private List<Affliction> _afflictions = new ArrayList<Affliction>(1);
    private List<Modifier> _modifiers = new ArrayList<Modifier>(1);
    private Map<String, Integer> _skills = new HashMap<String, Integer>(5);
    // TODO: if this is transient then doesn't survive save/load
    // but right now it's ok since you can't save while performing
    // an action anyway.
    private transient ProgressiveAction _progress;
    private Map<NHBot, Threat> _threats = new HashMap<NHBot, Threat>(3);


    public DefaultNHBot() {
        _skills.put("unarmed", 20); // default, if no one says anything
        addListener(new NHEnvironmentAdapter() {
            public void attackedBy(NHBot b, NHBot attacker) {
                if(b.isDead()) {
                    return;
                }
                if(!getEnvironment().getVisibleBots().contains(attacker)) {
                    getEnvironment().unhide();
                }
                DefaultNHBot bb = (DefaultNHBot) b;
                Threat old = bb.threat(attacker);
                if(old!=Threat.kos) {
                    bb.setThreat(attacker, Threat.kos);
                    /*
                    if((old==Threat.familiar||old==Threat.friendly)&&attacker.isPlayer()) {
                        // if a friendly bot attacks, it is no longer friendly
                        N.narrative().print(bb, Grammar.start(bb, "get")+" angry!");
                    }
                    */
                }
            }
        });
    }

    public static NHBot copy(NHBot orig) {
        return (NHBot) deepCopy(orig);
    }

    public static Object deepCopy(Object orig) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(orig);
            oos.close();
            ObjectInputStream ois = new ThreadContextObjectInputStream(new ByteArrayInputStream(os.toByteArray()));
            Object copy = ois.readObject();
            ois.close();
            return copy;
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    public void setThreat(NHBot b, Threat t) {
        //Threat e = _threats.get(b);
        //if(e==Threat.familiar&&t!=Threat.familiar) {
            //throw new Error("here");
        //}
        Threat old = threat(b);
        _threats.put(b, t);
        if(!isPlayer()&&b.isPlayer()&&t==Threat.kos&&(old==Threat.familiar||old==Threat.friendly)) {
            N.narrative().print(this, Grammar.start(this, "get")+" angry!");
        }
    }

    private static int _threatCleaner = 0;
    public Threat threat(NHBot b) {
        Threat t = _threats.get(b);
        if(t==null&&Universe.getUniverse()!=null) {
            t = Universe.getUniverse().con(this, b);
        }
        if(t==null) {
            t = Threat.kos;
        }
        // hack for memory clearing
        if(++_threatCleaner==42) {
            for(NHBot bot:new ArrayList<NHBot>(_threats.keySet())) {
                if(bot.isDead()) {
                    _threats.remove(bot);
                }
            }
            _threatCleaner = 0;
        }
        return t;
    }

    public void polymorph(NHBot to) {
        // don't polymorph these attributes
        final List excludes = Arrays.asList(new String[]{"class", "hidden", "model", "level", "environment", "dead", "inventory", "pack", "wearing", "wielded", "form", "name", "hunger", "eventSource", "autopickup", "skills", "afflictions", "familiar"});
        try {
            BeanInfo bi = Introspector.getBeanInfo(getClass());
            PropertyDescriptor[] props = bi.getPropertyDescriptors();
            for(PropertyDescriptor p:props) {
                Method r = p.getReadMethod();
                Method w = p.getWriteMethod();
                if(w==null) {
                    continue;
                }
                if(excludes.indexOf(p.getName())==-1) {
                    try {
                        w.invoke(this, r.invoke(to, new Object[0]));
                    }
                    catch(Exception e) {
                        Logger.global.fine("can't polymorph '"+p.getName()+"': "+e.getMessage());
                        //throw new Error("can't polymorph '"+p.getName()+"': "+e.getMessage(), e);
                    }
                }
            }
            setHp(getMaxHp());
            setForm(to.getForm().getClass().newInstance());
            setModel(to.getModel());
            for(Affliction a:getAfflictions()) {
                if(!a.isStuck()) {
                    removeAffliction(a);
                }
            }
            for(Affliction a:to.getAfflictions()) {
                addAffliction(a);
            }
            //notifyListeners(null, null);
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    @Override public boolean intercept(final Context c, final Attack a) {
        return false;
    }

    public boolean canOccupy(NHSpace s) {
        return true;
    }

    public int[] getStats() {
        String[] stats = {"Hp", "MaxHp", "Mp", "MaxMp", "Strength", "Quickness", "Intuition", "Presence",
            "Empathy", "Agility", "Constitution", "Reasoning", "Memory", "SelfDiscipline"};
        int[] ss = new int[stats.length];
        try {
            for(int i=0;i<stats.length;i++) {
                ss[i] = ((Integer)getClass().getMethod("get"+stats[i], new Class[0]).invoke(this, new Object[0])).intValue();
            }
        }
        catch(Exception e) {
            throw new Error(e);
        }
        return ss;
    }

    public void setStats(int[] ss) {
        String[] stats = {"Hp", "MaxHp", "Mp", "MaxMp", "Strength", "Quickness", "Intuition", "Presence",
            "Empathy", "Agility", "Constitution", "Reasoning", "Memory", "SelfDiscipline"};
        try {
            clearModifier();
            for(int i=0;i<stats.length;i++) {
                getClass().getMethod("set"+stats[i], Integer.TYPE).invoke(this, ss[i]);
            }
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    public void setStat(Stat s, int value) {
        try {
            clearModifier();
            getClass().getMethod("set"+s.getMethodName(), Integer.TYPE).invoke(this, value);
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    public int getStat(Stat s) {
        try {
            return ((Integer)getClass().getMethod("get"+s.getMethodName(), new Class[0]).invoke(this, new Object[0])).intValue();
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    public int getModifiedStat(Stat s) {
        switch(s) {
            case st:
                return getModifiedStrength();
            case qu:
                return getModifiedQuickness();
            case ag:
                return getModifiedAgility();
            case co:
                return getModifiedConstitution();
            case sd:
                return getModifiedSelfDiscipline();
            case em:
                return getModifiedEmpathy();
            case in:
                return getModifiedIntuition();
            case re:
                return getModifiedReasoning();
            case me:
                return getModifiedMemory();
            case pr:
                return getModifiedPresence();
            case wt:
                return getModifiedWeight();
        }
        return 0;
    }

    public NHBot clone() {
        try {
            return (NHBot) super.clone();
        }
        catch(CloneNotSupportedException e) {
            throw new Error("clone must be supported");
        }
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        String o = _name;
        _name = name;
        notifyListeners("name", o);
    }

    public String getProfession() {
        return _profession;
    }

    public void setProfession(String profession) {
        _profession = profession.intern();
    }

    public int getHp() {
        return _hp;
    }

    public void setHp(int hp) {
        int o = _hp;
        _hp = hp;
        notifyListeners("hp", o);
    }

    public void setMaxHp(int maxHp) {
        int o = _maxHp;
        _maxHp = maxHp;
        notifyListeners("maxHp", o);
    }

    public int getMaxHp() {
        return _maxHp;
    }

    public void setCommon(String common) {
        if(common!=null) {
            _common = common.intern();
        }
        else {
            _common = common;
        }
    }

    public void setGender(Gender g) {
        _gender = g;
    }

    public Gender getGender() {
        if(_gender==null) {
            _gender = Rand.d100(51)?Gender.male:Gender.female;
        }
        return _gender;
    }

    public String getCommon() {
        return _common;
    }

    public void setUnique(boolean unique) {
        _unique = unique;
    }

    public boolean isUnique() {
        return _unique;
    }

    public void setModel(String model) {
        // do this check for model since it could save
        // a scenegraph change
        if(_model==null||!_model.equals(model)) {
            String om = _model;
            _model = model.intern();
            notifyListeners("model", om);
        }
    }

    public String getModel() {
        return _model;
    }

    public void setColor(String color) {
        // do this check for model since it could save
        // a scenegraph change
        if(_color==null||!_color.equals(color)) {
            String oc = _color;
            _color = color.intern();
            notifyListeners("color", oc);
        }
    }

    public String getColor() {
        return _color;
    }

    public void setMp(int mp) {
        clearModifier();
        _mp = mp;
    }

    public int getMp() {
        return _mp;
    }

    public void setMaxMp(int maxMp) {
        clearModifier();
        _maxMp = maxMp;
    }

    public int getMaxMp() {
        return _maxMp;
    }

    public void setStrength(int strength) {
        if(_strength!=strength) {
            clearModifier();
            int os = _strength;
            _strength = strength;
            notifyListeners("strength", os);
        }
    }

    public int getStrength() {
        return _strength;
    }

    public int getQuickness() {
        return _quickness;
    }

    public void setQuickness(int quickness) {
        if(_quickness!=quickness) {
            clearModifier();
            int oq = _quickness;
            _quickness = quickness;
            notifyListeners("quickness", oq);
        }
    }

    public int getEmpathy() {
        return _empathy;
    }

    public void setEmpathy(int empathy) {
        if(_empathy!=empathy) {
            clearModifier();
            int oe = _empathy;
            _empathy = empathy;
            notifyListeners("empathy", oe);
        }
    }

    public int getIntuition() {
        return _intuition;
    }

    public void setIntuition(int intuition) {
        if(_intuition!=intuition) {
            clearModifier();
            int oi = _intuition;
            _intuition = intuition;
            notifyListeners("intuition", oi);
        }
    }

    public int getPresence() {
        return _presence;
    }

    public void setPresence(int presence) {
        if(_presence!=presence) {
            clearModifier();
            int op = _presence;
            _presence = presence;
            notifyListeners("presence", op);
        }
    }

    public int getAgility() {
        return _agility;
    }

    public void setAgility(int agility) {
        if(_agility!=agility) {
            clearModifier();
            int oa = _agility;
            _agility = agility;
            notifyListeners("agility", oa);
        }
    }

    public int getConstitution() {
        return _constitution;
    }

    public void setConstitution(int constitution) {
        if(_constitution!=constitution) {
            clearModifier();
            int oc = _constitution;
            _constitution = constitution;
            notifyListeners("constitution", oc);
        }
    }

    public int getMemory() {
        return _memory;
    }

    public void setMemory(int memory) {
        if(_memory!=memory) {
            clearModifier();
            int om = _memory;
            _memory = memory;
            notifyListeners("memory", om);
        }
    }

    public int getReasoning() {
        return _reasoning;
    }

    public void setReasoning(int reasoning) {
        if(_reasoning!=reasoning) {
            clearModifier();
            int or = _reasoning;
            _reasoning = reasoning;
            notifyListeners("reasoning", or);
        }
    }

    public int getSelfDiscipline() {
        return _selfdiscipline;
    }

    public void setWeight(int weight) {
        _weight = weight;
        clearModifier();
    }

    public int getWeight() {
        return _weight;
    }

    public void setHungerRate(int hungerRate) {
        _hungerRate = hungerRate;
        clearModifier();
    }

    public int getHungerRate() {
        return _hungerRate;
    }

    public void setSelfDiscipline(int selfdiscipline) {
        if(_selfdiscipline!=selfdiscipline) {
            clearModifier();
            int os = _selfdiscipline;
            _selfdiscipline = selfdiscipline;
            notifyListeners("selfdiscipline", os);
        }
    }

    public int getModifiedStrength() {
        return getModified(getStrength(), modifier().getStrength());
    }

    public int getModifiedQuickness() {
        return getModified(getQuickness(), modifier().getQuickness());
    }

    public int getModifiedEmpathy() {
        return getModified(getEmpathy(), modifier().getEmpathy());
    }

    public int getModifiedIntuition() {
        return getModified(getIntuition(), modifier().getIntuition());
    }

    public int getModifiedPresence() {
        return getModified(getPresence(), modifier().getPresence());
    }

    public int getModifiedAgility() {
        return getModified(getAgility(), modifier().getAgility());
    }

    public int getModifiedConstitution() {
        return getModified(getConstitution(), modifier().getConstitution());
    }

    public int getModifiedMemory() {
        return getModified(getMemory(), modifier().getMemory());
    }

    public int getModifiedReasoning() {
        return getModified(getReasoning(), modifier().getReasoning());
    }

    public int getModifiedSelfDiscipline() {
        return getModified(getSelfDiscipline(), modifier().getSelfDiscipline());
    }

    public int getModifiedWeight() {
        return Math.max(0, getModified(getWeight(), (int)modifier().getWeight()));
    }

    private int getModified(int base, int bonus) {
        return Math.max(0, base+bonus);
    }

    public NHEnvironment getEnvironment() {
        return (NHEnvironment) super.getEnvironment();
    }

    public boolean isPlayer() {
        return false;
    }

    public boolean isHuman() {
        return false;
    }

    public void setDead(boolean dead) {
        _dead = dead;
    }

    public boolean isDead() {
        return _dead;
    }

    public void setHunger(int hunger) {
        int o = _hunger;
        _hunger = hunger;
        notifyListeners("hunger", o);
    }

    public int getHunger() {
        return _hunger;
    }

    public int getModifiedHungerRate() {
        return getHungerRate()+modifier().getHungerRate();
    }

    public void setAquatic(boolean aquatic) {
        _aquatic = aquatic;
    }

    public boolean isAquatic() {
        return _aquatic;
    }

    public void setAirborn(boolean airborn) {
        boolean o = _airborn;
        _airborn = airborn;
        notifyListeners("airborn", o);
    }

    public boolean isAirborn() {
        return _airborn;
    }

    public void setSlithering(boolean slithering) {
        boolean o = _slithering;
        _slithering = slithering;
        notifyListeners("slithering", o);
    }

    public boolean isSlithering() {
        return _slithering;
    }

    public void setRolling(boolean rolling) {
        boolean o = _rolling;
        _rolling = rolling;
        notifyListeners("rolling", o);
    }

    public boolean isRolling() {
        return _rolling;
    }

    public void setLevitating(boolean levitating) {
        boolean o = _levitating;
        _levitating = levitating;
        if(o!=_levitating) {
            notifyListeners("levitating", o);
        }
    }

    public boolean isLevitating() {
        return _levitating;
    }

    public void setConfused(boolean confused) {
        boolean o = _confused;
        _confused = confused;
        if(o!=_confused) {
            notifyListeners("confused", o);
        }
    }

    public boolean isConfused() {
        return _confused;
    }

    public void setInvisible(int invisible) {
        if(_invisible!=invisible) {
            int o = _invisible;
            _invisible = Math.max(0, invisible);
            if(_invisible==0) {
                // do it based on space because UI doesn't know we're visible yet,
                // so it might not display narrative.
                N.narrative().print(getEnvironment().getMSpace(), Grammar.start(this, "return")+" to view.");
            }
            else if(o==0) {
                N.narrative().print(this, Grammar.start(this, "fade")+" from view.");
            }
            if(o!=_invisible||_invisible==0) {
                notifyListeners("invisible", o==1);
            }
        }
    }

    public int getInvisible() {
        return _invisible;
    }

    public boolean isInvisible() {
        return _invisible>0;
    }

    public void setBlind(int blind) {
        if(_blind!=blind) {
            int ob = _blind;
            _blind = Math.max(0, blind);
            if(ob==0||_blind==0) {
                notifyListeners("blind", ob==1);
            }
            if(((ob>0&&_blind==0)||(ob==0&&_blind>0))&&getEnvironment()!=null) {
                getEnvironment().unhide();
            }
        }
    }

    public int getBlind() {
        return _blind;
    }

    public boolean isBlind() {
        return _blind>0;
    }

    public void setVision(int vision) {
        _vision = vision;
    }

    public int getVision() {
        return _vision;
    }

    public void setNightvision(int nightvision) {
        _nightvision = nightvision;
    }

    public int getNightvision() {
        return _nightvision;
    }

    public int getModifiedVision() {
        return getVision();
    }

    public int getModifiedNightvision() {
        return getNightvision();
    }

    public float getCandela() {
        return modifier().getCandela();
    }

    public void setAudible(int audible) {
        _audible = audible;
    }

    public int getAudible() {
        return _audible;
    }

    public boolean isAudible() {
        return _audible > 0;
    }

    public void setConnected(Connected connected) {
        Connected o = _connected;
        _connected = connected;
        clearModifier();
        notifyListeners("connected", o);
    }

    public Connected getConnected() {
        return _connected!=null?_connected:Connected.none;
    }

    public Connected getModifiedConnected() {
        Connected c = getConnected();
        Modifier m = modifier();
        int mc = m.getConnected();
        if(mc>0) {
            while(mc-->0) {
                c = c.stronger();
            }
        }
        else if(mc<0) {
            while(mc++>0) {
                c = c.weaker();
            }
        }
        return c;
    }

    public void setSociality(Sociality soc) {
        _sociality = soc;
    }

    public Sociality getSociality() {
        return _sociality;
    }

    public void setTemperament(Temperament temperament) {
        _temperament = temperament;
    }

    public Temperament getTemperament() {
        return _temperament;
    }

    public void setSize(Size s) {
        Size o = _size;
        _size = s;
        notifyListeners("size", o);
    }

    public Size getSize() {
        return _size;
    }

    public void setMinLevel(int minLevel) {
        _minLevel = minLevel;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        _maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public void setRarity(int rarity) {
        _rarity = rarity;
    }

    public int getRarity() {
        return _rarity;
    }

    public void setLoot(int loot) {
        _loot = loot;
    }

    public int getLoot() {
        return _loot;
    }

    public void setForm(Form f) {
        _inventory.setForm(f);
    }

    public Form getForm() {
        return _inventory.getForm();
    }

    public Inventory getInventory() {
        return _inventory;
    }

    public boolean isEquipped(Item item) {
        return _inventory.isEquipped(item);
    }

    @Override public boolean holds(Container c) {
        //TODO: bags
        return _inventory==c;
    }

    public void setPack(Item[] pack) {
        clearModifier();
        _inventory.add(pack);
    }

    public Item[] getPack() {
        return _inventory.getPack();
    }

    public boolean isPacked(Item item) {
        return _inventory.contains(item) && ! isEquipped(item);
    }

    public int score() {
        return getInventory().score();
    }

    public String toString() {
        String s = _name;
        if(s==null) {
            s = _common;
            if(s==null) {
                s = _profession;
                if(s==null) {
                    s = "mysterious entity";
                }
            }
        }
        return s;
    }

    public String toPack() {
        return "pack of "+Grammar.pluralize(getCommon());
    }

    public boolean changesNoticably() {
        return true;
    }

    public float sanity() {
        return getReasoning()/100f;
    }

    public void die(String cause) {
        die(new Source(cause), cause);
    }

    public void die(Source s, String cause) {
        setDead(true);
        setBlind(0);
        if(getEnvironment()!=null) {
            getEnvironment().die(s);
        }
    }

    public List<Affliction> getAfflictions() {
        return new ArrayList<Affliction>(_afflictions);
    }

    /**
     * Sets all afflictions on this bot. Prior afflictions,
     * if any, are removed.
     *
     * @param afflictions afflictions
     */
    public void setAfflictions(List<Affliction> afflictions) {
        if(_afflictions!=null) {
            for(Affliction a:getAfflictions()) {
                removeAffliction(a);
            }
        }
        for(Affliction a:afflictions) {
            addAffliction(a);
        }
    }

    public void addAffliction(Affliction a) {
        for(Affliction existing:_afflictions) {
            if(existing.equals(a)) {
                existing.compound(a);
                return;
            }
        }
        _afflictions.add(a);
        Logger.global.fine(this+" is afflicted by "+a.getName());
        a.setBot(this);
        if(getEnvironment()!=null) {
            for(EnvironmentListener l:getEnvironment().getListeners()) {
                if(l instanceof NHEnvironmentListener) {
                    ((NHEnvironmentListener)l).afflicted(this, a);
                }
            }
        }
    }

    public boolean isAfflictedBy(String affliction) {
        for(Affliction a:_afflictions) {
            if(a.getName().equals(affliction)) {
                return true;
            }
        }
        return false;
    }

    public Affliction getAffliction(String name) {
        for(Affliction a:getAfflictions()) {
            if(a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public void removeAffliction(Affliction a) {
        if(!_afflictions.remove(a)) {
            Logger.global.severe("affliction '"+a+"' does not afflict "+this);
        }
        Logger.global.fine(this+" is no longer afflicted by "+a.getName());
        if(getEnvironment()!=null) {
            for(EnvironmentListener l:getEnvironment().getListeners()) {
                if(l instanceof NHEnvironmentListener) {
                    ((NHEnvironmentListener)l).cured(this, a);
                }
            }
        }
    }

    public void removeAffliction(String name) {
        for(Affliction a:getAfflictions()) {
            if(a.getName().equals(name)) {
                removeAffliction(a);
                break;
            }
        }
    }

    public synchronized void addModifier(Modifier m) {
        _modifiers.add(m);
        clearModifier();
        notifyListeners("modifiers", null);
    }

    public synchronized void removeModifier(Modifier m) {
        if(!_modifiers.remove(m)) {
            throw new IllegalArgumentException("modifier '"+m+"' does not modify "+this);
        }
        clearModifier();
        notifyListeners("modifiers", null);
    }

    public List<Modifier> getModifiers() {
        return _modifiers;
    }

    public synchronized void clearModifier() {
        _cachedModifier = null;
    }

    private Modifier _cachedModifier = new Modifier();
    public synchronized Modifier modifier() {
        if(_cachedModifier==null) {
            Modifier t = new Modifier();
            for(Modifier m:_modifiers) {
                t.add(m);
            }
            for(Item i:getWearing()) {
                t.add(i.getModifier());
            }
            if(getWielded()!=null) {
                t.add(getWielded().getModifier());
            }
            //these are handled on inventory add/remove
            //for(Item i:getInventory().getItem()) {
                //t.add(i.getPackedModifier());
            //}
            _cachedModifier = t;
        }
        return _cachedModifier;
    }

    public void tick() {
        progress();
        for(Affliction a:getAfflictions()) {
            if(a.getOnset()==Affliction.Onset.tick) {
                a.beset();
            }
            a.tick();
        }
        _inventory.tick();
        if(_audible>0) {
            --_audible;
        }
    }

    public void start(ProgressiveAction action) {
        if(_progress!=null) {
            interrupt(true);
        }
        if(action!=null) {
            _progress = action;
            for(EnvironmentListener el:getListeners()) {
                if(el instanceof NHEnvironmentListener) {
                    ((NHEnvironmentListener)el).actionStarted(this, action);
                }
            }
            progress(); // first tick is free
        }
    }

    public boolean isOccupied() {
        return _progress!=null;
    }

    public void interrupt() {
        interrupt(false);
    }

    protected void interrupt(boolean ignoreRate) {
        if(_progress!=null) {
            ProgressiveAction old = _progress;
            if(ignoreRate||Rand.d100(old.getInterruptRate())) {
                _progress = null;
                old.interrupted();
                old.stopped();
                for(EnvironmentListener el:getListeners()) {
                    if(el instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)el).actionStopped(this, old);
                    }
                }
            }
        }
    }

    public ProgressiveAction getAction() {
        return _progress;
    }

    public void progress() {
        if(_progress!=null) {
            if(!_progress.iterate()) {
                _progress.stopped();
                ProgressiveAction old = _progress;
                _progress = null;
                for(EnvironmentListener el:getListeners()) {
                    if(el instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)el).actionStopped(this, old);
                    }
                }
            }
        }
    }

    public void afflict(Affliction.Onset onset) {
        for(Affliction a:getAfflictions()) {
            if(a.getOnset()==onset) {
                a.beset();
            }
        }
    }

    public boolean allow(NHBotAction a) {
        for(Affliction af:_afflictions) {
            if(!af.allow(a)) {
                return false;
            }
        }
        return true;
    }

    public int getStatGainRate() {
        return 3;
    }

    public int getSkill(String skill) {
        // have to check key existence to avoid NPE due to
        // idiotic primitive unwrapping by java
        return _skills.containsKey(skill)?_skills.get(skill):0;
    }

    public int getSkill(Armament arm) {
        if(_skills.containsKey(arm.getSkill())) {
            return _skills.get(arm.getSkill());
        }
        else {
            return 0;
        }
    }

    public Map<String, Integer> getSkills() {
        return _skills;
    }

    public void setSkills(Map<String, Integer> skills) {
        _skills = skills;
    }

    public void setSkill(String skill, int sk) {
        _skills.put(skill, sk);
    }

    public void setSkill(Armament arm, int skill) {
        _skills.put(arm.getSkill(), skill);
    }

    public void skillUp(String skill) {
        int sk = getSkill(skill);
        if(Rand.d100(101-sk)&&Rand.d100(10)) {
            setSkill(skill, 1+sk);
            if(isPlayer()) {
                //N.narrative().print(this, Grammar.first(Grammar.possessive(this))+" skill in "+skill+" improves.");
            }
        }
    }

    public void statGain(Stat s) {
        if(Rand.d100()>getStat(s)&&Rand.d100()<getStatGainRate()) {
            setStat(s, getStat(s)+1);
            if(isPlayer()||(threat(getEnvironment().getPlayer())==Threat.familiar&&getEnvironment().getPlayer().getEnvironment().getVisibleBots().contains(this))) {
                N.narrative().print(this, Grammar.start(this, "feel")+" "+s.getAdjective()+".");
            }
        }
    }

    public void setWielded(Item weapon) throws EquipFailedException {
        setWielded(weapon, null);
    }

    public void setWielded(Item weapon, String msg) throws EquipFailedException {
        _inventory.setWielded(weapon, msg);
        clearModifier();
        notifyListeners("wielded", null);
    }

    public Item getWielded() {
        return _inventory.getWielded();
    }

    public void setQuivered(Item missile) throws EquipFailedException {
        setQuivered(missile, null);
        notifyListeners("quivered", null);
    }

    public void setQuivered(Item missile, String msg) throws EquipFailedException {
        _inventory.setQuivered(missile, msg);
        notifyListeners("quivered", null);
    }

    public Item getQuivered() {
        return _inventory.getQuivered();
    }

    public void setWearing(Item[] wearing) throws EquipFailedException {
        for(Item w:wearing) {
            _inventory.wear(w, null);
        }
        clearModifier();
        notifyListeners("worn", null);
    }

    public void wear(Item i) throws EquipFailedException {
        wear(i, null);
    }

    public void wear(Item i, String msg) throws EquipFailedException {
        _inventory.wear(i, msg);
        clearModifier();
        notifyListeners("worn", i);
    }

    public void takeOff(Item i) throws EquipFailedException {
        _inventory.takeOff(i);
        clearModifier();
        notifyListeners("tookOff", i);
    }

    public Item[] getWearing() {
        return _inventory.getWearing();
    }

    public void notifyListeners(String attr, Object oldValue) {
        for(EnvironmentListener e:getListeners()) {
            e.attributeChanged(this, attr, oldValue);
        }
    }

    private class BotInventory extends Inventory {
        private Form _form;
        private Item _weapon;
        private Item _quivered;


        public BotInventory() {
        }

        public void tick() {
            List<Item> its = getItems();
            for(int i=0;i<its.size();i++) {
                its.get(i).update(this);
            }
        }

        public boolean destroy(Item it) {
            if(super.destroy(it)) {
                if(it==_weapon) {
                    _form.unequip(it);
                    _weapon = null;
                    if(getEnvironment()!=null) {
                        for(EnvironmentListener l:getEnvironment().getListeners()) {
                            if(l instanceof NHEnvironmentListener) {
                                ((NHEnvironmentListener)l).unequipped(DefaultNHBot.this, it);
                            }
                        }
                        notifyListeners("wielded", null);
                    }
                    if(it instanceof Affector) {
                        ((Affector)it).remove(DefaultNHBot.this);
                    }
                }
                else if(_form.isEquipped(it)) {
                    _form.unequip(it);
                    if(getEnvironment()!=null) {
                        for(EnvironmentListener l:getEnvironment().getListeners()) {
                            if(l instanceof NHEnvironmentListener) {
                                ((NHEnvironmentListener)l).unequipped(DefaultNHBot.this, it);
                            }
                        }
                        notifyListeners("tookOff", it);
                    }
                    if(it instanceof Affector) {
                        ((Affector)it).remove(DefaultNHBot.this);
                    }
                }
                else if(it==_quivered) {
                    _quivered = null;
                }
                if(it.getPackedModifier()!=null) {
                    removeModifier(it.getPackedModifier());
                }
                return true;
            }
            else {
                return false;
            }
        }

        public void setForm(Form form) {
            if(_form!=null) {
                Item weapon = getWielded();
                Item quivered = getQuivered();
                Item[] wearing = getWearing();
                _form = form;
                if(weapon != null) {
                    _weapon = null;
                    try {
                        setWielded(weapon, null);
                    }
                    catch(EquipFailedException e) {
                        N.narrative().print(DefaultNHBot.this, Grammar.start(DefaultNHBot.this, "drop")+" "+Grammar.possessive(DefaultNHBot.this)+" weapon!");
                        transfer(weapon, getEnvironment().getMSpace());
                    }
                }
                if(quivered != null) {
                    _quivered = null;
                    try {
                        setQuivered(quivered, null);
                    }
                    catch(EquipFailedException e) {
                        //N.narrative().print(DefaultNHBot.this, Grammar.start(DefaultNHBot.this, "drop")+" "+Grammar.possessive(DefaultNHBot.this)+" weapon!");
                        transfer(quivered, getEnvironment().getMSpace());
                    }
                }
                for(Item i:wearing) {
                    try {
                        wear(i, null);
                    }
                    catch(EquipFailedException e) {
                        N.narrative().print(DefaultNHBot.this, Grammar.first(Grammar.possessive(DefaultNHBot.this))+" "+i.getName()+" falls off!");
                        transfer(i, getEnvironment().getMSpace());
                    }
                }
            }
            else {
                _form = form;
            }
        }

        public Form getForm() {
            return _form;
        }

        public void setQuivered(Item quivered, String msg) throws EquipFailedException {
            _quivered = quivered;
            if(!contains(quivered)) {
                add(quivered);
            }
            if(msg!=null) {
                N.narrative().print(DefaultNHBot.this, msg);
            }
        }

        public Item getQuivered() {
            return _quivered;
        }

        public int remove(Item item) {
            int x = super.remove(item);
            if(item==_quivered) {
                _quivered = null;
            }
            if(item.getPackedModifier()!=null) {
                removeModifier(item.getPackedModifier());
            }
            return x;
        }

        public int consume(Item item) {
            if(_form.isEquipped(item)&&(item.getCount()==1||item.getStackType()==Item.StackType.separate)) {
                boolean weap = _weapon==item;
                _form.unequip(item);
                if(getEnvironment()!=null) {
                    for(EnvironmentListener l:getEnvironment().getListeners()) {
                        if(l instanceof NHEnvironmentListener) {
                            ((NHEnvironmentListener)l).unequipped(DefaultNHBot.this, item);
                        }
                    }
                    if(weap) {
                        notifyListeners("wielded", item);
                    }
                    else {
                        notifyListeners("tookOff", item);
                    }
                }
                if(item instanceof Affector) {
                    ((Affector)item).remove(DefaultNHBot.this);
                }
            }
            int x = super.consume(item);
            if(item==_quivered&&item.getCount()==0) {
                _quivered = null;
            }
            if(item.getPackedModifier()!=null&&item.getCount()==0) {
                removeModifier(item.getPackedModifier());
            }
            return x;
        }

        public int add(Item item) {
            int i = super.add(item);
            Modifier m = item.getPackedModifier();
            // since we can't statically enforce this, try to ensure
            // that returned modifier is not a new object each time.
            Modifier mcheck = item.getPackedModifier();
            if(m!=mcheck) {
                throw new IllegalStateException("packed modifier must be same instance");
            }
            if(m!=null) {
                addModifier(m);
            }
            if(isPlayer()) {
                ((Patsy)DefaultNHBot.this).analyze(item);
            }
            return i;
        }

        public void setWielded(Item weapon, String msg) throws EquipFailedException {
            if(_weapon==weapon) {
                throw new EquipFailedException(Grammar.start(DefaultNHBot.this)+" adjust your grip.");
            }
            Item oldWeapon = _weapon;
            if(_weapon!=null) {
                if(_weapon.getStatus()==Status.cursed) {
                    throw new EquipFailedException(Grammar.first(Grammar.noun(_weapon))+" is stuck to "+Grammar.possessive(DefaultNHBot.this)+" hand!");
                }
                unequip(_weapon);
                _weapon = null;
            }
            if(weapon!=null) {
                try {
                    equip(weapon, msg);
                    _weapon = weapon;
                    if(_weapon.getStatus()==Status.cursed) {
                        _weapon.setStatusIdentified(true);
                        //N.narrative().print(DefaultNHBot.this, Grammar.first(Grammar.noun(_weapon))+" welds itself to "+Grammar.possessive(DefaultNHBot.this)+" hand!");
                        N.narrative().printf(DefaultNHBot.this, "%n welds itself to %p hand!", _weapon, DefaultNHBot.this);
                    }
                }
                catch(EquipFailedException e) {
                    if(oldWeapon!=null) {
                        equip(oldWeapon);
                        _weapon = oldWeapon;
                    }
                    throw e;
                }
            }
        }

        public Item getWielded() {
            return _weapon;
        }

        public void wear(Item i, String msg) throws EquipFailedException {
            equip(i, msg);
        }

        public void takeOff(Item i) throws EquipFailedException {
            if(i.getStatus()==Status.cursed) {
                i.setStatusIdentified(true);
                throw new EquipFailedException(Grammar.first(Grammar.noun(i))+" doesn't want to come off!");
            }
            if(!isEquipped(i)) {
                throw new EquipFailedException(Grammar.startToBe(DefaultNHBot.this)+" not wearing that.");
            }
            unequip(i);
        }

        public Item[] getWearing() {
            List wearing = new ArrayList();
            for(Item i:getItem()) {
                if(isEquipped(i)&&(i instanceof Wearable)) {
                    wearing.add(i);
                }
            }
            return (Item[]) wearing.toArray(new Item[wearing.size()]);
        }

        private void equip(Item item) throws EquipFailedException {
            equip(item, null);
        }

        private void equip(Item item, String msg) throws EquipFailedException {
            if(!contains(item)) {
                add(item);
                //throw new IllegalArgumentException("'"+item+"' is not in "+Grammar.possessive(DefaultNHBot.this)+" inventory");
            }
            assertForm();
            _form.equip(item);
            if(msg!=null) {
                N.narrative().print(DefaultNHBot.this, msg);
            }
            if(getEnvironment()!=null) {
                for(EnvironmentListener l:getEnvironment().getListeners()) {
                    if(l instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)l).equipped(DefaultNHBot.this, item);
                    }
                }
            }
            if(item instanceof Affector) {
                ((Affector)item).attach(DefaultNHBot.this);
            }
        }

        private void unequip(Item item) throws EquipFailedException {
            if(!contains(item)) {
                throw new IllegalArgumentException("'"+item+"' is not in "+Grammar.possessive(DefaultNHBot.this)+" inventory");
            }
            assertForm();
            _form.unequip(item);
            if(getEnvironment()!=null) {
                for(EnvironmentListener l:getEnvironment().getListeners()) {
                    if(l instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)l).unequipped(DefaultNHBot.this, item);
                    }
                }
            }
            if(item instanceof Affector) {
                ((Affector)item).remove(DefaultNHBot.this);
            }
        }

        public boolean isEquipped(Item item) {
            assertForm();
            return _form.isEquipped(item);
        }

        public Item[] getPack() {
            List pack = new ArrayList();
            for(Item i:getItem()) {
                if(!isEquipped(i)) {
                    pack.add(i);
                }
            }
            return (Item[]) pack.toArray(new Item[pack.size()]);
        }

        public Item[] getEquipment() {
            List equip = new ArrayList();
            for(Item i:getItem()) {
                if(isEquipped(i)) {
                    equip.add(i);
                }
            }
            return (Item[]) equip.toArray(new Item[equip.size()]);
        }

        private void assertForm() {
            if(_form==null) {
                throw new IllegalStateException(DefaultNHBot.this.toString()+" has no form");
            }
        }
    }

    public abstract static class MoveAction extends DefaultNHBotAction implements Director {
        private Pickup _pickup = new Pickup(true);
        private LookHere _lookHere = new LookHere();
        private boolean _face;


        public MoveAction() {
            this(true);
        }

        public MoveAction(boolean face) {
            _face = face;
            _lookHere.setLootOnly(true);
        }

        public String getDescription() {
            return "Move "+getDirection()+".";
        }

        public boolean isRecordable() {
            return false;
        }

        @Deprecated public void perform() {
            //throw new UnsupportedOperationException("no");
            //System.err.println("deprecated perform()");
            perform(Actor.context());
        }

        public void perform(final Context c) {
            if(!c.getActor().canOccupy(c.getActor().getEnvironment().getMSpace())) {
                // such as if our environment was changed for the worse by hostile forces
                return;
            }
            Direction d = getDirection();
            MSpace m = c.getActor().getEnvironment().getMSpace().move(d);
            if(m==null) {
                throw new ActionCancelledException();
            }
            boolean doSwap = false;
            NHBot occ = (NHBot) m.getOccupant();
            if(occ!=null) {
                Threat threat = c.getActor().threat(occ);
                if(c.getActor().isPlayer()&&threat!=Threat.kos&&!c.getActor().isBlind()) {
                    if(threat==Threat.familiar) {
                        doSwap = true;
                    }
                    else {
                        if(!N.narrative().confirm(c.getActor(), "Really attack "+Grammar.noun(occ)+"?")) {
                            throw new ActionCancelledException();
                        }
                        else {
                            c.getActor().setThreat(occ, Threat.kos);
                        }
                    }
                }
            }
            else {
                // for example: a creature of the deep can
                // attack an occupant on the shore, but
                // can't move there
                if(!c.getActor().canOccupy((NHSpace)m)) {
                    throw new ActionCancelledException();
                }
            }
            c.getActor().afflict(Affliction.Onset.move);
            if(c.getActor().isConfused()) {
                c.getActor().getEnvironment().move(Direction.random());
            }
            else {
                if(_face) {
                    c.getActor().getEnvironment().face(d);
                }
                if(doSwap) {
                    N.narrative().print(c.getActor(), Grammar.start(c.getActor(), "displace")+" "+Grammar.noun(occ)+".");
                    c.getActor().getEnvironment().getMSpace().swapOccupant(occ.getEnvironment().getMSpace());
                }
                else {
                    c.getActor().getEnvironment().move(d);
                }
            }
            if(occ==null) {
                // only if we really moved
                if(c.getActor().isPlayer()) {
                    if(c.getActor().getEnvironment().getMSpace().numItems()>0) {
                        _pickup.setBot(c.getActor());
                        _lookHere.setBot(c.getActor());
                        try {
                            _pickup.perform(c);
                        }
                        catch(ActionCancelledException ignored) {
                        }
                        if(c.getActor().getEnvironment().getMSpace().numItems()>0) {
                            try {
                                _lookHere.perform(c);
                            }
                            catch(ActionCancelledException ignored) {
                            }
                        }
                    }
                    else {
                        //N.narrative().showLoot(null);
                        c.n().show(c.actor(), null);
                    }
                }
            }
        }
    }

    public static class Forward extends MoveAction implements Director {
        public Direction getDirection() {
            return getBot().getEnvironment().getFacing();
        }
    }

    public static class Backward extends MoveAction implements Director {
        public Backward() {
            super(false);
        }

        public Direction getDirection() {
            return getBot().getEnvironment().getFacing().opposing();
        }
    }

    public static class Pickup extends DefaultNHBotAction implements SpaceAction {
        private boolean _auto = false;
        private Item _item = null;


        public Pickup() {
        }

        public Pickup(boolean auto) {
            _auto = auto;
        }

        public String getDescription() {
            return "Pick up items or remove items from containers.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getEnvironment().getMSpace().numItems()>0;
        }

        public void setItem(Item i) {
            _item = i;
        }

        public void perform() {
            throw new UnsupportedOperationException();
        }

        public void perform(final Context c) {
            try {
                internalPerform(c);
            }
            catch(IllegalArgumentException e) {
                if(getBot().isPlayer()) {
                    c.n().printf(getBot(), "You're out of space.");
                }
            }
        }

        public String toString() {
            return "Pick up";
        }

        private void internalPerform(final Context c) {
            NHSpace space = (NHSpace) ((MatrixEnvironment)c.getActor().getEnvironment()).getMSpace();
            int s = space.numItems();
            if(c.getActor().isLevitating()) {
                if(!_auto) {
                    if(c.getActor().isPlayer()) {
                        c.n().print(c.getActor(), Grammar.start(c.getActor())+" cannot reach the ground.");
                    }
                }
                return;
            }
            if(s>0&&c.getActor().isPlayer()) {
                for(Item i:space.getItem()) {
                    ((Patsy)c.getActor()).analyze(i);
                }
            }
            if(space.isAutopickup()&&!_auto) {
                while(space.numItems()>0) {
                    Item it = space.firstItem();
                    space.transfer(it, c.getActor().getInventory());
                }
            }
            else {
                if(_auto) {
                    if(!space.isAutopickup()) {
                        Patsy p = (Patsy) c.getActor();
                        String api = p.getAutopickupItems();
                        boolean first = true;
                        if(p.isAutopickup()&&api!=null) {
                            for(Item i:space.getItem()) {
                                if(api.indexOf(i.getModel())>=0) {
                                    transfer(c, space, i, first);
                                    first = false;
                                }
                            }
                        }
                    }
                }
                else {
                    if(!space.pickup(c.getActor())) {
                        if(_item!=null) {
                            transfer(c, space, _item, true);
                        }
                        else if(space.numItems()==1) {
                            transfer(c, space, space.firstItem(), true);
                        }
                        else {
                            if(s==0) {
                                c.n().print(c.getActor(), "There is nothing here to pick up.");
                            }
                            else {
                                boolean first = true;
                                space.getLoot().sort();
                                for(Item it:N.narrative().choose(c.getActor(), space)) {
                                    transfer(c, space, it, first);
                                    first = false;
                                }
                            }
                        }
                    }
                }
            }
            _item = null;
        }

        private void transfer(final Context c, NHSpace space, Item it, boolean first) {
            if(it.getModifiedPackedWeight()>c.getActor().getModifiedConstitution()) {
                if(c.getActor().isPlayer()) {
                    c.n().printf(c.getActor(), "%n is too heavy to lift!", it);
                }
                else {
                    c.n().printf(c.getActor(), "%n can't lift %n!", c.getActor(), it);
                }
                return;
            }
            space.transfer(it, c.getActor().getInventory());
            if(c.getActor().isPlayer()) {
                //if(first) {
                    //if(!N.narrative().isClear()) {
                        //N.narrative().more();
                    //}
                    //N.narrative().clear();
                //}
                if(it.getStackType()!=Item.StackType.singular) {
                    c.n().print(c.getActor(), it /*Grammar.key(getBot().getInventory(), it)*/);
                }
                else {
                    N.narrative().print(c.getActor(), it.getName()+".");
                }
            }
            else {
                c.n().print(c.getActor(), Grammar.start(c.getActor(), "pick")+" up "+Grammar.nonspecific(it)+".");
            }
        }
    }

    public static class LookHere extends DefaultNHBotAction implements SpaceAction {
        private boolean _lootOnly;

        public String getDescription() {
            return "Describe the immediate vicinity.";
        }

        public boolean isPerformable(NHBot b) {
            return true;
        }

        public void setLootOnly(boolean lootOnly) {
            _lootOnly = lootOnly;
        }

        public boolean getLootOnly() {
            return _lootOnly;
        }

        @Override public void perform(final Context c) {
            //N.narrative().showLoot(null);
            c.n().show(c.actor(), null);
            c.n().show(c.actor(), c.actor().getEnvironment().getMSpace());
            c.actor().getEnvironment().getMSpace().look(c, false, _lootOnly);
            throw new ActionCancelledException();
        }

        public String toString() {
            return "Look here";
        }
    }

    public static class Multidrop extends DefaultNHBotAction {
        public Multidrop() {
        }

        public String getDescription() {
            return "Drop items on the ground or put them in containers.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getInventory().numItems()>0;
        }

        public void perform() {
            Item[] chosen = N.narrative().chooseMulti(getBot(), new ItemConstraints(
                getBot().getInventory(), "drop",
                new ItemFilter() { public boolean accept(Item i, NHBot bot) { return true; } },
                false), false);
            if(chosen.length==0) {
                throw new ActionCancelledException();
            }
            final Iterator<Item> i = Arrays.asList(chosen).iterator();
            getBot().start(new ProgressiveAction() {
                public boolean iterate() {
                    Item drop = i.next();
                    Drop d = new Drop();
                    d.setBot(getBot());
                    d.setItem(drop);
                    d.perform();
                    return i.hasNext();
                }

                public int getInterruptRate() {
                    return 100;
                }

                public void stopped() {
                }

                public void interrupted() {
                }

                public String getExcuse() {
                    return null;
                }
            });
        }
    }

    public static class Drop extends ItemAction {
        public Drop() {
            //super("drop", null, false, false);
            super("drop", null, false, true);
        }

        public String getDescription() {
            return "Drop an item on the ground or put it in a container.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getInventory().numItems()>0;
        }

        protected void act() {
            Item chosen = getItem();
            if(getBot().isEquipped(chosen)) {
                String verb = "wear";
                if(getBot().getWielded()==chosen) {
                    verb = "wield";
                }
                // "verbing that" sounds somehow bad.
                if(getBot().isPlayer()) {
                    N.narrative().print(getBot(), "You're currently "+verb+"ing that.");
                    // TODO: do not re-add
                    // re-add because drop ctor instructs ItemAction to remove item on choose
                    getBot().getInventory().add(chosen);
                }
                throw new ActionCancelledException();
            }
            NHSpace s = (NHSpace) ((MatrixEnvironment)getBot().getEnvironment()).getMSpace();
            N.narrative().print(getBot(), Grammar.start(getBot(), "drop")+" "+Grammar.nonspecific(chosen)+".");
            if(s.add(chosen, getBot())>=0) {
                //getBot().getInventory().remove(getItem());
            }
            else {
                // did not drop for some reason
                getBot().getInventory().add(chosen);
            }
        }
    }

    public static class Open extends DefaultNHBotAction implements SpaceAction {
        private Doorway _d;

        public Open() {
        }

        public Open(Doorway d) {
            _d = d;
        }

        public String getDescription() {
            return "Open a door.";
        }

        public boolean isPerformable(NHBot b) {
            for(MSpace m:b.getEnvironment().getMSpace().surrounding()) {
                if(m instanceof Doorway&&!m.isOccupied()) {
                    return true;
                }
            }
            return false;
        }

        public void perform() {
            NHSpace sp = _d;
            if(sp==null) {
                Direction d = N.narrative().direct(getBot(), "Which direction?");
                getBot().getEnvironment().face(d);
                sp = (NHSpace) getBot().getEnvironment().getMSpace().move(d, true);
            }
            if(sp instanceof Doorway) {
                open((Doorway)sp);
            }
            else {
                if(getBot().isBlind()) {
                    N.narrative().print(getBot(), Grammar.start(getBot())+" can't find a door there.");
                }
                else {
                    N.narrative().print(getBot(), Grammar.start(getBot(), "see")+" no door there.");
                }
                throw new ActionCancelledException();
            }
        }

        public void open(Doorway door) {
            if(door.isLocked()) {
                N.narrative().print(getBot(), "The door is locked.");
            }
            else {
                if(door.isOpen()&&(door.isOccupied()||door.numItems()>0)) {
                    if(getBot().isPlayer()) {
                        N.narrative().print(getBot(), "There's something in the doorway!");
                    }
                    throw new ActionCancelledException();
                }
                door.setOpen(!door.isOpen());
                getBot().getEnvironment().unhide();
                if(!getBot().isPlayer()) {
                    getBot().getEnvironment().getPlayer().getEnvironment().unhide();
                }
                if(door.isOpen()) {
                    if(!getBot().isPlayer()) {
                        N.narrative().print(getBot(), "The door opens!");
                    }
                    else {
                        throw new ActionCancelledException();
                    }
                }
                else {
                    //if(getBot().isPlayer()) {
                        //N.narrative().print(getBot(), "You close the door.");
                    //}
                }
            }
        }
    }
}
