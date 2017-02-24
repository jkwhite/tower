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


import com.jme3.scene.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class SlotNode extends Node {
    private Map<String,SlotUI> _slots = null;
    private List<SlotUI> _lslots = null;

    public SlotNode(String name) {
        super(name);
    }

    public void addSlotUI(String s, SlotUI sui) {
        if(_slots==null) {
            _slots = new HashMap<String, SlotUI>(1);
            _lslots = new ArrayList<SlotUI>(1);
        }
        _slots.put(s, sui);
        _lslots.add(sui);
    }

    public void removeSlotUI(String s) {
        if(_slots==null) {
            return;
        }
        SlotUI sui = _slots.remove(s);
        if(sui!=null) {
            _lslots.remove(sui);
        }
    }

    public void onMove() {
        if(_lslots!=null) {
            for(int i=0;i<_lslots.size();i++) {
                _lslots.get(i).onMove();
            }
        }
    }
}
