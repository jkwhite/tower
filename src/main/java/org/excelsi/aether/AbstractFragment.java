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


import java.util.Map;
import java.util.HashMap;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public abstract class AbstractFragment implements Fragment {
    private boolean _identified;
    private String _name;
    private Item _owner;
    private static Map<Class, Boolean> _classIdentified = new HashMap<Class, Boolean>();


    static void writeStatic(ObjectOutputStream os) throws IOException {
        Map<String, Boolean> ser = new HashMap<String, Boolean>();
        for(Map.Entry<Class,Boolean> e:_classIdentified.entrySet()) {
            ser.put(e.getKey().getName(), e.getValue());
        }
        os.writeObject(ser);
    }

    static void readStatic(ObjectInputStream is) throws ClassNotFoundException, IOException {
        _classIdentified = new HashMap<Class, Boolean>();
        Map<String,Boolean> ser = (Map<String,Boolean>) is.readObject();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for(Map.Entry<String,Boolean> e:ser.entrySet()) {
            _classIdentified.put(cl.loadClass(e.getKey()), e.getValue());
        }
    }

    public AbstractFragment() {
        String cname = getClass().getName().substring(1+getClass().getName().lastIndexOf('.'));
        if(cname.indexOf('$')>=0) {
            cname = cname.substring(1+cname.lastIndexOf('$'));
        }
        StringBuffer iname = new StringBuffer(""+Character.toLowerCase(cname.charAt(0)));
        for(int i=1;i<cname.length();i++) {
            char c = cname.charAt(i);
            if(c=='_') {
                iname.append('-');
            }
            else if(Character.isUpperCase(c)) {
                if(i>0&&cname.charAt(i-1)!='_') {
                    iname.append(" ");
                }
                iname.append(Character.toLowerCase(c));
            }
            else {
                iname.append(Character.toLowerCase(c));
            }
        }
        setName(iname.toString());
    }

    public final void setOwner(Item i) {
        _owner = i;
    }

    public final Item getOwner() {
        return _owner;
    }

    protected final String getOwnerName() {
        return _owner!=null?_owner.getName():"thing";
    }

    protected final String getOwnerPhrase() {
        if(_owner!=null) {
            return _owner.toString();
        }
        else {
            return getName();
        }
    }

    public boolean equals(Object o) {
        Fragment f = (Fragment) o;
        return getClass()==f.getClass()&&getName().equals(f.getName());
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getText() {
        return getName();
    }

    public void setClassIdentified(boolean identified) {
        _classIdentified.put(getClass(), identified);
    }

    public boolean isClassIdentified() {
        return _classIdentified.containsKey(getClass())?_classIdentified.get(getClass()):false;
    }

    public void setIdentified(boolean identified) {
        _identified = identified;
    }

    public boolean isIdentified() {
        return _identified;
    }

    public int getOccurrence() {
        return 100;
    }

    public Modifier getModifier() {
        return null;
    }

    public int getPowerModifier() {
        return 0;
    }

    public int getRateModifier() {
        return 0;
    }

    public void apply(Fragment f) {
    }

    public boolean intercepts(Attack a) {
        return false;
    }

    public Performable intercept(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public AbstractFragment deepCopy() {
        return (AbstractFragment) DefaultNHBot.deepCopy(this);
    }

    //public boolean equals(Object o) {
        //return getClass()==o.getClass() && _identified==((AbstractFragment)o)._identified;
    //}
}
