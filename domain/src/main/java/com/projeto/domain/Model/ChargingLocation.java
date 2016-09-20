package com.projeto.domain.Model;

import android.location.Location;

public class ChargingLocation {

    private Location location;
    private boolean isUpdated;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

}
