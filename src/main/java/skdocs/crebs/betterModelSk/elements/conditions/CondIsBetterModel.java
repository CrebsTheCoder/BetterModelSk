package skdocs.crebs.betterModelSk.elements.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.util.BetterModelUtil;

@Name("Is Better Model Entity")
@Description({
        "Checks whether an entity belongs to a BetterModel: either the source mob itself or one of its parts (hitbox).",
        "Useful on the entity a player is looking at, since that is usually a hitbox part."
})
@Example("""
        if targeted entity is a better model entity:
            send "model: %better model id of targeted entity%\"""")
@Since("1.0")
public class CondIsBetterModel extends Condition {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondIsBetterModel.class)
                        .supplier(CondIsBetterModel::new)
                        .priority(SyntaxInfo.COMBINED)
                        .addPattern("%entities% (is|are) [a] better model [entity|part]")
                        .addPattern("%entities% (isn't|is not|aren't|are not) [a] better model [entity|part]")
                        .build());
    }

    private Expression<Entity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) expressions[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return this.entities.check(event, entity -> BetterModelUtil.registryOf(entity) != null, isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return this.entities.toString(event, debug) + (isNegated() ? " is not" : " is") + " a better model entity";
    }
}
