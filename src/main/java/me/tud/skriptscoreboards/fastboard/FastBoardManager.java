package me.tud.skriptscoreboards.fastboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public final class FastBoardManager {

    private static final Map<UUID, FastBoardWrapper> boards = new WeakHashMap<>();
    private static boolean registered;

    private FastBoardManager() {
        throw new UnsupportedOperationException();
    }

    public static void registerListener(Plugin plugin) {
        if (registered)
            throw new IllegalStateException("Listener already registered");
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerJoin(PlayerJoinEvent event) {
                getBoard(event.getPlayer()).ifPresent(board -> board.addViewer(event.getPlayer()));
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerQuit(PlayerQuitEvent event) {
                getBoard(event.getPlayer()).ifPresent(board -> board.removeViewer(event.getPlayer()));
            }
        }, plugin);
        registered = true;
    }

    public static Optional<FastBoardWrapper> getBoard(Player player) {
        return Optional.ofNullable(boards.get(player.getUniqueId()));
    }

    public static FastBoardWrapper getOrCreate(Player player) {
        return boards.computeIfAbsent(player.getUniqueId(), uuid -> {
            FastBoardWrapper board = new FastBoardWrapper();
            board.addViewer(player);
            return board;
        });
    }

    public static void setBoard(Player player, @Nullable FastBoardWrapper board) {
        getBoard(player).ifPresent(oldBoard -> oldBoard.removeViewer(player));
        if (board != null) {
            boards.put(player.getUniqueId(), board);
            board.addViewer(player);
        } else {
            boards.remove(player.getUniqueId());
        }
    }

    public static void clearViewers(FastBoardWrapper board) {
        board.getViewers().forEach(viewer -> {
            board.removeViewer(viewer);
            boards.remove(viewer.getUniqueId());
        });
    }

}
