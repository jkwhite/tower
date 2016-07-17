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


import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.system.*;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;

import com.jmex.bui.*;
import com.jmex.bui.background.*;

import com.jmex.bui.layout.*;
import java.util.*;


public class KeyedSelector extends BContainer {
    private Object[] _elements;
    private String _heading;
    private Set _selected = new HashSet();
    private Map<Object,BLabel> _labels = new HashMap<Object,BLabel>();
    private Map<String,Object> _keys = new HashMap<String,Object>();
    private String[] _initKeys;
    private boolean _pluralize = true;


    public KeyedSelector() {
        this(new Object[0]);
    }

    public KeyedSelector(Object[] elements) {
        this(elements, null);
    }

    public KeyedSelector(Object[] elements, String[] keys) {
        this(null, elements, keys);
    }

    public KeyedSelector(String heading, Object[] elements, String[] keys) {
        this(heading, elements, keys, VGroupLayout.LEFT);
    }

    public KeyedSelector(String heading, Object[] elements, String[] keys, VGroupLayout.Justification layout) {
        VGroupLayout mgr = new VGroupLayout();
        mgr.setJustification(layout);
        mgr.setOffAxisJustification(layout);
        setLayoutManager(mgr);
        _heading = heading;
        _elements = elements;
        _initKeys = keys;
    }

    public void setPluralize(boolean p) {
        _pluralize = p;
    }

    public boolean getPluralize() {
        return _pluralize;
    }

    public void setElements(String heading, Object[] elements) {
        setElements(heading, elements, null);
    }

    public void setElements(String heading, Object[] elements, String[] keys) {
        _heading = heading;
        _elements = elements;
        _initKeys = keys;
        refresh();
    }

    public void refresh() {
        if(HUD.isUIThread()) {
            removeAll();
            _labels.clear();
            _selected.clear();
            _keys.clear();
            //long before = System.currentTimeMillis();
            BContainer parent = getParent();
            if(parent!=null) {
                parent.remove(this);
            }
            if(_heading!=null) {
                BLabel header = new BLabel("- "+Grammar.first(Grammar.pluralize(_heading))+" -");
                header.setStyleClass("header");
                add(header);
            }
            for(int i=0;i<_elements.length;i++) {
                Object o = _elements[i];
                String key = null;
                if(_initKeys!=null) {
                    key = _initKeys[i];
                    if(key!=null&&key.length()>1) {
                        key = null;
                    }
                }
                if(key==null) {
                    int ki=0;
                    do {
                        key = new String(new char[]{Inventory.KEYS.charAt(ki++)});
                    } while(_keys.containsKey(key));
                }
                BContainer line = new BContainer(new BorderLayout());
                BLabel lkey = new BLabel(key+" - ", "keylabel");
                line.add(lkey, BorderLayout.WEST);
                BLabel litem = new BLabel(o.toString());
                line.add(litem, BorderLayout.CENTER);

                //BLabel it = new BLabel(key+" - "+o.toString(), "keylabel");
                //add(it);
                add(line);

                //_labels.put(o, it);
                _labels.put(o, lkey);
                _keys.put(key, o);
            }
            if(parent!=null) {
                //invalidate();
                parent.add(this, BorderLayout.NORTH);
                //wasAdded();
            }
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    refresh();
                }
            });
        }
    }

    public Object[] choose(KeySource input, int max) {
        List<Item> sels = new ArrayList<Item>();
        do {
            String key = input.nextKey();
            if(key.equals("ESCAPE")) {
                throw new ActionCancelledException();
            }
            else if(key.equals("ENTER")||key.equals(" ")) {
                break;
            }
            else {
                Object i = _keys.get(key);
                if(i!=null) {
                    final BLabel b = _labels.get(i);
                    if(b==null) {
                        continue;
                    }
                    String sb = null;
                    if(_selected.contains(i)) {
                        _selected.remove(i);
                        //sb = key+" - "+i.toString();
                        sb = key+" - ";
                    }
                    else {
                        _selected.add(i);
                        //sb = key+" + "+i.toString();
                        sb = key+" + ";
                    }
                    if(max!=1) {
                        final String s = sb;
                        EventQueue.getEventQueue().postback(new AbstractGameAction() {
                            public void perform() {
                                b.setSize(getWidth(), b.getHeight());
                                b.setText(s);
                            }
                        });
                    }
                }
            }
        } while(max==-1||_selected.size()<max);
        Object[] selected = _selected.toArray();
        // ensure that KeyedSelector holds no long-term references
        // to game data
        //_elements = null;
        _selected.clear();
        _labels.clear();
        _keys.clear();
        return selected;
    }
}
