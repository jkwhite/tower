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
package org.excelsi.aether;


/** 
 * ItemAction is useful for implementing item invocation actions
 * that are shareable by players and NPCs.
 */
public abstract class InvokeAction extends ItemAction {
    private boolean _narrate;


    public InvokeAction(String verb, ItemFilter filter) {
        this(verb, filter, false);
    }

    public InvokeAction(String verb, ItemFilter filter, boolean acceptNull) {
        this(verb, filter, acceptNull, true);
    }

    public InvokeAction(String verb, ItemFilter filter, boolean acceptNull, boolean narrate) {
        super(verb, filter, acceptNull);
        _narrate = narrate;
    }

    public InvokeAction(String verb, Item i) {
        super(verb, i);
    }

    protected void act() {
        if(_narrate) {
            Context.c().n().print(getBot(), Grammar.start(getBot())+" "+Grammar.conjugate(getBot(), getVerb())+" "+
                Grammar.singular(getItem())+".");
        }
        if(getBot().isPlayer()) {
            getItem().setClassIdentified(true);
        }
        getItem().invoke(getBot());
    }
}
