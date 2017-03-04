package org.excelsi.tower;


import java.util.List;
import java.util.Map;

import org.excelsi.aether.Rand;


public class Segment {
    private String _realm;
    private Map<String,String> _names;
    private int _height;
    private boolean _unique;
    private boolean _stretchy;
    private List<String> _environs;


    public Segment() {
    }

    public void setRealm(String realm) {
        _realm = realm;
    }

    public String getRealm() {
        return _realm;
    }

    public void setNames(Map<String,String> names) {
        _names = names;
    }

    public Map<String,String> getNames() {
        return _names;
    }

    public String findName(final int ordinal) {
        final String k = Integer.toString(ordinal);
        String n = _names.get(k);
        if(n==null) {
            n = _names.get("*");
        }
        return n;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public int getHeight() {
        return _height;
    }

    public void setUnique(boolean unique) {
        _unique = unique;
    }

    public boolean getStretchy() {
        return _stretchy;
    }

    public void setStretchy(boolean stretchy) {
        _stretchy = stretchy;
    }

    public boolean getUnique() {
        return _unique;
    }

    public void setEnvirons(List<String> environs) {
        _environs = environs;
    }

    public List<String> getEnvirons() {
        return _environs;
    }

    public String[] randomEnvirons() {
        return _environs.get(Rand.om.nextInt(_environs.size())).split(" ");
    }
}
