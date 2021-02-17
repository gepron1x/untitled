package me.gepronix.sqltest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

public final class SqlTest extends JavaPlugin {
    public AsyncSqlWorker getSqlWorker() {
        return sqlWorker;
    }

    private AsyncSqlWorker sqlWorker;
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigurationSection sqlConfig = getConfig().getConfigurationSection("mysql");

        sqlWorker = new AsyncSqlWorker(
                this,
                sqlConfig.getString("host"),
                sqlConfig.getString("user"),
                sqlConfig.getString("database"),
                sqlConfig.getString("password")
        );
        sqlWorker.connect(connection -> {
            try {
                connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS player_data (`uuid` VARCHAR(36) NOT NULL UNIQUE," +
                        " `logout_date` DATETIME NOT NULL)");
                ResultSet rs = connection.prepareStatement("SELECT * FROM player_data").executeQuery();
                while(rs.next()) {
                    PlayerProfile.saveProfile(
                            UUID.fromString(rs.getString("uuid")),
                            sf.parse(rs.getString("logout_date"))
                    );
                }

            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        });
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

    }

    @Override
    public void onDisable() {
        sqlWorker.close();
    }
}
