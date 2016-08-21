package org.excelsi.aether.ui;


import java.util.HashMap;
import java.util.Map;


public class Maps {
    public static <K,V> Map<K,V> map(Object... kvs) {
        final Map m = new HashMap();
        for(int i=0;i<kvs.length;i+=2) {
            m.put(kvs[i], kvs[i+1]);
        }
        return (Map<K,V>) m;
    }

    private Maps() {}
}
