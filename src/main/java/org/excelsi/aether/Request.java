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


public class Request {
    private int _count;
    private Status _status;
    private String _noun;


    public Request(int count, Status status, String noun) {
        _count = count;
        _status = status;
        _noun = noun;
    }

    public int getCount() {
        return _count;
    }

    public Status getStatus() {
        return _status;
    }

    public String getNoun() {
        return _noun;
    }
}
