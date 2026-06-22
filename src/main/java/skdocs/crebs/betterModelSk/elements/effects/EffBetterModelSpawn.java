package skdocs.crebs.betterModelSk.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;

@Name("Better Model - Spawn")
@Description({
        "Spawns a better model entity in the world.",
        "The carrier mob is spawned at the location, then the model is attached to it.",
        "If no model with the given id is loaded, the carrier mob is spawned without a model."
})
@Example("""
        set {_mob} to a new better model entity:
            model: blue_knight
            mob: zombie
        better model spawn {_mob} at location of player""")
@Since("1.0")
public class EffBetterModelSpawn extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffBetterModelSpawn.class)
                        .supplier(EffBetterModelSpawn::new)
                        .addPattern("better[ ]model spawn %bettermodelentity% at %location%")
                        .build());
    }

    private static @Nullable BetterModelEntity lastSpawned;

    public static @Nullable BetterModelEntity lastSpawned() {
        return lastSpawned;
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
        if (template == null || loc == null || loc.getWorld() == null) return;

        Entity entity = loc.getWorld().spawnEntity(loc, template.getMobType());
        applyAttributes(entity, template);

        EntityTracker tracker = BetterModel.model(template.getModelName())
                .map(renderer -> renderer.getOrCreate(BukkitAdapter.adapt(entity)))
                .orElse(null);

        if (tracker == null) {
            Skript.warning("No BetterModel found with id '" + template.getModelName()
                    + "'. Spawned the carrier mob without a model.");
        }

        template.setSpawned(entity, tracker);
        lastSpawned = template;
    }

    @SuppressWarnings("deprecation")
    private static void applyAttributes(Entity entity, BetterModelEntity template) {
        if (entity instanceof LivingEntity living) {
            if (template.getMaxHealth() != null) living.setMaxHealth(template.getMaxHealth() * 2);
            if (template.getHealth() != null) living.setHealth(Math.min(template.getHealth() * 2, living.getMaxHealth()));
            if (template.getAi() != null) living.setAI(template.getAi());
        }
        if (template.getGravity() != null) entity.setGravity(template.getGravity());
        if (template.getInvulnerable() != null) entity.setInvulnerable(template.getInvulnerable());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model spawn " + this.modelEntity.toString(event, debug)
                + " at " + this.location.toString(event, debug);
    }
}
