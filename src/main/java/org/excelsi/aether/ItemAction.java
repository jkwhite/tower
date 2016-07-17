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
 * ItemAction is useful for implementing item invocation actions
 * that are shareable by players and NPCs.
 */
public abstract class ItemAction extends DefaultNHBotAction {
    private Item _item;
    private Item _origItem;
    private String _verb;
    private ItemFilter _filter;
    private boolean _acceptNull;
    private boolean _remove;
    private boolean _prompt = true;


    public ItemAction(String verb, Item i) {
        _verb = verb;
        _item = i;
        _origItem = Item.copy(_item);
        _prompt = i!=null;
    }

    public ItemAction(String verb, ItemFilter filter) {
        this(verb, filter, false);
    }

    public ItemAction(String verb, ItemFilter filter, boolean acceptNull) {
        this(verb, filter, acceptNull, false);
    }

    public ItemAction(String verb, ItemFilter filter, boolean acceptNull, boolean remove) {
        _verb = verb;
        _filter = filter;
        _acceptNull = acceptNull;
        _remove = remove;
        if(_filter==null) {
            _filter = new ItemFilter() { public boolean accept(Item i, NHBot bot) { return true; } };
        }
    }

    public void init() {
        super.init();
        setItem(null);
    }

    public boolean accepts(Item i, NHBot b) {
        return _filter!=null?_filter.accept(i, b):false;
    }

    /**
     * Prompts for item and performs action if bot is human, otherwise performs action.
     */
    public final void perform() {
        if(getBot().isDead()) {
            throw new IllegalStateException(getBot()+" is dead");
        }
        if(!useSpace()) {
            if(_prompt&&getBot().isPlayer()&&getItem()==null) {
                setItem(N.narrative().choose(getBot(), new ItemConstraints(getBot().getInventory(), _verb, _filter, _acceptNull), _remove));
            }
            else {
                if(_remove) {
                    getBot().getInventory().remove(getItem());
                }
            }
            try {
                act();
                if(this instanceof InstantaneousAction) {
                    for(EnvironmentListener el:getBot().getListeners()) {
                        if(el instanceof NHEnvironmentListener) {
                            ((NHEnvironmentListener)el).actionPerformed(getBot(), (InstantaneousAction) this);
                        }
                    }
                }
            }
            finally {
                //TODO: figure out a way to make this thread-safe
                //setItem(null);
            }
        }
    }

    public final void setItem(Item item) {
        _item = item;
        if(_item!=null) {
            // TODO: ugh, fix this
            // copy fails if the item retains a reference to the bot
            // wearing/wielding it, because that involved an EventQueue
            // deserialization, which is prevented because it would
            // mess everything up.
            try {
                _origItem = Item.copy(_item);
            }
            catch(Throwable t) {
                _origItem = item;
            }
        }
    }

    public final Item getItem() {
        return _item;
    }

    public final Item getOriginalItem() {
        return _origItem;
    }

    public final String getVerb() {
        return _verb;
    }

    /** 
     * Performs action.
     */
    abstract protected void act();

    /**
     * Some actions may optionally operate on the current space instead of
     * on an item. This method will be invoked before the player is prompted
     * for an item. If it returns true, no further action will be taken.
     *
     * @return <code>true</code> if some action on the space was taken
     */
    protected boolean useSpace() {
        return false;
    }
}
