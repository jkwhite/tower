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
 * Containers hold items. Items may be added to or
 * removed from containers, or transferred to other
 * containers.
 */
public interface Container extends java.io.Serializable {
    /**
     * Adds an item to this container.
     *
     * @param item item to add
     * @return index of added item
     */
    int add(Item item);

    /**
     * Adds an item to this container.
     *
     * @param item item to add
     * @param adder bot performing the add
     * @return index of added item
     */
    int add(Item item, NHBot adder);

    /**
     * Adds an item to this container.
     *
     * @param item item to add
     * @param adder bot performing the add
     * @param origin original location of added item (as in a throw)
     * @return index of added item
     */
    int add(Item item, NHBot adder, NHSpace origin);

    /**
     * Removes an item from this container.
     *
     * @param item item to remove
     * @return index of item before removal
     */
    int remove(Item item);

    /**
     * Consumes an item in this container, decrementing its count.
     * If an item's count reaches zero, it is
     * removed.
     *
     * @param item item to consume
     * @return index of (possibly-removed) item
     */
    int consume(Item item);

    /**
     * Destroys an item in this container.
     *
     * @param item item to destroy
     * @return <code>true</code> if the item was in this container and destroyed
     */
    boolean destroy(Item item);

    /**
     * Destroys all items in this container.
     */
    void destroyAll();

    /**
     * Moves an item to another container.
     *
     * @param item item to transfer
     * @destination container to which <code>item</code> should be moved
     */
    void transfer(Item item, Container destination);

    /**
     * Splits off a single instance of an item in this container.
     * This is useful for actions that operate on one instance of a stacked item.
     * The remaining item will be consumed if its count was originally 1.
     * <b>Note: </b> The returned item is <b>not</b> in any inventory.
     *
     * @param item item in this inventory
     * @return item with count 1
     */
    Item split(Item item);

    /**
     * Gets all items in this container.
     *
     * @return items in this container
     */
    Item[] getItem();

    /**
     * Gets the first item in this container.
     *
     * @return item at index 0 in this container
     */
    Item firstItem();

    /**
     * Gets the number of items in this container.
     *
     * @return number of items in this container
     */
    int numItems();

    /**
     * Tests if this container contains an item.
     *
     * @param item item to test
     * @return <code>true</code> if <code>item</code> is in this container
     */
    boolean contains(Item item);

    /**
     * Adds a listener to this container. If the listener
     * is already listening to this container, this method
     * does nothing.
     *
     * @param listener listener to add
     */
    void addContainerListener(ContainerListener listener);

    /**
     * Removes a listener from this container.
     *
     * @param listener listener to remove
     */
    void removeContainerListener(ContainerListener listener);
}
