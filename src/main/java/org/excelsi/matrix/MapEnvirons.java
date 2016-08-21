package org.excelsi.matrix;


import java.util.Map;
import java.util.HashMap;


public abstract class MapEnvirons extends Id implements Environs {
    private final Map<String,Object> _props = new HashMap<>();


    @Override public String findString(final String name, final String dvalue) {
        final Object v = _props.get(name);
        return v==null?dvalue:v.toString();
    }

    @Override public float findFloat(final String name, final float dvalue) {
        final Object v = _props.get(name);
        return v==null?dvalue:v instanceof Float?(Float)v:Float.valueOf(v.toString());
    }

    @Override public Environs putProperty(String name, Object value) {
        _props.put(name, value);
        return this;
    }
}
