package me.gepronix.sqltest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventListener implements Listener {
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SqlTest plugin;

    public EventListener(SqlTest plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerProfile profile = PlayerProfile.get(p.getUniqueId());
        if(profile == null) return;
        p.sendMessage("Дата выхода: " + sf.format(profile.getLogoutDate()));
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Date date = new Date();
        PlayerProfile profile = PlayerProfile.get(player.getUniqueId());
        if(profile == null) {
            PlayerProfile.saveProfile(player.getUniqueId(), date);
        } else {
            profile.setLogoutDate(date);
        }

        plugin.getSqlWorker().executeAsync(connection -> {
            try {
                PreparedStatement st = connection.prepareStatement("INSERT INTO player_data (`uuid`, `logout_date`) VALUES (?,?) ON DUPLICATE KEY UPDATE logout_date=?");
                st.setString(1, player.getUniqueId().toString());
                st.setString(2, sf.format(date));
                st.setString(3, sf.format(date));
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }
}
