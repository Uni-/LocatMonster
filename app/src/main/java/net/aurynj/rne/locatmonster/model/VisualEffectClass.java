package net.aurynj.rne.locatmonster.model;

public abstract class VisualEffectClass {
    protected VisualEffectClass() {}
    public abstract Type getType();
    public abstract BattleSide getTargetSide();

    public enum Type {
        NONE,
        CLAW,
        HIT,
        MAGIC_GLOW,
        MAGIC_FLASH,
    }
}
