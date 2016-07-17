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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;


/**
 * A scoreable container.
 */
public class Inventory implements Container, Scoreable {
    public static final String KEYS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private boolean[] _usedKeys;
    private List<Item> _items = new ArrayList<Item>(1);
    private List<ContainerListener> _listeners;
    private Map<String,Item> _keys;


    /**
     * Constructs a new, empty inventory.
     */
    public Inventory() {
    }

    /**
     * Constructs a new inventory.
     *
     * @param items items in this inventory
     */
    public Inventory(List<Item> items) {
        setItem((Item[])items.toArray(new Item[items.size()]));
    }

    /**
     * Computes this inventory's score.
     *
     * @return sum of scores of all items in this inventory
     */
    public int score() {
        int total = 0;
        for(Item i:getItem()) {
            int s = i.getCount()*i.score();
            if(i.getStatus()==Status.blessed) {
                s *= 2;
            }
            else if(i.getStatus()==Status.cursed) {
                s /= 2;
            }
            total += s;
        }
        return total;
    }

    public void addContainerListener(ContainerListener listener) {
        if(_listeners==null) {
            _listeners = new ArrayList<ContainerListener>(1);
        }
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeContainerListener(ContainerListener listener) {
        if(_listeners==null||!_listeners.remove(listener)) {
            throw new IllegalArgumentException("listener '"+listener+"' not listening to "+this);
        }
    }

    public final void setKeyed(boolean keyed) {
        if(isKeyed()!=keyed) {
            if(keyed) {
                _keys = new HashMap<String,Item>();
                _usedKeys = new boolean[KEYS.length()];
                //for(Item i:_items) {
                for(Category c:categorize()) {
                    for(Iterator<Item> its=c.items();its.hasNext();) {
                        _keys.put(nextKey(), its.next());
                    }
                }
            }
            else {
                _keys = null;
            }
        }
    }

    public final boolean isKeyed() {
        return _keys!=null;
    }

    /**
     * Gets all items in this inventory in a random order.
     *
     * @return list of all items in random order
     */
    public List<Item> randomized() {
        ArrayList<Item> a = new ArrayList<Item>(_items);
        Collections.shuffle(a);
        return a;
    }

    public Item[] getItem() {
        return (Item[]) _items.toArray(new Item[_items.size()]);
    }

    public List<Item> getItems() {
        return _items;
    }

    public void setItem(Item[] items) {
        _items.clear();
        ((ArrayList)_items).ensureCapacity(items.length);
        for(Item i:items) {
            add(i);
        }
    }

    public boolean contains(Item item) {
        return _items.contains(item);
    }

    public Category[] categorize() {
        return categorize(null, null);
    }

    public Category[] categorize(ItemFilter filter, NHBot b) {
        Map<String, DefaultCategory> cats = new HashMap<String, DefaultCategory>();
        for(Item i:_items) {
            if(filter==null||filter.accept(i, b)) {
                DefaultCategory c = cats.get(i.getModel());
                if(c==null) {
                    c = new DefaultCategory(i.getCategory());
                    cats.put(i.getModel(), c);
                }
                c.add(i);
            }
        }
        List<Category> ordered = new ArrayList<Category>(cats.size());
        for(String p:getPackorder()) {
            Category c = cats.get(p);
            if(c!=null) {
                ordered.add(c);
                cats.remove(p);
            }
        }
        for(Category c:cats.values()) {
            ordered.add(c);
        }
        return (Category[]) ordered.toArray(new Category[ordered.size()]);
    }

    public void add(Container inventory) {
        if(inventory!=null) {
            Item[] items = inventory.getItem();
            ((ArrayList)_items).ensureCapacity(items.length+size());
            for(int i=0;i<items.length;i++) {
                add(items[i], true, null, null);
            }
        }
    }

    public void sort() {
        setKeyed(false);
        setKeyed(true);
    }

    public int add(Item item) {
        return add(item, false, null, null);
    }

    public int add(Item item, NHBot adder) {
        return add(item, false, adder, null);
    }

    public int add(Item item, NHBot adder, NHSpace origin) {
        return add(item, false, adder, origin);
    }

    public void add(Item[] items) {
        ((ArrayList)_items).ensureCapacity(size()+items.length);
        for(Item i:items) {
            add(i);
        }
    }

    private int add(Item item, boolean drop, NHBot adder, NHSpace origin) {
        if(item==null) {
            throw new IllegalArgumentException("null item");
        }
        assert item.getCategory()!=null;
        if(item.getStackType()==Item.StackType.singular) {
            for(int idx=0;idx<_items.size();idx++) {
                Item i = _items.get(idx);
                if(i.getClass().isAssignableFrom(item.getClass())) {
                    i.combine(item);
                    return idx;
                }
            }
        }
        else if(item.getStackType()==Item.StackType.stackable) {
            // time goes in circles
            for(int idx=0;idx<_items.size();idx++) {
                Item i = _items.get(idx);
                if(i.equals(item)) {
                    //System.err.println("COMBINING: "+i+" + "+item);
                    i.setCount(i.getCount()+item.getCount());
                    item.setCount(i.getCount());
                    //System.err.println("COMBINED: "+i+" + "+item);
                    if(_listeners!=null) {
                        for(ContainerListener l:getListeners()) {
                            if(drop) {
                                l.itemDropped(this, item, idx, true);
                            }
                            else {
                                if(adder==null) {
                                    l.itemAdded(this, item, idx, true);
                                }
                                else {
                                    l.itemAdded(this, item, idx, true, adder, origin);
                                }
                            }
                        }
                    }
                    return idx;
                }
            }
        }
        if(isKeyed()) {
            if(keyFor(item)==null) {
                String h = item.getKeyHint();
                if(h!=null&&!_keys.containsKey(h)) {
                    _keys.put(h, item);
                }
                else {
                    _keys.put(nextKey(), item);
                }
            }
        }
        _items.add(item);
        int idx = size()-1;
        if(_listeners!=null) {
            for(ContainerListener l:getListeners()) {
                if(drop) {
                    l.itemDropped(this, item, idx, false);
                }
                else {
                    if(adder==null) {
                        l.itemAdded(this, item, idx, false);
                    }
                    else {
                        l.itemAdded(this, item, idx, false, adder, origin);
                    }
                }
            }
        }
        return idx;
    }

    public int consume(Item it) {
        if(it.getCount()>1) {
            it.setCount(it.getCount()-1);
            return _items.indexOf(it);
        }
        else {
            clearKey(it);
            return remove(it);
        }
    }

    public Item split(Item it) {
        Item sp = Item.copy(it);
        sp.setCount(1);
        consume(it);
        return sp;
    }

    public int remove(Item it) {
        for(int i=0;i<_items.size();i++) {
            if(_items.get(i).equals(it)) {
                _items.remove(i);
                if(_listeners!=null) {
                    for(ContainerListener l:getListeners()) {
                        l.itemTaken(this, it, i);
                    }
                }
                return i;
            }
        }
        return -1;
    }

    public boolean destroy(Item it) {
        for(int i=0;i<_items.size();i++) {
            if(_items.get(i).equals(it)) {
                clearKey(it);
                _items.remove(i);
                if(_listeners!=null) {
                    for(ContainerListener l:getListeners()) {
                        l.itemDestroyed(this, it, i);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void destroyAll() {
        Item[] its = getItem();
        _items.clear();
        if(_keys!=null) {
            _keys.clear();
        }

        if(_usedKeys!=null) {
            for(int i=0;i<_usedKeys.length;i++) {
                _usedKeys[i] = false;
            }
        }
        if(_listeners!=null) {
            for(ContainerListener l:getListeners()) {
                l.itemsDestroyed(this, its);
            }
        }
    }

    public void transfer(Item item, Container destination) {
        if(!contains(item)) {
            throw new IllegalStateException("item '"+item+"' is not in this inventory");
        }
        destination.add(item);
        remove(item);
    }

    public Item firstItem() {
        if(_items.size()==0) {
            throw new IllegalStateException("inventory is empty");
        }
        return _items.get(0);
    }

    public Item itemFor(String key) {
        assert isKeyed();
        if(_keys==null) {
            throw new IllegalStateException("inventory "+getClass().getName()+" is not keyed: "+this);
        }
        return _keys.get(key);
    }

    public String keyFor(Item i) {
        if(_keys!=null) {
            for(Map.Entry<String,Item> e:_keys.entrySet()) {
                if(e.getValue().equals(i)) {
                //if(e.getValue()==i) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    public final int size() {
        return _items.size();
    }

    public final int numItems() {
        return size();
    }

    public String validKeys(ItemFilter filter, NHBot bot) {
        StringBuffer keys = new StringBuffer();
        for(Item i:_items) {
            if(filter.accept(i, bot)) {
                keys.append(keyFor(i));
            }
        }
        return keys.toString();
    }

    private List<ContainerListener> getListeners() {
        return _listeners!=null?new ArrayList<ContainerListener>(_listeners):new ArrayList<ContainerListener>(0);
    }

    public String toString() {
        return _items.toString();
    }

    private String nextKey() {
        for(int i=0;i<_usedKeys.length;i++) {
            if(!_usedKeys[i]) {
                _usedKeys[i] = true;
                return Character.toString(KEYS.charAt(i));
            }
        }
        // reclaim old keys
        for(int i=0;i<_usedKeys.length;i++) {
            if(_usedKeys[i]) {
                Item it = itemFor(Character.toString(KEYS.charAt(i)));
                if(!contains(it)) {
                    clearKey(it);
                    return nextKey();
                }
            }
        }
        throw new ActionCancelledException("Inventory full!");
    }

    private void clearKey(Item it) {
        if(isKeyed()) {
            String key = keyFor(it);
            if(key!=null) {
                _keys.remove(key);
                int k = KEYS.indexOf(key);
                if(k>=0) {
                    _usedKeys[k] = false;
                }
            }
        }
    }

    private static String[] _packorder;
    private static String[] getPackorder() {
        if(_packorder==null) {
            String packorder = System.getProperty("tower.packorder", ")]%!,?");
            _packorder = new String[packorder.length()];
            for(int i=0;i<packorder.length();i++) {
                _packorder[i] = Character.toString(packorder.charAt(i));
            }
        }
        return _packorder;
    }

    public static void setPackorder(String packorder) {
        System.setProperty("tower.packorder", packorder);
        _packorder = null;
    }

    public interface Category {
        String getName();
        Item[] getItems();
        int size();
        Item getItem(int index);
        boolean contains(Item item);
        Iterator<Item> items();
    }

    private class DefaultCategory extends ArrayList<Item> implements Category {
        private String _name;


        public DefaultCategory(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }

        public Iterator<Item> items() {
            return iterator();
        }

        public Item[] getItems() {
            return (Item[]) toArray(new Item[size()]);
        }

        public Item getItem(int index) {
            return (Item) get(index);
        }

        public boolean removeItem(Item item) {
            return remove(item);
        }

        public boolean contains(Item item) {
            return contains((Object)item);
        }
    }
}
