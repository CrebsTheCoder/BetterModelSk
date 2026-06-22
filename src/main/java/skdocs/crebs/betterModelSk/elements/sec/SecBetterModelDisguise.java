package skdocs.crebs.betterModelSk.elements.sec;

import ch.njol.skript.Skript;
import ch.njol.skript.config.EntryNode;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityHideOption;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.util.DisguiseState;

import java.util.List;
import java.util.Locale;

@Name("Better Model - Disguise Player")
@Description({
        "Disguises players as a BetterModel by attaching it to them, like /bettermodel disguise.",
        "By default the player's held item and armor stay visible; set 'hide hotbar: true' to hide them.",
        "Health and max health are doubled, matching how Minecraft draws hearts."
})
@Example("""
        better model disguise:
            target: player
            model: llama_pinata
            health: 500
            max health: 500
            hide hotbar: false""")
@Since("1.0")
public class SecBetterModelDisguise extends Section {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.SECTION,
                SyntaxInfo.builder(SecBetterModelDisguise.class)
                        .supplier(SecBetterModelDisguise::new)
                        .addPattern("better[ ]model disguise")
                        .build());
    }

    private Expression<Player> target;
    private String modelName;
    private @Nullable Double health;
    private @Nullable Double maxHealth;
    private boolean hideHotbar;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
                        ParseResult parseResult, @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {
        if (sectionNode == null) {
            Skript.error("better model disguise requires a section with 'target' and 'model' entries.");
            return false;
        }

        Expression<Player> targetExpr = null;
        String model = null;

        for (Node node : sectionNode) {
            String key;
            String value;

            if (node instanceof EntryNode entry) {
                key = entry.getKey();
                value = entry.getValue();
            } else {
                String line = node.getKey();
                int separator = line == null ? -1 : line.indexOf(':');
                if (separator < 0) {
                    Skript.error("Unexpected line '" + (line == null ? "" : line) + "'. Use 'key: value' entries.");
                    return false;
                }
                key = line.substring(0, separator);
                value = line.substring(separator + 1);
            }

            key = key.trim().toLowerCase(Locale.ROOT);
            value = value.trim();
            switch (key) {
                case "target", "targets", "player", "players" -> {
                    targetExpr = (Expression<Player>) new SkriptParser(value, SkriptParser.PARSE_EXPRESSIONS, ParseContext.DEFAULT)
                            .parseExpression(Player.class);
                    if (targetExpr == null) {
                        Skript.error("Can't understand the target '" + value + "'.");
                        return false;
                    }
                }
                case "model" -> model = value;
                case "health" -> {
                    this.health = parseDouble(key, value);
                    if (this.health == null) return false;
                }
                case "max health", "maxhealth", "max_health" -> {
                    this.maxHealth = parseDouble(key, value);
                    if (this.maxHealth == null) return false;
                }
                case "hide hotbar", "hide hand", "hide equipment" -> {
                    Boolean hide = parseBoolean(key, value);
                    if (hide == null) return false;
                    this.hideHotbar = hide;
                }
                default -> {
                    Skript.error("Unknown entry '" + key + "'. Expected one of: target, model, health, "
                            + "max health, hide hotbar.");
                    return false;
                }
            }
        }

        if (targetExpr == null) {
            Skript.error("Missing required entry 'target' (the players to disguise).");
            return false;
        }
        if (model == null || model.isEmpty()) {
            Skript.error("Missing required entry 'model' (the BetterModel id, e.g. llama_pinata).");
            return false;
        }

        this.target = targetExpr;
        this.modelName = model;
        return true;
    }

    private static @Nullable Double parseDouble(String key, String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            Skript.error("The value for '" + key + "' must be a number, got '" + value + "'.");
            return null;
        }
    }

    private static @Nullable Boolean parseBoolean(String key, String value) {
        switch (value.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "on", "enabled" -> {
                return Boolean.TRUE;
            }
            case "false", "no", "off", "disabled" -> {
                return Boolean.FALSE;
            }
            default -> {
                Skript.error("The value for '" + key + "' must be true or false, got '" + value + "'.");
                return null;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        EntityHideOption hideOption = new EntityHideOption(this.hideHotbar, true, true, true);

        for (Player player : this.target.getArray(event)) {
            EntityTracker tracker = BetterModel.model(this.modelName)
                    .map(renderer -> renderer.getOrCreate(BukkitAdapter.adapt(player), TrackerModifier.DEFAULT,
                            t -> t.hideOption(hideOption)))
                    .orElse(null);

            if (tracker == null) {
                Skript.warning("No BetterModel found with id '" + this.modelName + "'.");
                continue;
            }

            if (this.maxHealth != null) {
                DisguiseState.rememberMaxHealth(player);
                player.setMaxHealth(this.maxHealth * 2);
            }
            if (this.health != null) player.setHealth(Math.min(this.health * 2, player.getMaxHealth()));
        }

        return walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "better model disguise " + this.target.toString(event, debug) + " as " + this.modelName;
    }
}
