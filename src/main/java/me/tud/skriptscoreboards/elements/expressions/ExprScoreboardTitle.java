package me.tud.skriptscoreboards.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Scoreboard Title")
@Description("Get or set the title of a scoreboard.")
@Examples({
    "on join:",
        "\tset title of scoreboard of player to \"&c&lMy Scoreboard\""
})
@Since("1.0.0")
public class ExprScoreboardTitle extends SimplePropertyExpression<FastBoardWrapper, String> {

    static {
        register(ExprScoreboardTitle.class, String.class, "[[score[ ]]board] (title|name)", "scoreboards");
    }

    @Override
    public String convert(FastBoardWrapper board) {
        return board.getTitle();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> CollectionUtils.array(String.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        String title = delta != null ? (String) delta[0] : "";
        for (FastBoardWrapper board : getExpr().getArray(event))
            board.setTitle(title);
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "title";
    }

}
