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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.EnumSet;


public class Corpses implements Mixin {
    public boolean match(Class c) {
        return NHBot.class.isAssignableFrom(c);
    }

    public void mix(Object o) {
        ((NHBot)o).addListener(new EnvironmentAdapter() {
            public void died(Bot b, MSource s) {
                NHBot nb = (NHBot) b;
                if(nb.getForm() instanceof Mortal) {
                    Corpse corpse = ((Mortal)nb.getForm()).toCorpse();
                    if(corpse!=null) {
                        corpse.randomize();
                        if(Rand.d100(corpse.getFindRate())) {
                            if(nb.getName()!=null) {
                                corpse.setName("corpse of "+nb.getName());
                            }
                            else {
                                corpse.setName(nb.getCommon()+" corpse");
                            }
                            corpse.setSpirit(nb);
                            nb.getEnvironment().getMSpace().add(corpse);
                        }
                    }
                }
            }
        });
    }
}
