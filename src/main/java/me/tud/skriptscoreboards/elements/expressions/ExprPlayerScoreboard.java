package me.tud.skriptscoreboards.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import me.tud.skriptscoreboards.fastboard.FastBoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Scoreboard of Player")
@Description({
    "Get or set the scoreboard of a player.",
    "If the player does not have a scoreboard, it will create a new one."
})
@Examples({
    "on join:",
        "\tset {_board} to player's scoreboard",
        "\tset title of {_board} to \"&c&lMy Scoreboard\"",
        "\tset line 1 of {_board} to \"&cPlayer: &f%player%\"",
        "\tset line 2 of {_board} to \"&cKills: &f%{kills::%player%}%\"",
        "",
        "\t# Skipped lines will be empty",
        "\tset line 4 of {_board} to \"IP: &fmyserver.com\""
})
@Since("INSERT VERSION")
public class ExprPlayerScoreboard extends SimplePropertyExpression<Player, FastBoardWrapper> {

    static {
        register(ExprPlayerScoreboard.class, FastBoardWrapper.class, "[score[ ]]board[s]", "players");
    }

    @Override
    public FastBoardWrapper convert(Player player) {
        return FastBoardManager.getOrCreate(player);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> CollectionUtils.array(FastBoardWrapper.class); 
            default -> super.acceptChange(mode);
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        switch (mode) {
            case SET -> {
                FastBoardWrapper board = (FastBoardWrapper) delta[0];
                for (Player player : getExpr().getArray(event))
                    FastBoardManager.setBoard(player, board);
            }
            case DELETE -> {
                for (Player player : getExpr().getArray(event))
                    FastBoardManager.setBoard(player, null);
            }
            default -> super.change(event, delta, mode);
        }
    }

    @Override
    public Class<? extends FastBoardWrapper> getReturnType() {
        return FastBoardWrapper.class;
    }

    @Override
    protected String getPropertyName() {
        return "scoreboard";
    }

}
