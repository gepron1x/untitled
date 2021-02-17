package me.gepronix.sqltest;

import jdk.internal.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {
    private static Map<UUID, PlayerProfile> profiles = new HashMap<>();

    public void setLogoutDate(Date logoutDate) {
        this.logoutDate = logoutDate;
    }

    private Date logoutDate;
    @Nullable
    public static PlayerProfile get(UUID uuid) {
        return profiles.get(uuid);
    }

    private PlayerProfile(Date logoutDate) {
        this.logoutDate = logoutDate;
    }
    public static void saveProfile(UUID uuid, Date logoutDate) {
        profiles.put(uuid, new PlayerProfile(logoutDate));
    }

    public Date getLogoutDate() {
        return logoutDate;
    }
}
