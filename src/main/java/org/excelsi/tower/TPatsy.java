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


import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;


public class TPatsy extends Patsy implements Elemental {
    private List<Element> _elements;


    public List<Element> getElements() {
        return _elements!=null?_elements:new ArrayList<Element>(0);
    }

    public void setElements(List<Element> e) {
        List temp = e;
        ArrayList ar = new ArrayList<Element>(e.size());
        for(int i=0;i<temp.size();i++) {
            Object o = temp.get(i);
            ar.add(Enum.valueOf(Element.class, o.toString()));
        }
        _elements = ar;
    }

    public void setMaxLevel(int level) {
        if(level<900) {
            super.setMaxLevel(level);
        }
    }
}
