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


public abstract class Infliction extends AbstractFragment {
    private float _dosage = 1f;
    private Status _status = Status.uncursed;
    private boolean _permanent;


    public void setDosage(float dosage) {
        _dosage = dosage;
    }

    public float getDosage() {
        return _dosage;
    }

    public void setPermanent(boolean permanent) {
        _permanent = permanent;
    }

    public boolean isPermanent() {
        return _permanent;
    }

    public void setStatus(Status s) {
        _status = s;
    }

    public Status getStatus() {
        return _status;
    }

    /**
     * Inflicts this infliction on a bot.
     *
     * @param b bot receiving this infliction
     * @return <code>false</code> if this infliction is consumed
     */
    abstract public boolean inflict(NHBot b);

    /**
     * Inflicts this infliction on a space.
     *
     * @param s space receiving this infliction
     * @return <code>false</code> if this infliction is consumed
     */
    abstract public boolean inflict(NHSpace s);

    /**
     * Applies this infliction to an item.
     *
     * @param i item to which to apply this infliction
     * @return <code>false</code> if this infliction is consumed
     */
    public boolean apply(Item i, NHBot b) {
        if(!i.hasFragment(this)) {
            i.addFragment(this);
            return true;
        }
        return false;
    }

    public boolean isReplaceable() {
        return false;
    }

    public Infliction deepCopy() {
        return (Infliction) super.deepCopy();
    }

    protected final int dose(int amt) {
        int d = (int) Math.floor(_dosage*amt);
        if(d==0&&amt==1) {
            return Rand.om.nextFloat()<_dosage?amt:d;
        }
        return d;
    }
}
