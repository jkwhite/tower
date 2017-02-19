package org.excelsi.aether;


import java.util.function.Consumer;
import java.util.function.Function;


public final class Spaces {
    public static SpaceFactory identity() {
        return (s)->{
            try {
                return s.newInstance();
            }
            catch(Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }

    public static SpaceFactory modulator(final Consumer<NHSpace> m) {
        final SpaceFactory i = identity();
        return (s)->{
            final NHSpace n = i.create(s);
            m.accept(n);
            return n;
        };
    }
}
