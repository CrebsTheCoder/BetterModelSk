package skdocs.crebs.betterModelSk.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.util.DisguiseState;

@Name("Better Model - Undisguise Player")
@Description("Removes every BetterModel disguise from players.")
@Example("better model undisguise player")
@Since("1.0")
public class EffBetterModelUndisguise extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffBetterModelUndisguise.class)
                        .supplier(EffBetterModelUndisguise::new)
                        .addPattern("better[ ]model undisguise %players%")
                        .build());
    }

    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player player : this.players.getArray(event)) {
            EntityTrackerRegistry registry = EntityTrackerRegistry.registry(player.getUniqueId());
            if (registry != null) registry.close();
            DisguiseState.restore(player);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model undisguise " + this.players.toString(event, debug);
    }
}
