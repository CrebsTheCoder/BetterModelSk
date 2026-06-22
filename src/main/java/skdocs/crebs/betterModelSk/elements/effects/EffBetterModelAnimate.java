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
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;

@Name("Better Model - Animate")
@Description("Plays a named animation on a spawned better model entity.")
@Example("better model animate {_mob} with \"attack_pose\"")
@Since("1.0")
public class EffBetterModelAnimate extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffBetterModelAnimate.class)
                        .supplier(EffBetterModelAnimate::new)
                        .addPattern("better[ ]model animate %bettermodelentity% with [animation] %string%")
                        .build());
    }

    private Expression<BetterModelEntity> modelEntity;
    private Expression<String> animation;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.modelEntity = (Expression<BetterModelEntity>) expressions[0];
        this.animation = (Expression<String>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        BetterModelEntity template = this.modelEntity.getSingle(event);
        String anim = this.animation.getSingle(event);
        if (template == null || anim == null) return;

        EntityTracker tracker = template.getTracker();
        if (tracker == null) {
            Skript.warning("Cannot animate a better model entity that has not been spawned yet.");
            return;
        }
        tracker.animate(anim);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model animate " + this.modelEntity.toString(event, debug)
                + " with " + this.animation.toString(event, debug);
    }
}
