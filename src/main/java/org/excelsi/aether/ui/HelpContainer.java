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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;

import com.jmex.bui.*;
import com.jmex.bui.background.*;

import com.jmex.bui.layout.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.jme.system.DisplaySystem;
import java.util.Collections;


public class HelpContainer extends BContainer {
    private InputSource _input;
    //private Iterator<String> _keys;
    private Iterator<Key> _keys;
    private int _h = DisplaySystem.getDisplaySystem().getHeight();


    public HelpContainer(InputSource input) {
        super();
        _input = input;
        VGroupLayout mgr = new VGroupLayout();
        mgr.setJustification(VGroupLayout.LEFT);
        mgr.setOffAxisJustification(VGroupLayout.LEFT);
        setLayoutManager(mgr);
        ArrayList<Key> keys = new ArrayList<Key>();
        for(String key:Universe.getUniverse().getKeymap().keySet()) {
            String v = Universe.getUniverse().getKeymap().get(key);
            if(v.startsWith("-")) {
                continue;
            }

            String k = key;
            if(k.length()>1&&!k.startsWith("C-")) {
                k = k.charAt(0) + k.substring(1).toLowerCase();
            }
            //try {
                //v = Class.forName(v).newInstance().toString();
            //}
            //catch(Exception e) {
                //v = Character.toUpperCase(v.charAt(0)) + v.substring(1);
            //}
            GameAction a = _input.actionFor(key);
            if(a instanceof Extended) {
                continue;
            }
            Key kk = new Key();
            kk.k = k;
            kk.a = a.toString();
            kk.d = a.getDescription();
            keys.add(kk);
        }
        for(Map.Entry<String,GameAction> e:Extended.getCommands().entrySet()) {
            if(e.getKey().startsWith("_")) {
                continue;
            }
            Key kk = new Key();
            kk.k = '#'+e.getKey();
            //kk.a = Character.toUpperCase(e.getKey().charAt(0))+e.getKey().substring(1);
            kk.a = e.getValue().toString();
            kk.d = e.getValue().getDescription();
            keys.add(kk);
        }
        Collections.sort(keys);
        _keys = keys.iterator();
    }

    public boolean hasNext() {
        return _keys.hasNext();
    }

    public void next() {
        if(!_keys.hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        else {
            load();
        }
    }

    public void load() {
        //long start= System.currentTimeMillis();
        removeAll();
        BLabel header = new BLabel("- Help -", "header");
        add(header);
        BContainer top = null;
        int times = 1;
        if(DisplaySystem.getDisplaySystem().getWidth()>=1024) {
            top = new BContainer();
            top.setLayoutManager(new TableLayout(2, 2, 10));
            add(top);
            times = 2;
        }
        for(int i=0;i<times;i++) {
            BContainer bc = new BContainer();
            TableLayout mgr = new TableLayout(3, 2, 10);
            bc.setLayoutManager(mgr);
            //for(Map.Entry<String,String> e:Universe.getUniverse().getKeymap().entrySet()) {
            int height = 0;
            while(_keys.hasNext()&&height<_h-64) {
                //validate();
                //System.err.println("height="+getHeight());
                height += 24;
                BContainer p = new BContainer(new BorderLayout());
                Key kk = _keys.next();
                BLabel name = new BLabel(kk.k, "keyaction");
                BLabel value = new BLabel(kk.a, "keyaction");
                BLabel desc = new BLabel(kk.d, "desc");
                bc.add(name);
                bc.add(value);
                bc.add(desc);
            }
            if(top!=null) {
                top.add(bc);
            }
            else {
                if(_keys.hasNext()) {
                    bc.add(new BLabel("- More -"));
                }
                add(bc);
            }
        }
        if(_keys.hasNext()) {
            if(top!=null) {
                top.add(new BLabel("- More -"));
            }
        }
        //long end= System.currentTimeMillis();
        //System.out.println("HelpContainer.load(): "+(end-start));
    }

    private static class Key implements Comparable {
        public String k;
        public String a;
        public String d;

        public int compareTo(Object o) {
            return d.compareTo(((Key)o).d);
        }
    }
}
