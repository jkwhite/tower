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


//import org.ho.yaml.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.IOException;


public class YamlDataSource implements DataSource {
    public Universe populate(Universe u, Data d) {
        /*
        // need to split into two steps in order to
        // load singleton universe before loading
        // items and bots
        Universe uni = u;
        try {
            uni = Yaml.loadType(d.getResource("universe.yaml"), Universe.class);
            Universe.setUniverse(uni);
        }
        catch(Throwable e) {
            throw new Error("error reading universe.yaml", e);
        }
        try {
            for(InputStream is:d.getResources("items.yaml")) {
                try {
                    uni.addItems(Yaml.loadType(is, new ArrayList<Item>().getClass()));
                }
                catch(Throwable e) {
                    throw new Error("error reading items.yaml", e);
                }
            }
            for(InputStream is:d.getResources("bots.yaml")) {
                try {
                    uni.addBots(Yaml.loadType(is, new NHBot[0].getClass()));
                }
                catch(Throwable e) {
                    throw new Error("error reading bots.yaml", e);
                }
            }
        }
        catch(IOException e) {
            throw new Error(e);
        }
        //uni.print();
        //System.exit(0);
        return uni;
        */
        throw new UnsupportedOperationException();
    }
}
