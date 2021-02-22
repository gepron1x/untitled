package me.gepronix.sqltest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;

public class AsyncSqlWorker {
    private Connection connection;
    private JavaPlugin owner;
    private String host, user, database, password;
    public AsyncSqlWorker(
            JavaPlugin owner,
            String host,
            String user,
            String database,
            String password
    ) {
        this.owner = owner;
        this.host = host;
        this.user = user;
        this.database = database;
        this.password = password;

    }
    public void connect(Consumer<Connection> onConnect) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
                    onConnect.accept(connection);
                } catch (SQLException t) {
                    t.printStackTrace();
                }
            }
        }.runTaskAsynchronously(owner);
    }
    public void executeAsync(Consumer<Connection> consumer) {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    if(connection != null || !connection.isClosed()) {
                        consumer.accept(connection);
                    } else {
                        connect(conn -> consumer.accept(conn));
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                consumer.accept(connection);
            }
        }.runTaskAsynchronously(owner);
    }
    public void close() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
