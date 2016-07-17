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
import java.util.Collections;
import java.util.Arrays;
import java.util.List;


public class Store {
    private String[] _names;
    private ItemFilter _filter;
    private static final String[] SUFFIX = new String[]{"Emporium", "Superstore", "Megamart", "Outlet", "Minimall"};
    private static String[] NAMES = new String[]{"Relcock Eyeplot",
        "Tangor Barm", "Whistlin' Joe", "Dramanchtrie", "Elgor Yttriorn", /*"Lai Ye-Yum",*/
        "Ul Thu", "Varn Lenticle", "Wainscot Jim", "Marlow", "Curia Hellion",
        "Lochinewti", "Yegg Worthier", "Ibotot", "Ting", "Tonhop", "Sgg'th'pul",
        "Nacarlnacnor", "Ogopon", "Telluri De", "Lenticular Joe Surly", "Fresno Coalinga",
        "Zyrzephia", "Bort", "Rastrolnitrops", "Creor Dulmoth", "Shafter Wascoe",
        "Fierie Trajeckt", "Chrae Maraman", "Ghim Tiolind", "Kilgore",
        "Azimordon", "Burlaxor", "Celli Allided", "Buttonwillow McKittrick"};
    private static int _next = 0;
    static {
        List<String> names = Arrays.asList(NAMES);
        Collections.shuffle(names);
        NAMES = names.toArray(new String[0]);
    }


    public Store(ItemFilter sfilter, String... snames) {
        _filter = sfilter;
        _names = snames;
    }

    public String[] getNames() {
        return _names;
    }

    public String getKeeper() {
        return NAMES[Rand.om.nextInt(NAMES.length)];
    }

    public String createKeeper() {
        if(_next==NAMES.length) {
            _next = 0;
        }
        String name = NAMES[_next];
        _next++;
        return name;
    }

    public String getRandomName() {
        int i = Rand.om.nextInt(_names.length);
        if(i==0&&_names[0]==null) {
            i = 1;
        }
        if(i==0&&Rand.d100(33)) {
            return _names[0]+" "+SUFFIX[Rand.om.nextInt(SUFFIX.length)];
        }
        else {
            return _names[i];
        }
    }

    public ItemFilter getFilter() {
        return _filter;
    }

    public boolean hasKeeper() {
        return true;
    }

    public void modulate(Shops.Building b) {
    }
}
