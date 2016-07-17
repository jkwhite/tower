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
import org.excelsi.matrix.*;
import static org.excelsi.aether.Brain.Chemical;
import java.util.ArrayList;
import java.util.List;


public class TNPC extends NPC implements Elemental {
    private List<Element> _elements;
    //private Chemical _hungerChem = new Chemical("hunger");


    public TNPC() {
        setVision(8);
        setNightvision(2);
    }

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

    public boolean canOccupy(NHSpace s) {
        boolean ret;
        if(s.getStatus()==Status.blessed&&!canOccupyHoly()) {
            return false;
        }
        switch(getForm().getHabitat()) {
            case aquatic:
                ret = s instanceof Water;
                break;
            case terrestrial:
                if(s instanceof Water) {
                    if(isLevitating()) {
                        ret = true;
                    }
                    else if(getSize()!=Size.small&&getSize()!=Size.tiny) {
                        ret = true;
                    }
                    else if(((Water)s).getDepth()<=4) {
                        ret = true;
                    }
                    else {
                        ret = false;
                    }
                }
                else {
                    ret = true;
                }
                break;
            case hydrophobic:
                ret = !(s instanceof Water);
                break;
            case airborn:
            default:
                ret = true;
        }
        return ret && super.canOccupy(s);
    }

    public String toPack() {
        String c = getCommon();
        String p = "pack";
        if("pirahna".equals(c)) {
            p = "school";
        }
        else if("bat".equals(c)) {
            p = "colony";
        }
        else if(c.endsWith("snake")||c.endsWith("viper")) {
            p = "nest";
        }
        else if(c.endsWith("dragon")) {
            p = "weyr";
        }
        else if(c.endsWith("kitten")) {
            p = "litter";
        }
        else if("penguin".equals(c)) {
            p = "parcel";
        }
        return p+" of "+Grammar.pluralize(c);
    }

    protected Affinity getAffinityEvaluator() {
        return new Affinity() {
            public float evaluate(MSpace from, MSpace to) {
                float af;
                if((from instanceof Pit&&!(to instanceof Pit))
                    ||(!(from instanceof Pit)&&to instanceof Pit)&&!isAirborn()) {
                    return 10f;
                }
                else if(from.isCardinalTo(to)) {
                    af = 1f;
                }
                else {
                    af = 1.5f;
                }
                if(to instanceof Water && ((Water)to).getDepth()>4) {
                    af *= 2f;
                }
                return af;
            }
        };
    }

    protected boolean canOccupyHoly() {
        return false;
    }
}
