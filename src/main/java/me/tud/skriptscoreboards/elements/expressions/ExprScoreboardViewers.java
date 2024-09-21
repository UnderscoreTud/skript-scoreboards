package me.tud.skriptscoreboards.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tud.skriptscoreboards.fastboard.FastBoardManager;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Scoreboard Viewers")
@Description("Get or set the viewers of a scoreboard.")
@Examples({
    "on join:",
        "\tadd player to scoreboard viewers of {-global-scoreboard}"
})
@Since("1.0.0")
public class ExprScoreboardViewers extends PropertyExpression<FastBoardWrapper, Player> {

    static {
        register(ExprScoreboardViewers.class, Player.class, "[score[ ]]board (viewers|players)", "scoreboards");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends FastBoardWrapper>) expressions[0]);
        return true;
    }

    @Override
    protected Player[] get(Event event, FastBoardWrapper[] source) {
        return Arrays.stream(source)
            .flatMap(board -> board.getViewers().stream())
            .toArray(Player[]::new);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(Player[].class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        switch (mode) {
            case SET -> {
                assert delta != null;
                for (FastBoardWrapper board : getExpr().getArray(event)) {
                    FastBoardManager.clearViewers(board);
                    for (Object object : delta)
                        FastBoardManager.setBoard((Player) object, board);
                }
            }
            case ADD -> {
                assert delta != null;
                for (FastBoardWrapper board : getExpr().getArray(event)) {
                    for (Object object : delta)
                        FastBoardManager.setBoard((Player) object, board);
                }
            }
            case REMOVE -> {
                assert delta != null;
                for (FastBoardWrapper board : getExpr().getArray(event)) {
                    for (Object object : delta) {
                        if (board.getViewers().contains((Player) object))
                            FastBoardManager.setBoard((Player) object, null);
                    }
                }
            }
            case DELETE -> {
                for (FastBoardWrapper board : getExpr().getArray(event))
                    FastBoardManager.clearViewers(board);
            }
        }
    }

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "viewers of " + getExpr().toString(event, debug);
    }

}
