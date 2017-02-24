package org.excelsi.aether.ui;


import java.util.Map;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.excelsi.matrix.Typed;


public class CompositeNodeFactory implements NodeFactory<Typed> {
    private final Map<String,NodeFactory> _nfs;
    private final NodeFactory _fallback;


    public CompositeNodeFactory(Map<String,NodeFactory> nfs, NodeFactory fallback) {
        _nfs = nfs;
        _fallback = fallback;
    }

    @Override public Spatial createNode(final String name, final Typed s, final SceneContext sc) {
        if(s==null) {
            return _fallback.createNode(name, s, sc);
        }
        final NodeFactory nf = _nfs.get(s.getObjectType());
        if(nf!=null) {
            return nf.createNode(name, s, sc);
        }
        else {
            return _fallback.createNode(name, s, sc);
        }
    }
}
