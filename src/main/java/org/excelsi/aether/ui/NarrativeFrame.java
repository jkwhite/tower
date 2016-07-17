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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;

import com.jmex.bui.*;
import com.jmex.bui.background.*;

import com.jmex.bui.layout.BorderLayout;
import java.util.List;
import java.util.ArrayList;


public class NarrativeFrame extends BContainer {
    private static final int WIDTH = 70;
    private static final int RETAINED = 200;
    private BLabel _message;
    private List<String> _previous = new ArrayList<String>(1+RETAINED);
    private int _prevIndex = 0;
    private StringBuffer _text = new StringBuffer();
    private int _printcCount = 0;
    private InputSource _input;
    private int _widthEstimate = 0;


    public NarrativeFrame(InputSource input) {
        super(new BorderLayout());
        _input = input;
        _message = new BLabel("");
        add(_message, BorderLayout.NORTH);
    }

    public void print(NHSpace source, String message) {
        print((NHBot)null, message);
    }

    public void print(NHBot source, String message) {
        synchronized(_text) {
            if(_text.length()>80) {
                more();
            }
        }
        if(_text.length()>0) {
            _text.append(" ");
        }
        _text.append(message);
        _message.setText(_text.toString());
    }

    public void append(String text) {
        _text.append(text);
        _message.setText(_text.toString());
    }

    public void willAppend(int length) {
        _widthEstimate += length;
    }

    public void willClear() {
        _widthEstimate = 0;
    }

    public void printc(NHBot source, String message) {
        append(message);
        _printcCount++;
    }

    public void backspace() {
        if(_printcCount>0) {
            _text.setLength(_text.length()-1);
            _message.setText(_text.toString());
            _printcCount--;
        }
    }

    public String getMessage() {
        return _message.getText();
    }

    public void redo() {
        String txt = _message.getText();
        remove(_message);
        _message = new BLabel(txt);
        add(_message, BorderLayout.NORTH);
    }

    public boolean isClear() {
        return _widthEstimate == 0;
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean addPrevious) {
        _widthEstimate = 0;
        if(addPrevious&&_text.length()>0) {
            _previous.add(_text.toString().replace(" --More--", ""));
            if(_previous.size()>200) {
                _previous.remove(0);
            }
        }
        _text.setLength(0);
        //remove(_message);
        //_message = new BLabel("");
        //add(_message, BorderLayout.NORTH);
        if(!_message.getText().equals("")) {
            _message.setText("");
        }
        _printcCount = 0;
    }

    public boolean confirm(NHBot source, String message) {
        if(_text.length()>0) {
            more();
        }
        print(source, message+" [yn]");
        for(;;) {
            String k = _input.nextKey();
            if(k.equals("y")) {
                clear(false);
                return true;
            }
            else if(k.equals("n")||k.equals("ESCAPE")) {
                clear(false);
                return false;
            }
        }
    }

    public int length() {
        return _text.length();
    }

    public int estimatedLength() {
        return _widthEstimate;
    }

    public String reply(NHBot source, String query) {
        if(_text.length()>0) {
            more();
        }
        print(source, query+" ");
        StringBuffer b = new StringBuffer();
        for(;;) {
            String k = _input.nextKey();
            if(k.equals("ENTER")) {
                clear(false);
                return b.toString();
            }
            else if(k.equals("ESCAPE")) {
                throw new ActionCancelledException();
            }
            else if(k.equals("BACK")) {
                backspace();
                b.setLength(b.length()-1);
            }
            else {
                printc(source, k);
                b.append(k);
            }
        }
    }

    public Direction direct(NHBot source, String message) {
        if(_text.length()>0) {
            more();
        }
        for(;;) {
            print(source, message);
            String k = _input.nextKey();
            clear(false);
            if(k.equals("ESCAPE")) {
                throw new ActionCancelledException();
            }
            try {
                GameAction a = _input.actionFor(k);
                if(a instanceof Director) {
                    return ((Director)a).getDirection();
                }
            }
            catch(IllegalArgumentException e) {
            }
            catch(NullPointerException e) {
            }
            print(source, "That is not a direction.");
            more();
        }
    }

    public void more() {
        _text.append(" --More--");
        _message.setText(_text.toString());
        while(true) {
            String key = _input.nextKey();
            if(" ".equals(key)||"ENTER".equals(key)) {
                break;
            }
        }
        clear();
    }

    public void resetPrevious() {
        _prevIndex = 0;
    }

    public void previous() {
        _text.setLength(0);
        if(--_prevIndex==-1) {
            _prevIndex = _previous.size()-1;
        }
        _message.setText(_previous.get(_prevIndex));
    }
}
