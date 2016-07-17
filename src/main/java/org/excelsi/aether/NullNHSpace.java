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
import java.util.List;
import java.util.Set;
import java.util.ArrayList;


public class NullNHSpace extends NullMatrixMSpace implements NHSpace {
    private Overlay _overlay;
    private boolean _replaceable;


    public NullNHSpace(Matrix m, int i, int j) {
        super(m, i, j);
        _replaceable = !(i<=0||j<=0||i>=m.width()-1||j>=m.height()-1);
    }

    public boolean isNull() {
        return true;
    }

    public float getShininess() {
        return 10f;
    }

    public boolean isSpecial() {
        return false;
    }

    public void update() {
    }

    public boolean isDestroyable() {
        return false;
    }

    public boolean hasParasite(Class c) {
        return false;
    }

    public void destroy() {
    }

    public boolean isReplaceable() {
        return _replaceable;
    }

    public boolean pickup(NHBot b) {
        return false;
    }

    public boolean isWalkable() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isAutopickup() {
        return false;
    }

    public boolean look(NHBot b) {
        return false;
    }

    public boolean look(NHBot b, boolean nothing, boolean lootOnly) {
        return false;
    }

    public int getAltitude() {
        int alt = 0;
        int count = 0;
        for(MSpace m:surrounding()) {
            if(m!=null) {
                alt += ((NHSpace)m).getAltitude();
                count++;
            }
        }
        return count > 0 ? alt / count : 0;
    }

    public void setAltitude(int alt) {
        throw new IllegalStateException("null spaces have no altitude");
    }

    public void setStatus(Status s) {
        throw new IllegalStateException("null spaces have no status");
    }

    public Status getStatus() {
        return Status.uncursed;
    }

    public int getHeight() {
        return 0;
    }

    public int getDepth() {
        return 0;
    }

    public int getModifiedDepth() {
        return getDepth();
    }

    public void setDepth(int d) {
        throw new IllegalStateException("null spaces have no depth");
    }

    public int getOccupantDepth() {
        throw new IllegalStateException();
    }

    public void addParasite(Parasite p) {
        throw new IllegalStateException("null spaces cannot have parasites");
    }

    public void removeParasite(Parasite p) {
        throw new IllegalStateException("null spaces cannot have parasites");
    }

    public void moveParasite(Parasite p, NHSpace to) {
        throw new IllegalStateException("null spaces cannot have parasites");
    }

    public List<Parasite> getParasites() {
        return new ArrayList<Parasite>(0);
    }

    public Inventory getLoot() {
        return new Inventory();
    }

    public NHBot getOccupant() {
        return null;
    }

    public List<MSpace> visible(Set bots, Set<NHSpace> spaces, Set<NHSpace> knowns, float max) {
        return new ArrayList<MSpace>();
    }

    public void setLoot(Inventory i) {
        throw new IllegalStateException("null spaces cannot have loot");
    }

    public void addLoot(Container i) {
        throw new IllegalStateException("null spaces cannot have loot");
    }

    public void destroyLoot() {
        throw new IllegalStateException("null spaces cannot have loot");
    }

    public void setOverlay(Overlay o) {
        _overlay = o;
    }

    public Overlay getOverlay() {
        return _overlay;
    }

    public void moveOverlay(NHSpace other) {
        Overlay o = _overlay;
        _overlay = null;
        other.setOverlay(o);
    }

    public String getColor() {
        return null;
    }

    public void setColor(String color) {
    }

    public String getModel() {
        return null;
    }

    public void addContainerListener(ContainerListener c) {
        throw new IllegalStateException("null spaces may not have listeners");
    }

    public void removeContainerListener(ContainerListener c) {
        throw new IllegalStateException("null spaces may not have listeners");
    }

    public int numItems() {
        return 0;
    }

    public Item firstItem() {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public Item[] getItem() {
        return new Item[0];
    }

    public void setItem(Item[] items) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public void transfer(Item i, Container dest) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public Item split(Item i) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public int add(Item item) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public int add(Item item, NHBot adder) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public int add(Item item, NHBot adder, NHSpace origin) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public boolean contains(Item item) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public int remove(Item item) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public int consume(Item item) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public boolean destroy(Item item) {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public void destroyAll() {
        throw new IllegalStateException("null spaces may not have loot");
    }

    public void bloom(Transform t, int radius) {
    }

    protected MatrixMSpace createNullSpace(Matrix m, int i, int j) {
        return new NullNHSpace(m, i, j);
    }
}
