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


import java.util.Set;

import org.excelsi.matrix.MSpace;
import java.util.List;


public interface NHSpace extends MSpace, Container, Material, Described {
    void removeParasite(Parasite p);
    void addParasite(Parasite p);
    List<Parasite> getParasites();
    boolean hasParasite(Class p);
    void moveParasite(Parasite p, NHSpace to);
    String getModel();
    String getColor();
    void setColor(String color);
    boolean isWalkable();
    boolean isSpecial();
    int getHeight();
    int getDepth();
    //void setDepth(int depth);
    int getModifiedDepth();
    int getOccupantDepth();
    void setAltitude(int alt);
    int getAltitude();
    void setStatus(Status s);
    Status getStatus();
    Inventory getLoot();
    void setLoot(Inventory inventory);
    void addLoot(Container inventory);
    void destroyLoot();
    boolean isDestroyable();
    void destroy();
    List<MSpace> visible(Set bots, Set<NHSpace> spaces, Set<NHSpace> knowns, float max);
    NHBot getOccupant();
    void setOverlay(Overlay o);
    Overlay getOverlay();
    void moveOverlay(NHSpace other);
    //boolean look(NHBot b);
    boolean look(Context c, boolean nothing, boolean lootOnly);
    boolean isAutopickup();
    boolean pickup(NHBot b);
    Architecture getArchitecture();
    Orientation getOrientation();
    void bloom(Transform t, int radius);
}
