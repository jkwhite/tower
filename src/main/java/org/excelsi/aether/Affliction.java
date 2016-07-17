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
 * Affliction represents a change to a bot that
 * is beyond the bot's control (e.g., confusion or hallucination).
 */
public abstract class Affliction implements java.io.Serializable, Excuse {
    private static final long serialVersionUID = 1L;
    public enum Onset { tick, move };
    private String _name;
    private NHBot _bot;
    private Onset _onset;
    private boolean _stuck;


    public Affliction(String name, Onset onset) {
        this(name, onset, false);
    }

    public Affliction(String name, Onset onset, boolean stuck) {
        if(name==null) {
            throw new IllegalArgumentException("name must be non-null");
        }
        if(onset==null) {
            throw new IllegalArgumentException("onset must be non-null");
        }
        _name = name;
        _onset = onset;
        _stuck = stuck;
    }

    public final String getName() {
        return _name;
    }

    public final Onset getOnset() {
        return _onset;
    }

    public void setBot(NHBot bot) {
        _bot = bot;
    }

    public NHBot getBot() {
        return _bot;
    }

    public final Narrative getNarrative() {
        return N.narrative();
    }

    public boolean equals(Object o) {
        return getName().equals(((Affliction)o).getName());
    }

    public final int hashCode() {
        return getName().hashCode();
    }

    public abstract void beset();

    public void tick() {
    }

    public boolean allow(NHBotAction a) {
        return true;
    }

    public boolean isStuck() {
        return _stuck;
    }

    public void setStuck(boolean stuck) {
        _stuck = stuck;
    }

    public abstract String getStatus();

    public abstract void compound(Affliction a);

    public String toString() {
        return getName()+" ("+_onset+")";
    }
}
