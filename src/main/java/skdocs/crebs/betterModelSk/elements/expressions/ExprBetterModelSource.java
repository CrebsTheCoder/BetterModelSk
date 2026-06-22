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
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.util.BetterModelUtil;

@Name("Better Model - Source Entity")
@Description({
        "Returns the source mob of a BetterModel from any of its entities.",
        "Given a part (hitbox) the player is looking at, this returns the main entity, e.g. the llama."
})
@Example("set {_main} to better model source of targeted entity")
@Since("1.0")
public class ExprBetterModelSource extends SimpleExpression<Entity> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ExprBetterModelSource.class, Entity.class)
                        .supplier(ExprBetterModelSource::new)
                        .priority(SyntaxInfo.COMBINED)
                        .addPattern("better[ ]model (source|main|base) [entity] of %entity%")
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
    protected Entity @Nullable [] get(Event event) {
        Entity in = this.input.getSingle(event);
        if (in == null) return new Entity[0];

        EntityTrackerRegistry registry = BetterModelUtil.registryOf(in);
        if (registry == null) return new Entity[0];

        Entity source = BetterModelUtil.sourceEntity(registry);
        return source == null ? new Entity[0] : new Entity[]{source};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model source of " + this.input.toString(event, debug);
    }
}
