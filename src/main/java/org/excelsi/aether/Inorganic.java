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


import java.util.EnumSet;


/**
 * Inorganic is a "fake" bot that can represent
 * non-intelligent actions against another bot.
 * For example, a trap that attacks a bot can be
 * represented as an Inorganic for the purpose
 * of resolving combat.
 * <p/>
 * By default, inorganics have a stat value of
 * <code>50</code> for all stats.
 */
public class Inorganic extends DefaultNHBot {
    /**
     * Constructs a new Inorganic with all stats
     * at 50.
     *
     * @param name name of this bot
     */
    public Inorganic(String name) {
        setCommon(name);
        for(Stat s:EnumSet.allOf(Stat.class)) {
            this.setStat(s, 50);
        }
    }

    /**
     * Throws IllegalStateException.
     */
    public void act() {
        throw new IllegalStateException("inorganics cannot act");
    }

    /**
     * Throws IllegalStateException.
     */
    public void act(final Context c) {
        throw new IllegalStateException("inorganics cannot act");
    }

    /**
     * Throws IllegalStateException.
     */
    public void setEventSource(EventSource e) {
        throw new IllegalStateException("inorganics cannot process events");
    }
}
