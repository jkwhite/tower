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


import com.jmex.bui.*;
import com.jmex.bui.background.*;
import com.jmex.bui.layout.*;
import com.jme.renderer.ColorRGBA;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import com.jme.renderer.Renderer;


public class TextWindow extends BWindow {
    private BTextArea _text;
    private boolean _centered;


    public TextWindow(BStyleSheet s) {
        super(s, new BorderLayout());
        _text = new BTextArea();
        //_text.setBackground(new TintedBackground(new ColorRGBA(0f, 0f, 0f, 0.8f)));
        _text.setText("");
        BContainer c = new BContainer(new BorderLayout());
        c.add(_text, BorderLayout.CENTER);
        add(c, BorderLayout.CENTER);
    }

    public void setCentered(boolean centered) {
        if(_centered!=centered) {
            removeAll();
            if(centered) {
                setLayoutManager(new BorderLayout());
                VGroupLayout m = new VGroupLayout();
                m.setOffAxisJustification(m.CENTER);
                BContainer c = new BContainer(m);
                //setLayoutManager(m);
                c.add(_text);
                add(c, BorderLayout.CENTER);
            }
            else {
                setLayoutManager(new BorderLayout());
                BContainer c = new BContainer(new BorderLayout());
                c.add(_text, BorderLayout.CENTER);
                add(c, BorderLayout.CENTER);
            }
            _centered = centered;
        }
    }

    public void setText(String text) {
        _text.setText(text);
    }
}
