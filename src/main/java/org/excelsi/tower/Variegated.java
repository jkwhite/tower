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
package org.excelsi.tower;


import java.util.HashMap;
import java.util.Map;
import org.excelsi.aether.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


public abstract class Variegated extends Infliction {
    /** assigned variations, scoped per class */
    private static Map<String, Map<String, String>> _variations = new HashMap<String, Map<String, String>>();
    /** per class variation pool; must be initialized by subclass */
    private static Map<String, String[]> _allVariations = new HashMap<String, String[]>();
    /** assigned variation of this item */
    private String _variation;


    static void writeStatic(ObjectOutputStream os) throws IOException {
        os.writeObject(_variations);
        os.writeObject(_allVariations);
    }

    static void readStatic(ObjectInputStream is) throws ClassNotFoundException, IOException {
        _variations = (Map<String, Map<String,String>>) is.readObject();
        _allVariations = (Map<String,String[]>) is.readObject();
    }


    public abstract String getColor();

    public String getVariation(String category) {
        if(_variation==null) {
            Map<String, String> colormap = _variations.get(category);
            if(colormap==null) {
                colormap = new HashMap<String,String>();
                _variations.put(category, colormap);
            }
            _variation = colormap.get(super.getName());
            if(_variation==null) {
                for(String var:_allVariations.get(category)) {
                    if(!var.startsWith("-")&&!colormap.containsValue(var)) {
                        colormap.put(super.getName(), var);
                        _variation = var;
                        break;
                    }
                }
            }
            if(_variation==null) {
                // avoid call to getName() which may induce recursive variegation
                throw new IllegalStateException("cannot variegate '"+super.getName()+"': no more variations");
            }
        }
        return _variation;
    }

    public GrammarType getPartOfSpeech() {
        return isClassIdentified()?GrammarType.nounPhrase:GrammarType.adjective;
    }

    protected static void variegate(String category, String[] variations) {
        // randomize order
        for(int i=0;i<variations.length;i++) {
            int j = Rand.om.nextInt(variations.length);
            String flip = variations[j];
            variations[j] = variations[i];
            variations[i] = flip;
        }
        _allVariations.put(category, variations);
    }
}
