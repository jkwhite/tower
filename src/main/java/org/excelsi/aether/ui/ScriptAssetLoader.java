package org.excelsi.aether.ui;


import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetInfo;
import java.io.IOException;


public class ScriptAssetLoader implements AssetLoader {
    @Override public Object load(AssetInfo assetInfo) throws java.io.IOException {
        if("groovy".equals(assetInfo.getKey().getExtension())) {
            //return loadGroovy(assetInfo);
            return "wot";
        }
        else {
            throw new IllegalArgumentException("unsupported script asset: "+assetInfo);
        }
    }

    //private static String
}
