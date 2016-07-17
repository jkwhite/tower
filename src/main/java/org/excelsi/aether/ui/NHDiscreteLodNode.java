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


import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.DistanceSwitchModel;
import com.jme.renderer.Camera;
import com.jme.math.Vector3f;
import com.jme.scene.SwitchModel;
import com.jme.scene.SwitchNode;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;


/**
 * NHDiscreteLodNode assumes scale is 1, center is 0,
 * and requires no memory allocation during calculation.
 * It also doesn't updateWorldBound on updateWorldData,
 * or reset the dist vector on the model (which is already
 * retained by reference on the original set()).
 * This is useful in Tower, where a single level may have
 * a thousand or more LOD nodes.
 * <p/>
 * I also found that DiscreteLodNode would sometimes 'lose'
 * its children when switching. This does not happen as
 * NHDiscreteLodNode calls super.updateWorldData, unlike
 * DiscreteLodNode.
 */
public class NHDiscreteLodNode extends SwitchNode {
    private float last;
    private Vector3f worldCenter;
    private SwitchModel model;
    private static final Float worldSqr = new Float(1.7320508f); // sqrt(3)


    public NHDiscreteLodNode(String name, DistanceSwitchModel dsm) {
        super(name);
        model = dsm;
        model.set(worldSqr);
        worldCenter = new Vector3f();
        model.set(worldCenter);
    }

    private int lastActive = -1;
    public void selectLevelOfDetail(Camera cam) {
        super.updateWorldData(last);

        worldCenter.x=worldTranslation.x;
        worldCenter.y=worldTranslation.y;
        worldCenter.z=worldTranslation.z;
        worldCenter.subtractLocal(cam.getLocation());
        int active = model.getSwitchChild();
        if(active!=lastActive) {
            lastActive = active;
            setActiveChild(active);
        }
    }

    public void updateWorldData(float time) {
        super.updateWorldData(time);
        last = time;
    }

    public void draw(Renderer r) {
        selectLevelOfDetail(r.getCamera());
        super.draw(r);
    }
}
