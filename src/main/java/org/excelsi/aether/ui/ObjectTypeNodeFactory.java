package org.excelsi.aether.ui;


import java.util.Map;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.excelsi.matrix.Typed;


public class ObjectTypeNodeFactory implements NodeFactory<Typed> {
    private final Map<String,NodeFactory> _nfs;


    public ObjectTypeNodeFactory(Map<String,NodeFactory> nfs) {
        _nfs = nfs;
    }

    @Override public Spatial createNode(final String name, final Typed s, final SceneContext sc) {
        if(s==null) {
            return null;
        }
        final NodeFactory nf = _nfs.get(s.getObjectType());
        if(nf!=null) {
            return nf.createNode(name, s, sc);
        }
        else {
            return null;
        }
    }
}
