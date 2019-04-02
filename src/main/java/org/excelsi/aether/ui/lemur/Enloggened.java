package org.excelsi.aether.ui.lemur;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Enloggened {
    private final Logger _log = LoggerFactory.getLogger(getClass());


    protected final Logger l() {
        return _log;
    }
}
