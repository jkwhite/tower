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
package org.excelsi.tower.ui;


import com.jme.math.Vector3f;
import com.jme.scene.Node;
import org.excelsi.aether.ui.*;
import org.excelsi.tower.Unsteady;


public class UnsteadyNodeFactory extends DefaultNHSpaceNodeFactory {
    public Node createNode(String name, Object o, Node parent) {
        Node n = super.createNode(name, o, parent);
        Unsteady u = (Unsteady) o;
        //n.getLocalTranslation().y = -MatrixNode.STACK_HEIGHT*u.getDepth();
        //NHSpaceNode p = new NHSpaceNode("p");
        //p.attachChild(n);
        return n;
    }
}
