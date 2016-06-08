package net.aurynj.rne.locatmonster.model;

public enum BattleSide {
    NEAR,
    FAR,
    ;

    public BattleSide next() {
        switch (this) {
            case NEAR:
                return FAR;
            case FAR:
                return NEAR;
        }
        return null; // unreachable, in fact
    }

    public BattleSide multiply(BattleSide another) {
        switch (this) {
            case NEAR:
                switch (another) {
                    case NEAR:
                        return NEAR;
                    case FAR:
                        return FAR;
                }
                return null; // unreachable, in fact
            case FAR:
                switch (another) {
                    case NEAR:
                        return NEAR;
                    case FAR:
                        return FAR;
                }
                return null; // unreachable, in fact
        }
        return null; // unreachable, in fact
    }

    public<T> T select(T nearValue, T farValue) {
        switch (this) {
            case NEAR:
                return nearValue;
            case FAR:
                return farValue;
        }
        return null; // unreachable, in fact
    }

    public<T> T applySelect(BattleSide resultSide, T nearValue, T farValue) {
        switch (this) {
            case NEAR:
                switch (resultSide) {
                    case NEAR:
                        return nearValue;
                    case FAR:
                        return farValue;
                }
                return null; // unreachable, in fact
            case FAR:
                switch (resultSide) {
                    case NEAR:
                        return farValue;
                    case FAR:
                        return nearValue;
                }
                return null; // unreachable, in fact
        }
        return null; // unreachable, in fact
    }
}
