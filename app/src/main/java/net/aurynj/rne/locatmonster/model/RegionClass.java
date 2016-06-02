package net.aurynj.rne.locatmonster.model;

public abstract class RegionClass {
    protected RegionClass() {}
    public abstract String getName();
    public abstract LatLngImpl[] polygon();
    public abstract boolean includes(LatLngImpl ll);
    public abstract CharacterClass roulette();
}
