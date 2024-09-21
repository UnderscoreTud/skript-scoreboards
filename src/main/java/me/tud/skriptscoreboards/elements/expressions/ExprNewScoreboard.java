package me.tud.skriptscoreboards.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New Scoreboard")
@Description("Create a new scoreboard.")
@Examples({
    "on load:",
        "\tset {-global-scoreboard} to new scoreboard",
        "\tset title of {-global-scoreboard} to \"&c&lMy Scoreboard\"",
        "\tset line 1 of {-global-scoreboard} to \"&cPlayer Count: &f%size of all players%\"",
        "\tset line 2 of {-global-scoreboard} to \"&cServer IP: &fmyserver.com\"",

    "on join:",
        "\tset scoreboard of player to {-global-scoreboard}"
})
@Since("INSERT VERSION")
public class ExprNewScoreboard extends SimpleExpression<FastBoardWrapper> {

    static {
        Skript.registerExpression(ExprNewScoreboard.class, FastBoardWrapper.class, ExpressionType.SIMPLE, "[a [new]] score[ ]board");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected FastBoardWrapper[] get(Event event) {
        return new FastBoardWrapper[]{new FastBoardWrapper()};
    }

    @Override
    public Class<? extends FastBoardWrapper> getReturnType() {
        return FastBoardWrapper.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "a new scoreboard";
    }

}
