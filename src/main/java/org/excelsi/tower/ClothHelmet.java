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


public abstract class ClothHelmet extends Helmet implements Tearable {
    public String getSkill() {
        return CLOTH;
    }

    public void tear(NHBot b, Source s, Container c) {
        if(hasFragment(Shredded.NAME)) {
            N.narrative().printf(b, "%P falls apart!", this);
            c.destroy(this);
        }
        else if(hasFragment(Torn.NAME)) {
            removeFragment(Torn.NAME);
            N.narrative().printf(b, "%P %c!", this, "shreds");
            addFragment(new Shredded());
        }
        else {
            N.narrative().printf(b, "%P %c!", this, "tears");
            addFragment(new Torn());
        }
    }
}
