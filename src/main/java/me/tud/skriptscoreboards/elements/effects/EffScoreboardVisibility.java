package me.tud.skriptscoreboards.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Scoreboard Visibility")
@Description("Show or hide a scoreboard.")
@Examples({
    "show the scoreboard of all players",
    "hide the scoreboard of {-scoreboard}"
})
@Since("1.0.0")
public class EffScoreboardVisibility extends Effect {

    static {
        Skript.registerEffect(EffScoreboardVisibility.class, "(show|:hide) %scoreboards%");
    }

    private Expression<FastBoardWrapper> boards;
    private boolean hide;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        boards = (Expression<FastBoardWrapper>) expressions[0];
        hide = parseResult.hasTag("hide");
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (FastBoardWrapper board : boards.getArray(event)) {
            if (hide) board.hide();
            else board.show();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (hide ? "hide" : "show") + " " + boards.toString(event, debug);
    }

}
