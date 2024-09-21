package me.tud.skriptscoreboards.elements;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import me.tud.skriptscoreboards.fastboard.FastBoardManager;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(FastBoardWrapper.class, "scoreboard")
            .user("score ?boards?")
            .name("Scoreboard")
            .description(
                "A scoreboard object. A player can only view one scoreboard at a time.",
                "Scoreboards do not persist between server restarts."
            )
            .examples(
                "set {_board} to a new scoreboard",
                "set title of {_board} to \"Test\"",
                "set line 1 of {_board} to \"Line 1\"",
                "add player to {_board}"
            )
            .since("1.0.0")
            .parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(FastBoardWrapper board, int flags) {
                    String title = board.getTitle();
                    return "scoreboard" + (title.isEmpty() ? "" : " titled " + title);
                }

                @Override
                public String toVariableNameString(FastBoardWrapper board) {
                    return "scoreboard:" + board.hashCode();
                }
            })
            .changer(new Changer<>() {
                @Override
                public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                    return switch (mode) {
                        case ADD, REMOVE -> CollectionUtils.array(Player[].class);
                        default -> null;
                    };
                }

                @Override
                public void change(FastBoardWrapper[] what, Object @Nullable [] delta, ChangeMode mode) {
                    if (delta == null)
                        return;
                    switch (mode) {
                        case ADD -> {
                            for (FastBoardWrapper board : what) {
                                for (Object object : delta)
                                    FastBoardManager.setBoard((Player) object, board);
                            }
                        }
                        case REMOVE -> {
                            for (FastBoardWrapper board : what) {
                                for (Object object : delta) {
                                    if (board.getViewers().contains((Player) object))
                                        FastBoardManager.setBoard((Player) object, null);
                                }
                            }
                        }
                        default -> throw new IllegalStateException();
                    }
                }
            }));
    }

}
