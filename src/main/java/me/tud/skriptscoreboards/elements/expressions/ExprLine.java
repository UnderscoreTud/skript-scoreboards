package me.tud.skriptscoreboards.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.expressions.ExprSignText;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.ConvertedExpression;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tud.skriptscoreboards.fastboard.FastBoardWrapper;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

@NoDoc
public class ExprLine extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprLine.class, String.class, ExpressionType.COMBINED,
            "[the] line %number% [of %-scoreboard/block%]");
    }

    private ExprSignText signExpr;
    private ExprScoreboardLine scoreboardExpr;

    private Expression<?> line;
    private LazyExpression<?> something;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        line = expressions[0];
        if (expressions[1] == null) {
            EventValueExpression<Block> blockEventValue = new EventValueExpression<>(Block.class);
            if (!blockEventValue.init())
                return false;
            something = new LazyExpression<>(new EventValueExpression<>(Block.class));
            signExpr = new ExprSignText();
            return signExpr.init(new Expression[]{line, something}, 0, isDelayed, parseResult);
        }

        something = new LazyExpression<>(expressions[1]);
        Expression<?> converted;
        if ((converted = something.getConvertedExpression(FastBoardWrapper.class)) != null) {
            scoreboardExpr = new ExprScoreboardLine();
            line = line.getConvertedExpression(Integer.class);
            if (line == null || !scoreboardExpr.init(new Expression[]{line, converted}, 1, isDelayed, parseResult))
                scoreboardExpr = null;
        }

        if ((converted = something.getConvertedExpression(Block.class)) != null) {
            signExpr = new ExprSignText();
            if (!signExpr.init(new Expression[]{line, converted}, 0, isDelayed, parseResult))
                signExpr = null;
        }

        return signExpr != null || scoreboardExpr != null;
    }

    @Override
    protected String[] get(Event event) {
        return resolveExpression(event).getArray(event);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> CollectionUtils.array(String.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        resolveExpression(event).change(event, delta, mode);
    }

    private @Nullable Expression<String> resolveExpression() {
        if (scoreboardExpr == null) {
            assert signExpr != null;
            return signExpr;
        }
        if (signExpr == null)
            return scoreboardExpr;
        return null;
    }

    private Expression<String> resolveExpression(Event event) {
        something.resolve(event);
        Expression<String> expression = resolveExpression();
        if (expression != null)
            return expression;

        Object object = something.getSingle(event);
        if (object == null || Converters.converterExists(object.getClass(), FastBoardWrapper.class)) {
            assert scoreboardExpr != null;
            return scoreboardExpr;
        }
        if (Converters.converterExists(object.getClass(), Block.class)) {
            assert signExpr != null;
            return signExpr;
        }

        throw new IllegalStateException();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        Expression<?> expression = resolveExpression();
        if (expression != null)
            return expression.toString(event, debug);
        return "line " + line.toString(event, debug) + " of " + something.toString(event, debug);
    }

    private static class LazyExpression<T> extends WrapperExpression<T> {

        private transient T[] value;

        public LazyExpression(Expression<T> expression) {
            setExpr(expression);
        }

        @Override
        public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
            assert false;
            return false;
        }

        @Override
        protected T[] get(Event event) {
            return value;
        }

        @Override
        public T[] getAll(Event event) {
            return value;
        }

        @SuppressWarnings("unchecked")
        public void resolve(Event event) {
            value = (T[]) getExpr().getAll(event);
        }

        @Override
        protected @Nullable <R> ConvertedExpression<T, ? extends R> getConvertedExpr(Class<R>... to) {
            return ConvertedExpression.newInstance(this, to);
        }

        @Override
        public String toString(@Nullable Event event, boolean debug) {
            return getExpr().toString(event, debug);
        }

    }

}
