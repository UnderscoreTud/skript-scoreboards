package me.tud.skriptscoreboards.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.util.Kleenean;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;

import static ch.njol.skript.lang.SkriptParser.ParseResult;

@Name("Scoreboard Visibility")
@Description("Check if a scoreboard is shown or hidden.")
@Examples({
    "function toggle_scoreboard(scoreboard: scoreboard):",
        "\tif {_scoreboard} is hidden:",
            "\t\tshow {_scoreboard}",
        "\telse:",
            "\t\thide {_scoreboard}"
})
@Since("INSERT VERSION")
public class CondScoreboardVisibility extends PropertyCondition<FastBoardWrapper> {

    static {
        register(CondScoreboardVisibility.class, "(shown|visible|:hidden)", "scoreboards");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<FastBoardWrapper>) expressions[0]);
        boolean hidden = parseResult.hasTag("hidden");
        setNegated((matchedPattern % 2 == 1) ^ hidden);
        return true;
    }

    @Override
    public boolean check(FastBoardWrapper board) {
        return board.isVisible();
    }

    @Override
    protected String getPropertyName() {
        return "visible";
    }

}
