package me.tud.skriptscoreboards.fastboard;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class FastBoardWrapper {

    private final Map<Player, FastBoard> boards = new HashMap<>();

    private final List<String> lines = new ArrayList<>();
    private final List<String> scores = new ArrayList<>();
    private String title = "";

    private boolean created = false;
    private boolean visible = true;

    public @UnmodifiableView Set<Player> getViewers() {
        return Collections.unmodifiableSet(boards.keySet());
    }

    public void addViewer(Player player) {
        if (visible && boards.containsKey(player))
            return;
        boards.put(player, isVisible() ? createBoard(player) : null);
    }

    public void removeViewer(Player player) {
        FastBoard board = boards.remove(player);
        if (board != null)
            board.delete();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        initialize();
        if (isVisible())
            boards.values().forEach(board -> board.updateTitle(title));
    }

    public @UnmodifiableView List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public String getLine(int line) {
        if (line < 0 || line >= lines.size())
            return null;
        return lines.get(line);
    }

    public boolean addLine(String text) {
        return addLine(text, null);
    }

    public boolean addLine(String text, @Nullable String score) {
        if (lines.size() >= 15)
            return false;
        lines.add(text);
        scores.add(score);
        initialize();
        if (isVisible())
            boards.values().forEach(board -> board.updateLine(lines.size() - 1, text, score));
        return true;
    }

    public void setLine(int line, String text) {
        setLine(line, text, getScore(line));
    }

    public void setLine(int line, String text, @Nullable String score) {
        if (line < 0 || line >= 15)
            return;
        if (line >= lines.size()) {
            lines.addAll(Collections.nCopies(line - lines.size(), ""));
            scores.addAll(Collections.nCopies(line - scores.size(), null));
            lines.add(text);
            scores.add(score);
        } else {
            lines.set(line, text);
            scores.set(line, score);
        }
        initialize();
        if (isVisible())
            boards.values().forEach(board -> board.updateLine(line, text, score));
    }

    public void setLines(String[] lines) {
        setLines(Arrays.asList(lines));
    }

    public void setLines(List<String> lines) {
        this.lines.clear();
        this.lines.addAll(lines.subList(0, Math.min(lines.size(), 15)));
        this.scores.clear();
        this.scores.addAll(Collections.nCopies(this.lines.size(), null));
        initialize();
        if (isVisible())
            boards.values().forEach(board -> board.updateLines(this.lines, scores));
    }

    public void removeLine(int line) {
        if (line < 0 || line >= lines.size())
            return;
        lines.remove(line);
        scores.remove(line);
        initialize();
        if (isVisible())
            boards.values().forEach(board -> board.updateLines(lines, scores));
    }

    public @UnmodifiableView List<String> getScores() {
        return Collections.unmodifiableList(scores);
    }

    public @Nullable String getScore(int line) {
        if (line < 0 || line >= lines.size())
            return null;
        return scores.get(line);
    }

    public void setScore(int line, @Nullable String score) {
        if (line < 0 || line >= lines.size())
            return;
        scores.set(line, score);
        initialize();
        if (isVisible())
            boards.values().forEach(board -> board.updateScore(line, score));
    }

    public void clearScores() {
        scores.clear();
        scores.addAll(Collections.nCopies(lines.size(), null));
        initialize();
        if (isVisible())
            boards.values().forEach(FastBoard::updateScores);
    }

    public void show() {
        if (visible)
            return;
        visible = true;
        if (created)
            boards.keySet().forEach(player -> boards.put(player, createBoard(player)));
    }

    public void hide() {
        if (!visible)
            return;
        visible = false;
        if (created)
            boards.values().forEach(FastBoard::delete);
    }

    public boolean isVisible() {
        return created && visible;
    }

    public void clear() {
        lines.clear();
        scores.clear();
        if (isVisible())
            boards.values().forEach(FastBoard::updateLines);
    }

    private void initialize() {
        if (created)
            return;
        created = true;
        if (visible)
            boards.keySet().forEach(player -> boards.put(player, createBoard(player)));
    }

    private FastBoard createBoard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle(title);
        board.updateLines(lines, scores);
        return board;
    }

}
