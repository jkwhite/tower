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


public class No51Corpse extends Corpse {
    public String getColor() { return "yellow"; }
    public int getFindRate() { return 100; }
    public float getSize() { return 10; }
    public float getDecayRate() { return 0f; }

    public String getCombustionPhrase() { return "chars"; }

    public void combust(Container c) {
        c.consume(this);
        /*
         * TODO: finish alchemical process. cut straight
         * to disk for now.
        String name = getName();
        if(name.startsWith("corpse of ")) {
            name = name.substring("corpse of ".length());
        }
        else {
            name = name.substring(0, name.lastIndexOf(" corpse"));
        }
        Skeleton sk = new Skeleton();
        sk.addFragment(new Charred());
        sk.setStatus(getStatus());
        sk.setName(name+" skeleton");
        for(Fragment f:getFragments()) {
            sk.addFragment((Fragment)DefaultNHBot.deepCopy(f));
        }
        c.add(sk);
        */
        c.add(new DiskOfOdin());
    }
}
