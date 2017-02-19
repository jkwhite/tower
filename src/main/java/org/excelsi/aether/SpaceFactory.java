package org.excelsi.aether;


@FunctionalInterface
public interface SpaceFactory {
    NHSpace create(Class<? extends NHSpace> s);
}
