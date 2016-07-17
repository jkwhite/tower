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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


public class Switch extends Floor implements Device {
    static interface Toggle extends java.io.Serializable {
        void toggle(boolean on);
    }

    private static final long serialVersionUID = 1L;


    private String _label;
    private String _adj;
    private Toggle _effect;
    private boolean _on;


    public Switch(String label, String adj, Toggle effect) {
        _label = label;
        _adj = adj==null||adj.trim().length()==0?"switch":adj+" switch";
        _effect = effect;
    }

    public String getName() {
        return _adj;
    }

    public String getModel() {
        return _on?"abs":"a/";
    }

    public String getColor() {
        return "white";
    }

    public boolean look(NHBot b, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(b, nothing, lootOnly);
        if(!lootOnly) {
            N.narrative().print(b, "There is a "+_adj+" switch here, labeled '"+_label+"'.");
            return true;
        }
        return ret;
    }

    public void use(NHBot b) {
        if(_on) {
            N.narrative().print(b, Grammar.start(b, "cut")+" the switch.");
        }
        else {
            N.narrative().print(b, Grammar.start(b, "pull")+" the switch.");
            //N.narrative().more();
            N.narrative().print(b, "The circuit sparks to life!");
        }
        String oldm = getModel();
        _on=!_on;
        _effect.toggle(_on);
        notifyAttr("model", oldm, getModel());
    }
}
