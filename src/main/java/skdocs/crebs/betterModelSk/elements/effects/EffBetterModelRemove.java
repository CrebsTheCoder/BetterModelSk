package skdocs.crebs.betterModelSk.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;

@Name("Better Model - Remove")
@Description("Closes the model tracker and removes the carrier mob of a spawned better model entity.")
@Example("better model remove {_mob}")
@Since("1.0")
public class EffBetterModelRemove extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffBetterModelRemove.class)
                        .supplier(EffBetterModelRemove::new)
                        .addPattern("better[ ]model (remove|despawn) %bettermodelentity%")
                        .build());
    }

    private Expression<BetterModelEntity> modelEntity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.modelEntity = (Expression<BetterModelEntity>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        BetterModelEntity template = this.modelEntity.getSingle(event);
        if (template == null) return;

        EntityTracker tracker = template.getTracker();
        if (tracker != null) tracker.close();

        Entity entity = template.getEntity();
        if (entity != null) entity.remove();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model remove " + this.modelEntity.toString(event, debug);
    }
}
