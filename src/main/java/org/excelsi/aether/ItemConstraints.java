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


public class ItemConstraints implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private ItemFilter _filter;
    private boolean _acceptMulti;
    private boolean _acceptNull;
    private boolean _allowSplit;
    private String _message;
    private String _failMessage;
    private Inventory _container;


    public ItemConstraints(Inventory c, String verb) {
        this(c, "What do you want to "+verb+"?", "You can't "+verb+" that.");
    }

    public ItemConstraints(Inventory c, String message, String failMessage) {
        this(c, message, failMessage, null);
    }

    public ItemConstraints(Inventory c, String verb, ItemFilter filter) {
        this(c, verb, filter, false);
    }

    public ItemConstraints(Inventory c, String verb, ItemFilter filter, boolean acceptNull) {
        this(c, verb);
        _filter = filter;
        _acceptNull = acceptNull;
    }

    public ItemConstraints(Inventory c, String message, String failMessage, ItemFilter filter) {
        _message = message;
        _failMessage = failMessage;
        _filter = filter;
        _container = c;
        if(_filter==null) {
            _filter = new ItemFilter() { public boolean accept(Item i, NHBot bot) { return true; } };
        }
    }

    public Inventory getContainer() {
        return _container;
    }

    public boolean isAcceptNull() {
        return _acceptNull;
    }

    public boolean isAcceptMulti() {
        return _acceptMulti;
    }

    public String getMessage() {
        return _message;
    }

    public String getFailMessage() {
        return _failMessage;
    }

    public ItemFilter getFilter() {
        return _filter;
    }
}
