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
 * Interface for multi-step actions (actions that
 * may span more than one round).
 */
public interface ProgressiveAction extends Excuse {
    /**
     * Performs subsequent steps of this action.
     *
     * @return <code>false</code> iff this action is complete
     */
    boolean iterate();

    /**
     * Gets the rate at which this action is interrupted when
     * combat against the performing bot is initiated.
     *
     * @return interrupt rate
     */
    int getInterruptRate();

    /**
     * Guaranteed to be invoked whenever this action
     * is stopped, whether by completing iteration or
     * through any other means.
     */
    void stopped();

    /**
     * Invoked only when this action is interrupted. This
     * method, if invoked, will always be called before
     * <code>stopped</code>.
     */
    void interrupted();
}
