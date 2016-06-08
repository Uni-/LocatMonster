package net.aurynj.rne.locatmonster.model;

public abstract class ArenaClass {
    protected OnChangeListener mChangeListener;
    protected ArenaClass() {}

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mChangeListener = onChangeListener;
    }

    public static abstract class ShallowChangeEntry {
        public abstract int getTotalOffset();
        public abstract BattleSide getSourceSide();
        public abstract VisualEffectClass getVisualEffect();

        public static ShallowChangeEntry crate(final int totalOffset, final BattleSide sourceSide, final VisualEffectClass visualEffectClass) {
            return new ShallowChangeEntry() {
                @Override
                public int getTotalOffset() {
                    return totalOffset;
                }

                @Override
                public BattleSide getSourceSide() {
                    return sourceSide;
                }

                @Override
                public VisualEffectClass getVisualEffect() {
                    return visualEffectClass;
                }
            };
        }
    }

    public static abstract class StateChangeEntry {
        public abstract int getTotalOffset();
        public abstract BattleSide getTargetSide();
        public abstract PointClass getPointClass();
        public abstract int getPointIncrement();

        public static StateChangeEntry create(final int totalOffset, final BattleSide targetSide, final PointClass pointClass, final int pointIncrement) {
            return new StateChangeEntry() {
                @Override
                public int getTotalOffset() {
                    return totalOffset;
                }

                @Override
                public BattleSide getTargetSide() {
                    return targetSide;
                }

                @Override
                public PointClass getPointClass() {
                    return pointClass;
                }

                @Override
                public int getPointIncrement() {
                    return pointIncrement;
                }
            };
        }
    }

    public static abstract class ArenaEnd {

    }

    public interface OnChangeListener {
        void onShallowChange(ShallowChangeEntry shallowChangeEntry);
        void onStateChange(StateChangeEntry stateChangeEntry);
        void onEnd(BattleSide win);
    }
}
