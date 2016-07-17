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


import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.excelsi.matrix.EnvironmentListener;
import org.excelsi.matrix.Actor;


public abstract class Item implements java.io.Serializable, Material, Interceptor {
    private static final long serialVersionUID = 1L;
    private static final String[] DEFAULT_DAMAGES = {"severely damaged ",
        "badly damaged ", "damaged ", "well-worn ", "worn "};
    public static final int WEAPON_TYPE_SLASH = 0;
    public static final int WEAPON_TYPE_CRUSH = 1;
    public static final int WEAPON_TYPE_PUNCTURE = 2;
    public static final int WEAPON_TYPE_SLASHCRUSH = 3;
    public static final int WEAPON_TYPE_SLASHPUNCTURE = 4;

    public enum DisplayType { inventory, status };
    public enum StackType { singular, stackable, separate };

    private String _name;
    private boolean _equipped;
    private boolean _identified;
    private boolean _identifiedStatus;
    private int _count = 1;
    private int _hp = 1;
    private int _maxHp = 1;
    private Status _status = Status.uncursed;
    private List<Fragment> _fragments;
    private List<String> _tags;
    private static Map<Class, Boolean> _classIdentified = new HashMap<Class, Boolean>();


    static void writeStatic(ObjectOutputStream os) throws IOException {
        Map<String, Boolean> ser = new HashMap<String, Boolean>();
        for(Map.Entry<Class,Boolean> e:_classIdentified.entrySet()) {
            ser.put(e.getKey().getName(), e.getValue());
        }
        os.writeObject(ser);
    }

    static void readStatic(ObjectInputStream is) throws ClassNotFoundException, IOException {
        _classIdentified = new HashMap<Class, Boolean>();
        Map<String,Boolean> ser = (Map<String,Boolean>) is.readObject();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for(Map.Entry<String,Boolean> e:ser.entrySet()) {
            _classIdentified.put(cl.loadClass(e.getKey()), e.getValue());
        }
    }

    /** 
     * Constructs a new Item. The item's name is set to the unscoped name
     * of the item's class, with underscores converted to dashes and capital
     * letters marking word beginnings. For example, the class
     * <code>org.excelsi.nh.tower.FoodRation</code> is named <code>food
     * ration</code>, and <code>org.excelsi.nh.tower.Battle_Axe</code> is
     * named <code>battle-axe</code>.
     */
    public Item() {
        String cname = getClass().getName().substring(1+getClass().getName().lastIndexOf('.'));
        if(cname.indexOf('$')>=0) {
            cname = cname.substring(1+cname.lastIndexOf('$'));
        }
        StringBuffer iname = new StringBuffer(""+Character.toLowerCase(cname.charAt(0)));
        for(int i=1;i<cname.length();i++) {
            char c = cname.charAt(i);
            if(c=='_') {
                iname.append('-');
            }
            else if(Character.isUpperCase(c)) {
                if(i>0&&cname.charAt(i-1)!='_') {
                    iname.append(" ");
                }
                iname.append(Character.toLowerCase(c));
            }
            else if(Character.isDigit(c)) {
                if(i>0&&!Character.isDigit(cname.charAt(i-1))) {
                    iname.append(" ");
                }
                iname.append(c);
            }
            else {
                iname.append(Character.toLowerCase(c));
            }
        }
        setName(iname.toString());
    }

    public String getName() {
        return _name;
        //return isIdentified()?_status+" "+_name:_name;
    }

    public void setName(String name) {
        if(name!=null) {
            _name = name.intern();
        }
        else {
            _name = name;
        }
    }

    /**
     * Gets the name of this item as if its class was
     * identified. This method should not include status
     * or fragments.
     *
     * @return true name of this item
     */
    public final String getTrueName() {
        return _name;
    }

    /**
     * Gets a description of this item when the observer
     * does not have a good look at it, for example if it
     * is on a distant space. By default, returns the value of
     * <code>getName()</code>.
     *
     * @return obscured name
     */
    public String getObscuredName() {
        return getName();
    }

    public void addTag(String tag) {
        if(_tags==null) {
            _tags = new ArrayList<String>();
        }
        _tags.add(tag);
    }

    public void setTags(String[] tags) {
        _tags = new ArrayList<String>();
        _tags.addAll(Arrays.asList(tags));
    }

    public String[] getTags() {
        return _tags==null?null:_tags.toArray(new String[0]);
    }

    public void setEquipped(boolean equipped) {
        _equipped = equipped;
    }

    public boolean isEquipped() {
        return _equipped;
    }

    public String getAudio() {
        return null;
    }

    private Modifier _mod = null;
    public Modifier getModifier() {
        if(_mod==null) {
            _mod = new Modifier();
        }
        _mod.clear();
        if(_fragments==null) {
            return _mod;
        }
        else {
            Modifier m = new Modifier();
            for(int i=0;i<_fragments.size();i++) {
                m.add(_fragments.get(i).getModifier());
            }
            return m;
        }
    }

    public Modifier getPackedModifier() {
        return null;
    }

    public void setIdentified(boolean identified) {
        _identified = identified;
        if(true /*_identified*/) {
            if(_fragments!=null) {
                for(Fragment f:_fragments) {
                    f.setIdentified(identified);
                }
            }
        }
        setStatusIdentified(identified);
    }

    public boolean isIdentified() {
        return _identified;
    }

    public void setStatusIdentified(boolean statusIdentified) {
        _identifiedStatus = statusIdentified;
    }

    public boolean isStatusIdentified() {
        return _identifiedStatus;
    }

    public void setClassIdentified(boolean identified) {
        _classIdentified.put(getClass(), identified);
        if(_fragments!=null) {
            for(Fragment f:_fragments) {
                f.setClassIdentified(identified);
            }
        }
    }

    public boolean isClassIdentified() {
        return _classIdentified.containsKey(getClass())?_classIdentified.get(getClass()):false;
    }

    public void setStatus(Status status) {
        _status = status;
    }

    public Status getStatus() {
        return _status;
    }

    public String[] getDamageNames() {
        return DEFAULT_DAMAGES;
    }

    /**
     * Gets this item's find rate. Find rates are all relative to each other.
     * For example, an item with rate 10 is found ten times more often than
     * an item with rate 1, but if all rates are 1, all items are found in
     * equal numbers. Defaults to 50.
     *
     * @return rate between 0 and 100, inclusive
     */
    public int getFindRate() {
        return 50;
    }

    /**
     * Tests if this item is obtainable by normal means. Items which can only
     * be acquired by special means should return <code>false</code> here.
     * Returns <code>true</code> by default. Most items are obtainable.
     *
     * @return whether or not this item can be acquired by normal means
     */
    public boolean isObtainable() {
        return true;
    }

    /**
     * Tests if this item can be wished for. Returns <code>true</code>
     * by default.
     *
     * @return whether or not this item can be obtained by wishing
     */
    public boolean isWishable() {
        return true;
    }

    public float getShininess() {
        return 1f;
    }

    /**
     * Gets the find rate of this item including all fragments.
     * The occurrence is the base find rate multiplied by the
     * find rates of all fragments.
     *
     * @return occurrence
     */
    public int getOccurrence() {
        int rate = getFindRate();
        int mod = rate;
        if(_fragments!=null) {
            for(Fragment f:_fragments) {
                mod = (int) (mod * f.getOccurrence()/100f);
            }
        }
        //return mod>0?mod:rate; // don't let fragments lower find rate to 0
        return mod;
    }

    /**
     * Gets the display type of this item. Items with a type of
     * <code>DisplayType.inventory</code> will appear in the player's
     * inventory. This is the default.
     *
     * @return <code>DisplayType.inventory</code>
     */
    public DisplayType getDisplayType() {
        return DisplayType.inventory;
    }

    /**
     * Gets stacking behavior for this item. By default, items do not
     * stack. Stackable items share the same key.
     *
     * @return <code>StackType.separate</code>
     */
    public StackType getStackType() {
        return StackType.separate;
    }

    /**
     * When forming a sentence, the normal "is/are" rules are applied based
     * on item count. In some rare exceptions, however, the "is" conjugation should
     * always be used. Override this method to return <code>true</code> in the case of an
     * exception.
     *
     * @return <code>false</code>
     */
    public boolean isAlwaysSingular() {
        return false;
    }

    /**
     * When forming a sentence, the normal "is/are" rules are applied based
     * on item count. In some rare exceptions, however, the "are" conjugation should
     * always be used. Override this method to return <code>true</code> in the case of an
     * exception.
     *
     * @return <code>false</code>
     */
    public boolean isAlwaysPlural() {
        return false;
    }

    /**
     * Tests if this item is naturally-occuring (e.g., a rock or
     * an apple, and not a sword or a food ration, which are very unlikely
     * to occur in nature). By default, returns <code>false</code>.
     *
     * @return <code>true</code> if this item occurs naturally
     */
    public boolean isNatural() {
        return false;
    }

    /**
     * Gets a percentage weighting of where this item can normally be found.
     * For example, an item that can be found in early levels should have
     * a weighting of 0, while an item that can be found near the end of
     * the game should have a weighting of 1. By default, returns 0.5.
     *
     * @return weighting
     */
    public float getLevelWeight() {
        return 0.5f;
    }

    public void combine(Item i) {
        throw new UnsupportedOperationException("not singular");
    }

    public void update(Container c) {
    }

    public void randomize() {
        int r = Rand.d100();
        if(r<=15) {
            setStatus(Status.cursed);
        }
        else if(r<=20) {
            setStatus(Status.blessed);
        }
    }

    public int score() {
        return 1;
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int count) {
        _count = count;
    }

    public void setHp(int hp) {
        _hp = hp;
    }

    public int getHp() {
        return _hp;
    }

    public void setMaxHp(int maxHp) {
        _maxHp = maxHp;
    }

    public int getMaxHp() {
        return _maxHp;
    }

    protected void initHp(int hp) {
        _hp = hp;
        _maxHp = hp;
    }

    public int getSlotCount() {
        return 1;
    }

    public int getSlotModifier() {
        return 0;
    }

    public boolean isUnique() {
        return false;
    }

    public boolean isFragile() {
        return false;
    }

    public boolean invokesIncidentally() {
        return false;
    }

    /**
     * Gets a hint for what key to bind to this
     * item in a keyed inventory. The container is
     * not required to use this value, it is only
     * a suggestion.
     *
     * @return hint or <code>null</code> to use container default
     */
    public String getKeyHint() {
        return null;
    }

    /**
     * Tests if this item is equal to another item. For the purpose of equality, this method
     * considers class, name, identified, status, and fragments.
     *
     * @return true if items are equal
     */
    public boolean equals(Object o) {
        if(o==null) {
            return false;
        }
        if(getStackType()==StackType.separate) {
            return o==this;
        }
        Item i = (Item) o;
        boolean eq = getClass()==i.getClass()&&_name.equals(i._name)&&_identified==i._identified
            &&_status==i.getStatus();
        if(eq) {
            List<Fragment> ta = getFragments();
            List<Fragment> to = i.getFragments();
            if(ta.size()!=to.size()) {
                return false;
            }
            for(int idx=0;idx<ta.size();idx++) {
                if(!ta.get(idx).equals(to.get(idx))) {
                    return false;
                }
            }
        }
        return eq;
    }

    public int hashCode() {
        return _name.hashCode() + (_identified?1:0);
    }

    public String toString() {
        return Grammar.nonspecific(this);
    }

    public String toObscureString() {
        return Grammar.nonspecificObscure(this);
    }

    private static final boolean[] EMPTY = new boolean[0];
    public final String toTrueString() {
        boolean oid = isClassIdentified();
        boolean[] ob;
        if(_fragments!=null) {
            ob = new boolean[_fragments.size()];
            for(int i=0;i<_fragments.size();i++) {
                ob[i] = _fragments.get(i).isClassIdentified();
            }
        }
        else {
            ob = EMPTY;
        }
        setClassIdentified(true);
        String ret = Grammar.nonspecific(this);
        setClassIdentified(oid);
        for(int i=0;i<ob.length;i++) {
            _fragments.get(i).setClassIdentified(ob[i]);
        }
        int k = ret.lastIndexOf('[');
        if(k>=0) {
            ret = ret.substring(0, k-1);
        }
        return ret;
    }

    public static Item copy(Item orig) {
        return (Item) DefaultNHBot.deepCopy(orig);
    }

    public static Item forName(String name) {
        try {
            for(Item i:Universe.getUniverse().getItems()) {
                if(i.getName().equals(name)) {
                    return i.getClass().newInstance();
                }
            }
            return null;
        }
        catch(Exception e) {
            throw new IllegalStateException("unable to instantiate "+name);
        }
    }

    public Fragment.GrammarType partOfSpeech(Fragment f) {
        return f.getPartOfSpeech();
    }

    public List<Fragment> getFragments() {
        return _fragments!=null?new ArrayList<Fragment>(_fragments):new ArrayList<Fragment>(0);
    }

    public Fragment getFragment(String name) {
        if(_fragments!=null) {
            for(Fragment f:_fragments) {
                if(f.getName().equals(name)) {
                    return f;
                }
            }
        }
        return null;
    }

    public void addFragment(Fragment f) {
        if(hasFragment(f)) {
            // TODO: fragments can only be added once right now
            return;
        }
        if(_fragments==null) {
            _fragments = new ArrayList<Fragment>(1);
        }
        _fragments.add(f);
        f.setOwner(this);
        notifyModified();
    }

    public void removeFragment(String name) {
        if(_fragments==null) {
            throw new IllegalArgumentException(name+" is not on "+this);
        }
        for(Fragment f:getFragments()) {
            if(f.getName().equals(name)) {
                _fragments.remove(f);
                f.setOwner(null);
                _mod = null;
            }
        }
        notifyModified();
    }

    public void removeFragment(Fragment f) {
        if(_fragments==null||!_fragments.remove(f)) {
            throw new IllegalArgumentException(f+" is not on "+this);
        }
        _mod = null;
        notifyModified();
    }

    public boolean hasFragment(String name) {
        if(_fragments!=null) {
            for(Fragment f:_fragments) {
                if(f.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasFragment(Class fragment) {
        if(_fragments!=null) {
            for(Fragment f:_fragments) {
                if(fragment.isAssignableFrom(f.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasFragment(Fragment f) {
        return _fragments!=null&&_fragments.contains(f);
    }

    public void setFragments(List<Fragment> fragments) {
        _fragments = new ArrayList<Fragment>(fragments.size());
        _fragments.addAll(fragments);
        for(Fragment f:_fragments) {
            f.setOwner(this);
        }
    }

    /** 
     * Gets the bot attributes applicable to this item.
     * Attributes are specified in their abbreviated forms,
     * and separated by forward slashes. For example, a
     * heavy weapon that depends on strength and agility,
     * with more of an emphasis on strength, would be specified
     * as <code>"ST/ST/AG"</code>, or equivalently
     * <code>"ST/AG/ST"</code>, order being irrelevant.
     * 
     * @return bot attributes applicable to this item
     */
    abstract public Stat[] getStats();
    abstract public String getModel();
    abstract public String getColor();
    abstract public String getCategory();
    abstract public SlotType getSlotType();

    /**
     * Gets the size of this item, in feet. For two- or three-dimensional
     * objects, use area or volume.
     *
     * @return size of this item
     */
    abstract public float getSize();

    /**
     * Gets the weight of this item, in pounds.
     *
     * @return weight of this item
     */
    abstract public float getWeight();

    /**
     * Gets the modified packed weight of this item, in pounds.
     *
     * @return modified packed weight of this item
     */
    public float getModifiedPackedWeight() {
        float w = getWeight();
        Modifier pm = getPackedModifier();
        if(pm!=null) {
            w += pm.getWeight();
        }
        Status s = getStatus();
        if(s==null) {
            s = Status.uncursed;
        }
        switch(s) {
            case cursed:
                w += w/3;
                break;
            case blessed:
                w -= w/3;
                break;
        }
        return w;
    }

    /**
     * Gets the modified weight of this item, in pounds.
     *
     * @return modified weight of this item
     */
    public float getModifiedWeight() {
        float w = getWeight();
        w += getModifier().getWeight();
        Status s = getStatus();
        if(s==null) {
            s = Status.uncursed;
        }
        switch(s) {
            case cursed:
                w += w/3;
                break;
            case blessed:
                w -= w/3;
                break;
        }
        return w;
    }

    abstract public void invoke(NHBot invoker);

    public boolean intercepts(Attack a) {
        if(_fragments!=null) {
            for(int i=0;i<_fragments.size();i++) {
                if(_fragments.get(i).intercepts(a)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Runnable intercept(final NHBot attacker, final NHBot defender, Attack a) {
        if(_fragments!=null) {
            for(int i=0;i<_fragments.size();i++) {
                if(_fragments.get(i).intercepts(a)) {
                    return _fragments.get(i).intercept(attacker, defender, a);
                }
            }
        }
        return null;
    }

    protected void notifyModified() {
        NHBot b = (NHBot) Actor.current();
        if(b!=null&&b.getEnvironment()!=null) {
            ((DefaultNHBot)b).clearModifier();
            for(EnvironmentListener li:b.getEnvironment().getListeners()) {
                if(li instanceof NHEnvironmentListener) {
                    ((NHEnvironmentListener)li).itemModified(b, this);
                }
            }
            // HACK
            ((DefaultNHBot)b).notifyListeners("candela", null);
        }
    }
}
