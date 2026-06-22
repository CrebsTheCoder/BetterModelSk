package skdocs.crebs.betterModelSk.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.Tracker;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.util.BetterModelUtil;

@Name("Better Model - Model Id")
@Description({
        "Returns the model id(s) attached to an entity, e.g. llama_pinata.",
        "Works on the source mob or any of its parts. An entity may carry more than one model."
})
@Example("send \"model: %better model id of targeted entity%\"")
@Since("1.0")
public class ExprBetterModelId extends SimpleExpression<String> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ExprBetterModelId.class, String.class)
                        .supplier(ExprBetterModelId::new)
                        .priority(SyntaxInfo.COMBINED)
                        .addPattern("better[ ]model id[s] of %entity%")
                        .build());
    }

    private Expression<Entity> input;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.input = (Expression<Entity>) expressions[0];
        return true;
    }

    @Override
    protected String @Nullable [] get(Event event) {
        Entity in = this.input.getSingle(event);
        if (in == null) return new String[0];

        EntityTrackerRegistry registry = BetterModelUtil.registryOf(in);
        if (registry == null) return new String[0];

        return registry.trackers().stream().map(Tracker::name).toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model id of " + this.input.toString(event, debug);
    }
}
