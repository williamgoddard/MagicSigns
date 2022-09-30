package me.uniqueimpact.magicsigns.objects;

import org.bukkit.Location;

public class SignBlock {

    private final String name;
    private final Location location;
    private final String placedBy;

    public SignBlock(String name, Location location, String placedBy) {
        this.name = name;
        this.location = location;
        this.placedBy = placedBy;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getPlacedBy() {
        return placedBy;
    }
}
