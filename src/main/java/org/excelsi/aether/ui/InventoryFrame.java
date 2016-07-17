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
package org.excelsi.aether.ui;


import com.jme.math.Vector3f;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.system.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;

import com.jmex.bui.*;
import com.jmex.bui.background.*;

import com.jmex.bui.layout.*;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;


public class InventoryFrame extends BContainer {
    private Inventory _inventory;
    private boolean _showKeys;
    private Map<Item,BLabel> _labels = new HashMap<Item,BLabel>();
    private Inventory.Category[] _cats;
    private int _i = 0;
    private final ItemFilter DISPLAY_HIDDEN = new ItemFilter() {
        public boolean accept(Item i, NHBot b) {
            return isDisplayHidden() || !i.getCategory().startsWith("-");
        }
    };


    public InventoryFrame(Inventory i) {
        super();
        VGroupLayout mgr = new VGroupLayout();
        mgr.setJustification(VGroupLayout.LEFT);
        mgr.setOffAxisJustification(VGroupLayout.LEFT);
        setLayoutManager(mgr);
        _inventory = i;
        if(_inventory==null) {
            _inventory = new Inventory();
        }
        _i = 0;
        _cats = _inventory.categorize();
    }

    private boolean _invKeyed;
    public void setShowKeys(boolean showKeys) {
        //_invKeyed = _inventory.isKeyed();
        if(_showKeys!=showKeys) {
            _showKeys = showKeys;
            if(_showKeys&&!_inventory.isKeyed()) {
                _inventory.setKeyed(true);
            }
            _i = 0;
            refresh();
        }
    }

    public boolean isShowKeys() {
        return _showKeys;
    }

    public void restoreKeyed() {
        if(_invKeyed!=_inventory.isKeyed()) {
            _inventory.setKeyed(_invKeyed);
        }
    }

    public void setInventory(Inventory inventory) {
        setInventory(inventory, null, null);
    }

    public void setInventory(Inventory inventory, ItemFilter filter, NHBot b) {
        _inventory = inventory;
        if(_inventory==null) {
            _inventory = new Inventory();
        }
        _i = 0;
        _cats = _inventory.categorize(and(filter, DISPLAY_HIDDEN), b);
        refresh(filter, b);
    }

    public boolean isDisplayHidden() {
        return true;
    }

    public boolean hasNext() {
        return _i<_cats.length;
    }

    public void next() {
        if(!hasNext()) {
            _i = 0;
        }
        refresh(null,null);
    }

    public void refresh() {
        refresh(null, null);
    }

    public void refresh(final ItemFilter filter, final NHBot b) {
        if(HUD.isUIThread()) {
            final int lin = 16;
            int h = DisplaySystem.getDisplaySystem().getHeight();
            removeAll();
            _labels.clear();
            BContainer parent = getParent();
            if(parent!=null) {
                parent.remove(this);
            }
            //for(Inventory.Category c:_inventory.categorize(filter, b)) {
            int height = 0;
            //System.err.println("i="+_i);
            //System.err.println("height="+height);
            //System.err.println("h="+h);
            //System.err.println("cats.length="+_cats.length);
            while(_i<_cats.length&&height<h-64) {
                Inventory.Category c = _cats[_i];
                if(height+lin*(1+c.size())>=h-64) {
                    break;
                }
                height += lin;
                String cname = c.getName();
                if(cname.startsWith("-")) {
                    if(!isDisplayHidden()) {
                        continue;
                    }
                    else {
                        cname = cname.substring(1);
                    }
                }
                BLabel header = new BLabel("- "+Grammar.first(Grammar.pluralize(cname))+" -");
                header.setStyleClass("header");
                add(header);
                boolean showKeys = _showKeys&&_inventory.isKeyed();
                for(Item i:c.getItems()) {
                    height += lin;
                    if(true||i.getDisplayType()==Item.DisplayType.inventory) {
                        StringBuilder ktext = new StringBuilder();
                        StringBuilder text = new StringBuilder();
                        createText(i, ktext, text, _sels.contains(i)?" + ":" - ");
                        BContainer line = new BContainer(new BorderLayout());
                        BLabel lkey = new BLabel(ktext.toString(), "keylabel");
                        BLabel litem = new BLabel(text.toString());
                        line.add(lkey, BorderLayout.WEST);
                        line.add(litem, BorderLayout.CENTER);
                        //BLabel it = new BLabel(text.toString());
                        //add(it);
                        add(line);
                        if(showKeys) {
                            //_labels.put(i, it);
                            _labels.put(i, lkey);
                        }
                    }
                }
                _i++;
            }
            //System.err.println("i="+_i);
            //System.err.println("height="+height);
            //System.err.println("h="+h);
            //System.err.println("cats.length="+_cats.length);
            if(parent!=null) {
                parent.add(this, BorderLayout.NORTH);
            }
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    refresh(filter, b);
                }
            });
        }
    }

    List<Item> _sels = new ArrayList<Item>();
    public Item[] choose(InputSource input, int max) {
        setShowKeys(true);
        assert _showKeys;
        boolean okeyed = _inventory.isKeyed();
        _sels.clear();
        _inventory.setKeyed(true);
        if(!_inventory.isKeyed()) {
            throw new Error("not keyed");
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder keysb = new StringBuilder();
        do {
            String key = input.nextKey();
            if(key.equals("ESCAPE")) {
                _sels.clear();
                throw new ActionCancelledException();
            }
            else if(" ".equals(key)) {
                if(hasNext()) {
                    next();
                }
                else {
                    _i = 0;
                    next();
                }
            }
            else if(key.equals("ENTER")||key.equals(" ")) {
                break;
            }
            else {
                Item i = _inventory.itemFor(key);
                if(i!=null) {
                    final BLabel b = _labels.get(i);
                    if(b==null) {
                        continue;
                    }
                    sb.setLength(0);
                    keysb.setLength(0);
                    if(_sels.contains(i)) {
                        _sels.remove(i);
                        createText(i, keysb, sb, " - ");
                    }
                    else {
                        _sels.add(i);
                        createText(i, keysb, sb, " + ");
                    }
                    final String s = sb.toString();
                    final String ks = keysb.toString();
                    EventQueue.getEventQueue().postback(new AbstractGameAction() {
                        public void perform() {
                            b.setSize(getWidth(), b.getHeight());
                            b.setText(ks);
                        }
                    });
                }
            }
        } while(max==-1||_sels.size()<max);
        _inventory.setKeyed(okeyed);
        Item[] ret = (Item[]) _sels.toArray(new Item[_sels.size()]);
        _sels.clear();
        return ret;
    }

    protected void createText(Item i, StringBuilder skey, StringBuilder text, String sep) {
        if(_showKeys&&_inventory.isKeyed()) {
            String key = _inventory.keyFor(i);
            //text.append(key);
            //text.append(sep);
            skey.append(key);
            skey.append(sep);
        }
        if(Grammar.pov().isBlind()) {
            text.append(i.toObscureString());
        }
        else {
            text.append(i.toString());
        }
    }

    public static ItemFilter and(final ItemFilter f1, final ItemFilter f2) {
        return new ItemFilter() {
            public boolean accept(Item item, NHBot bot) {
                return (f1==null||f1.accept(item, bot)) && (f2==null||f2.accept(item, bot));
            }
        };
    }
}
