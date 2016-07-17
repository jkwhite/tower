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
package org.excelsi.matrix;


import java.util.List;


public interface EnvironmentListener extends java.io.Serializable {
    void moved(Bot b, MSpace from, MSpace to);
    void faced(Bot b, Direction old, Direction d);
    void obscured(Bot b, List<MSpace> s);
    void seen(Bot b, List<MSpace> s);
    void discovered(Bot b, List<MSpace> s);
    void forgot(Bot b, List<MSpace> s);
    void noticed(Bot b, List<Bot> bots);
    void missed(Bot b, List<Bot> bots);
    void died(Bot b, MSource s);
    void collided(Bot active, Bot passive);
    void attributeChanged(Bot b, String attribute, Object oldValue);
}
