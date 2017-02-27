package org.excelsi.aether;


@FunctionalInterface
public interface Stagemaker {
    Stage createStage(int ordinal);


    public static Stagemaker expanse() {
        return (i)->{ throw new UnsupportedOperationException(); };
    }
}
