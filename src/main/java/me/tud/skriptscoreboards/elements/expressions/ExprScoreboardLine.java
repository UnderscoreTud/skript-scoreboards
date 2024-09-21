package me.tud.skriptscoreboards.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@Name("Scoreboard Line")
@Description({
    "Get or set the lines or scores of lines in a scoreboard. Scores are the values that are displayed on the right side of the scoreboard.",
    "Custom line scores are only supported in Minecraft 1.20.3 and newer."
})
@Examples({
    "on join:",
        "\tset title of scoreboard of player to \"&c&lMy Scoreboard\"",
        "\tset line 1 of scoreboard of player to \"&cPlayer: &f%player%\"",
        "\tset line 2 of scoreboard of player to \"&cKills: &f%{kills::%player%}%\"",
        "",
        "\t# Skipped lines will be empty",
        "\tset line 4 of scoreboard of player to \"IP: &fmyserver.com\""
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.20.3+ (custom scores)")
public class ExprScoreboardLine extends PropertyExpression<FastBoardWrapper, String> {

    static {
        Skript.registerExpression(ExprScoreboardLine.class, String.class, ExpressionType.COMBINED,
            "[all [[of] the]|the] (lines|score:scores) of %scoreboards%",

            "[the] [:score[s] of [the]] line[s] %integers% of %scoreboards%",

            "[the] [score:score of [the]] %integer%(st|nd|rd|th) line of %scoreboards%",
            "[score:[the] score of] %scoreboards%'[s] %integer%(st|nd|rd|th) line",

            "score:[the] %integer%(st|nd|rd|th) line's score of %scoreboards%",
            "score:%scoreboards%'[s] %integer%(st|nd|rd|th) line's score"
        );
    }

    private @Nullable Expression<Integer> lines;
    private boolean score;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        score = parseResult.hasTag("score");
        if (score && !Skript.isRunningMinecraft(1, 20, 3)) {
            Skript.error("Custom line scores are only supported in Minecraft 1.20.3 and newer");
            return false;
        }
        if (expressions.length == 2)
            lines = (Expression<Integer>) expressions[0];
        setExpr((Expression<? extends FastBoardWrapper>) expressions[expressions.length - 1]);
        return true;
    }

    @Override
    protected String[] get(Event event, FastBoardWrapper[] source) {
        if (lines == null) {
            return Arrays.stream(source)
                .flatMap(board -> score ? board.getScores().stream() : board.getLines().stream())
                .toArray(String[]::new);
        }
        int[] lineNumbers = lines.stream(event)
            .mapToInt(Integer::intValue)
            .toArray();
        return Arrays.stream(source)
            .flatMap(board -> Arrays.stream(lineNumbers)
                .mapToObj(lineNumber -> score ? board.getScore(lineNumber - 1) : board.getLine(lineNumber - 1)))
            .toArray(String[]::new);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (lines == null) {
            if (score && mode != ChangeMode.DELETE) {
                Skript.error("You can only set the score of a specific line. E.g. set score of line 1 of scoreboard to \"&c1\"");
                return null;
            }
            return switch (mode) {
                case SET, ADD, REMOVE, REMOVE_ALL, DELETE -> CollectionUtils.array(String[].class);
                default -> null;
            };
        }
        return switch (mode) {
            case SET, DELETE -> CollectionUtils.array(String.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        if (lines == null) {
            for (FastBoardWrapper board : getExpr().getArray(event)) {
                switch (mode) {
                    case SET -> board.setLines(Arrays.copyOf(delta, delta.length, String[].class));
                    case ADD -> {
                        assert delta != null;
                        for (Object object : delta) {
                            if (!board.addLine((String) object))
                                break;
                        }
                    }
                    case REMOVE, REMOVE_ALL -> {
                        assert delta != null;
                        for (Object object : delta) {
                            int index = 0;
                            for (String line : List.copyOf(board.getLines())) {
                                if (!line.equalsIgnoreCase((String) object)) {
                                    index++;
                                    continue;
                                }
                                board.removeLine(index);
                                if (mode == ChangeMode.REMOVE)
                                    break;
                            }
                        }
                    }
                    case DELETE -> {
                        if (score) board.clearScores();
                        else board.clear();
                    }
                    default -> throw new IllegalStateException();
                }
            }
            return;
        }
        int[] lineNumbers = lines.stream(event)
            .mapToInt(Integer::intValue)
            .toArray();
        for (FastBoardWrapper board : getExpr().getArray(event)) {
            for (int lineNumber : lineNumbers) {
                lineNumber--;
                String text = delta != null ? (String) delta[0] : "";
                switch (mode) {
                    case SET -> {
                        if (score) board.setScore(lineNumber, text);
                        else board.setLine(lineNumber, text);
                    }
                    case DELETE -> {
                        if (score) board.setScore(lineNumber, null);
                        else board.removeLine(lineNumber);
                    }
                    default -> throw new IllegalStateException();
                }
            }
        }
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        if (!getExpr().isSingle())
            return false;
        return lines != null && lines.isSingle();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (lines == null)
            return "lines of " + getExpr().toString(event, debug);
        return "line " + lines.toString(event, debug) + " of " + getExpr().toString(event, debug);
    }

}
