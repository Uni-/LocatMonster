package net.aurynj.rne.locatmonster.appframework;

import com.google.android.gms.maps.model.LatLng;

import net.aurynj.rne.locatmonster.model.*;
import net.aurynj.rne.locatmonster.data.*;

public class RegionHelper {
    private static final RegionClass[] REGIONS = {
            new Region_ID_1(),
            new Region_ID_2(),
            new Region_ID_3(),
            new Region_ID_4(),
            new Region_ID_5(),
    };

    private static final String[] REGION_NAMES = new String[REGIONS.length];

    static {
        for (int i = 0; i < REGIONS.length; i++) {
            REGION_NAMES[i] = REGIONS[i].getName();
        }
    }

    public static String[] getRegionNames() {
        return REGION_NAMES;
    }

    public static RegionClass getRegion(int i) {
        return REGIONS[i];
    }

    public RegionClass findRegion(LatLng latLng) {
        for (RegionClass region: REGIONS) {
            if (region.includes(new LatLngImpl(latLng.latitude, latLng.longitude)))
                return region;
        }
        return null;
    }
}
