package de.itsgraphax.fgtDiscovery.serverInfo;

import com.google.gson.annotations.SerializedName;

public class PlayerSample {
    @SerializedName("name")
    private String name;
    @SerializedName("uuid")
    private String uuid;

    public String uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }
}
