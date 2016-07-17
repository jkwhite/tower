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
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jme.bounding.*;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class DefaultNHSpaceNodeFactory extends NodeFactory {
    public Node createNode(String name, Object o, Node parent) {
        NHSpace s = (NHSpace)o;
        Node n = loadModel(s.getModel(), s.getColor(), s.getShininess(), 0, true);
        //n.getLocalTranslation().y = MatrixNode.STACK_HEIGHT*s.getAltitude();
        NHSpaceNode p = new NHSpaceNode("p");
        p.attachChild(n);
        return p;
    }

    public void lock(Node n) {
        //n.lockTransforms();
        n.lockBounds();
        n.lockMeshes();
    }
}
