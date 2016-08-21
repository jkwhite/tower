package org.excelsi.aether.ui;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public abstract class Enloggened {
    private final Logger _logger = LoggerFactory.getLogger(getClass());


    protected final Logger log() {
        return _logger;
    }
}
