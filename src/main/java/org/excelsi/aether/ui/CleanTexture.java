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


import com.jme.scene.state.TextureState;
import com.jme.scene.Spatial;
import com.jme.scene.Node;
import java.util.ArrayList;
import com.jme.scene.state.RenderState;
import java.util.Iterator;


public class CleanTexture {
    private static TextureState ts;

    public static void cleanTexture(Spatial s) {
        ts = (TextureState)s.getRenderState(RenderState.RS_TEXTURE);
        if(ts != null)
            ts.deleteAll();
        if(s instanceof Node) {
            ArrayList<Spatial> children = ((Node)s).getChildren();
            if(children != null) {
                Iterator i = children.iterator();
                while(i.hasNext()) {
                    cleanTexture((Spatial)i.next());
                }
            }
        }
        ts = null;
    }
}
