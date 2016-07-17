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


/**
 * Catch the sky and hold the stars for ransom.
 */
public class Draining extends Infliction {
    private String _item;


    static {
        Basis.claim(new Basis(Basis.Type.disks, 7));
    }

    public Draining() {
    }

    public void setItem(String item) {
        _item = item;
    }

    public GrammarType getPartOfSpeech() {
        return isClassIdentified()?GrammarType.nounPhrase:GrammarType.adjective;
    }

    public boolean inflict(NHBot b) {
        if(_item!=null) {
            for(Item i:b.getInventory().getItem()) {
                if(i.getClass().getName().equals(_item)) {
                    if(i.getCount()>1) {
                        N.narrative().printf(b, "Some of %P vanishes!", i);
                        if(i.getStackType()==Item.StackType.singular) {
                            int count = i.getCount();
                            int minus = Rand.om.nextInt(count)+1;
                            if(minus==count) {
                                b.getInventory().remove(i);
                            }
                            else {
                                i.setCount(i.getCount()-minus);
                            }
                        }
                        else {
                            b.getInventory().consume(i);
                        }
                    }
                    else {
                        N.narrative().printf(b, "%P vanishes!", i);
                        b.getInventory().consume(i);
                    }
                    break;
                }
            }
        }
        else {
            // do hp drain
        }
        return true;
    }

    public boolean inflict(NHSpace s) {
        return false;
    }
}
