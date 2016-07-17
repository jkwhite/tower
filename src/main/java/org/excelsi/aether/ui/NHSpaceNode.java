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


import com.jme.scene.Node;
import com.jme.scene.Spatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.excelsi.aether.Parasite;


public class NHSpaceNode extends Node {
    private List<Spatial> _items;
    private Map<Parasite,Spatial> _parasites;


    public NHSpaceNode(String name) {
        super(name);
    }

    public void attachItem(Spatial item) {
        if(_items==null) {
            _items = new ArrayList<Spatial>(2);
        }
        _items.add(item);
        attachChild(item);
    }

    public Spatial getItem(int idx) {
        return _items.get(idx);
    }

    public List<Spatial> getItems() {
        return _items;
    }

    public Spatial removeItem(int idx) {
        if(_items!=null&&_items.size()>idx) {
            Spatial s = _items.remove(idx);
            if(_items.size()==0) {
                _items = null;
            }
            return s;
        }
        return null;
    }

    public void attachParasite(Parasite p, Spatial s) {
        if(_parasites==null) {
            _parasites = new HashMap<Parasite, Spatial>(1);
        }
        _parasites.put(p,s);
        attachChild(s);
    }

    public Spatial removeParasite(Parasite p) {
        if(_parasites==null) {
            return null;
        }
        Spatial s = _parasites.remove(p);
        if(_parasites.size()==0) {
            _parasites = null;
        }
        return s;
    }

    public Spatial getParasite(Parasite p) {
        if(_parasites==null) {
            return null;
        }
        return _parasites.get(p);
    }
}
