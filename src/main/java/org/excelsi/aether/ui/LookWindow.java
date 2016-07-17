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


import com.jmex.bui.*;
import com.jmex.bui.background.*;
import com.jmex.bui.layout.BorderLayout;
import com.jme.renderer.ColorRGBA;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class LookWindow extends BWindow {
    private InventoryFrame _loot;


    public LookWindow(BStyleSheet s) {
        super(s, new BorderLayout());
        _loot = new InventoryFrame(new Inventory());
        //_loot.setBackground(new TintedBackground(new ColorRGBA(0f, 0f, 0f, 0.8f)));
        add(_loot, BorderLayout.NORTH);
    }

    public void setLoot(Inventory loot) {
        _loot.setInventory(loot);
    }

    public InventoryFrame getInventory() {
        return _loot;
    }
}
