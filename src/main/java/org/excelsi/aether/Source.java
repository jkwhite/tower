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


public final class Source extends org.excelsi.matrix.MSource {
    public static final long serialVersionUID = 1L;
    private Item _i;


    public Source(NHBot b) {
        super(b);
    }

    public Source(Item i) {
        super((String)null);
        _i = i;
    }

    public Source(String s) {
        super(s);
    }

    public Source(NHBot b, Item i) {
        super(b);
        _i = i;
    }

    public NHBot bot() {
        return (NHBot) super.bot();
    }

    public Item item() {
        return _i;
    }

    public String toString() {
        if(string()!=null) {
            return string();
        }
        if(bot()!=null) {
            if(_i!=null) {
                return _i.getName();
            }
            return Grammar.noun(bot());
        }
        return _i.getName();
    }

    public String toString(String verb) {
        if(string()!=null) {
            return string()+" "+Grammar.conjugate(null, verb);
        }
        if(bot()!=null) {
            if(_i!=null) {
                //return Grammar.possessive(_b, _i)+" "+Grammar.conjugate(null, verb);
                return Grammar.possessive(bot())+" "+_i.getName()+" "+Grammar.conjugate(null, verb);
            }
            else {
                return Grammar.noun(bot())+" "+Grammar.conjugate(bot(), verb);
            }
        }
        return Grammar.noun(_i)+" "+Grammar.conjugate(null, verb);
    }
}
