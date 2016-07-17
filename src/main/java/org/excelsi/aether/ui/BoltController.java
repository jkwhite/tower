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
package org.excelsi.aether.ui;


import com.jme.scene.Node;


public class BoltController extends FixedTimeController implements StoppableController {
    private Node _n;
    private float _delay;
    private boolean _remOnDone;


    public BoltController(Node n, float delay) {
        this(n, delay, true);
    }

    public BoltController(Node n, float delay, boolean removeOnDone) {
        super(new FixedTimeController.Modulator[]{
            new SizeModulator(n, SizeModulator.Z)}, FixedTimeController.CONSTANT, 0.2f, delay);
        _n = n;
        _delay = delay;
        _remOnDone = removeOnDone;
    }

    public void stop() {
        if(isActive()) {
            _remOnDone = true;
        }
        else {
            remOnDone();
        }
    }

    protected void done() {
        if(_remOnDone) {
            remOnDone();
        }
    }

    protected void remOnDone() {
        _n.removeController(this);
        _n.addController(new FixedTimeController(new FixedTimeController.Modulator[]{
            new SizeModulator(_n, SizeModulator.Z, 1f, 0f)}, FixedTimeController.CONSTANT, 0.1f, _delay) {
            protected void done() {
                _n.getParent().detachChild(_n);
            }
        });
    }
}
