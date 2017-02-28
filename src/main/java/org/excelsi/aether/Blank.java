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


public class Blank extends DefaultNHSpace implements Breakable {
    private static final long serialVersionUID = 1L;
    private boolean _replaceable;
    private NHSpaceMutator _breakupAction;


    public Blank() {
        this(true);
    }

    public Blank(boolean replaceable) {
        super("gray");
        _replaceable = replaceable;
    }

    public boolean isReplaceable() {
        return _replaceable;
    }

    @Override public boolean isNull() {
        return true;
    }

    public MatrixMSpace union(MatrixMSpace m) {
        return m;
    }

    public String getModel() {
        return "";
    }

    public String getColor() {
        return "gray";
    }

    public boolean isWalkable() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    @Override public boolean breakup() {
        if(_breakupAction!=null) {
            return _breakupAction.mutate(this);
        }
        else {
            return false;
        }
    }

    public void setBreakupAction(NHSpaceMutator m) {
        _breakupAction = m;
    }

    public NHSpaceMutator getBreakupAction() {
        return _breakupAction;
    }
}
