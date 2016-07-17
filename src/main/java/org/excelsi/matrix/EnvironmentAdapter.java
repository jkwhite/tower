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


public class EnvironmentAdapter implements EnvironmentListener {
    public void moved(Bot b, MSpace from, MSpace to) {
    }

    public void faced(Bot b, Direction old, Direction d) {
    }

    public void obscured(Bot b, List<MSpace> s) {
    }

    public void seen(Bot b, List<MSpace> s) {
    }

    public void discovered(Bot b, List<MSpace> s) {
    }

    public void forgot(Bot b, List<MSpace> s) {
    }

    public void noticed(Bot b, List<Bot> bots) {
    }

    public void missed(Bot b, List<Bot> bots) {
    }

    public void died(Bot b, MSource s) {
    }

    public void collided(Bot active, Bot passive) {
    }

    public void attributeChanged(Bot b, String attribute, Object newValue) {
    }
}
