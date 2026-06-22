package skdocs.crebs.betterModelSk.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;

@Name("Better Model - Teleport")
@Description("Teleports the carrier mob of a spawned better model entity to a location. The model follows.")
@Example("better model teleport {_mob} to location of player")
@Since("1.0")
public class EffBetterModelTeleport extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffBetterModelTeleport.class)
                        .supplier(EffBetterModelTeleport::new)
                        .addPattern("better[ ]model (teleport|tp) %bettermodelentity% to %location%")
                        .build());
    }

    private Expression<BetterModelEntity> modelEntity;
    private Expression<Location> location;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.modelEntity = (Expression<BetterModelEntity>) expressions[0];
        this.location = (Expression<Location>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        BetterModelEntity template = this.modelEntity.getSingle(event);
        Location loc = this.location.getSingle(event);
        if (template == null || loc == null) return;

        Entity entity = template.getEntity();
        if (entity != null) entity.teleport(loc);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model teleport " + this.modelEntity.toString(event, debug)
                + " to " + this.location.toString(event, debug);
    }
}
