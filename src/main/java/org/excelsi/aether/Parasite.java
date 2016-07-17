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


import org.excelsi.matrix.*;


/**
 * Parasites can be attached to spaces to represent objects
 * permanently associated with the space. This is in contrast
 * to items, which only temporarily occupy spaces. A space
 * may have any number of parasites.
 * <p/>
 * Distinct types of spaces should not be implemented as parasites;
 * they should instead extend <code>Floor</code> or <code>DefaultNHSpace</code>.
 * A parasite should be something that can exist on different types
 * of spaces. For example, writing and traps are ideally implemented
 * as parasites, because they have no dependency to the type of space
 * on which they exist.
 */
public abstract class Parasite implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /** whether or not this parasite is hidden */
    private boolean _hidden;
    /** space to which this parasite is attached */
    private NHSpace _space;


    /**
     * Constructs a new, unhidden parasite.
     */
    public Parasite() {
        this(false);
    }

    /**
     * Constructs a new parasite.
     *
     * @param hidden whether or not this parasite is hidden
     */
    public Parasite(boolean hidden) {
        _hidden = hidden;
    }

    /**
     * Tests if this parasite is hidden. If hidden,
     * bots do not know the parasite is present.
     */
    public final boolean isHidden() {
        return _hidden;
    }

    /**
     * Sets whether or not this parasite is hidden.
     * When unhiding a parasite, keep in mind that
     * the unhide is global. Therefore you will
     * probably only want to unhide if the parasite
     * is active within the player's POV (otherwise
     * how could the player know the parasite exists?).
     *
     * @param hidden whether or not this parasite is hidden
     */
    public final void setHidden(boolean hidden) {
        if(hidden!=_hidden) {
            boolean oh = _hidden;
            _hidden = hidden;
            if(_space!=null) {
                for(MSpaceListener l:_space.getMSpaceListeners()) {
                    if(l instanceof NHSpaceListener) {
                        ((NHSpaceListener)l).parasiteAttributeChanged(_space, this, "hidden", oh, _hidden);
                    }
                }
            }
        }
    }

    /**
     * Sets the space to which this parasite is attached.
     * If this parasite is already attached to a different
     * space, the parasite will be removed from the old
     * space before being attached to the new space.
     *
     * @param s space to which this parasite will be attached
     */
    public final void setSpace(NHSpace s) {
        if(_space!=null) {
            _space.removeParasite(this);
        }
        _space = s;
    }

    /**
     * Gets the space to which this parasite is attached.
     *
     * @return space
     */
    public final NHSpace getSpace() {
        return _space;
    }

    /**
     * Gets the size of this parasite. By default,
     * returns Size.medium.
     *
     * @return parasite size
     */
    public Size getSize() {
        return Size.medium;
    }

    /**
     * Called when a bot triggers this parasite. This
     * happens when a bot enters the space to which
     * this parasite it attached.
     *
     * @param b bot which triggered this parasite
     */
    abstract public void trigger(NHBot b);

    /**
     * Called when a bot has a chance of noticing this
     * parasite. Note that the bot may not necessarily
     * occupy the space to which this parasite is
     * attached (it may be looking from afar).
     *
     * @param b bot who may notice this parasite
     * @return <code>true</code> iff the bot noticed this parasite
     */
    abstract public boolean notice(NHBot b);

    abstract public void update();
    abstract public String getModel();
    abstract public String getColor();
    abstract public int getHeight();
    abstract public boolean isMoveable();
    abstract public void attacked(Armament a);

    public boolean canLeave(MSpace to) {
        return true;
    }

    protected void notifyAttr(String attr, Object oldValue, Object newValue) {
        if(_space!=null) {
            for(MSpaceListener l:_space.getMSpaceListeners()) {
                if(l instanceof NHSpaceListener) {
                    ((NHSpaceListener)l).parasiteAttributeChanged(_space, this, attr, oldValue, newValue);
                }
            }
        }
    }
}
