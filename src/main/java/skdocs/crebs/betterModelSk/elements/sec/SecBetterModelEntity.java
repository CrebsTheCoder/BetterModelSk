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
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;

import java.util.List;
import java.util.Locale;

@Name("New Better Model Entity")
@Description({
        "Creates a better model entity template from a BetterModel id and a carrier mob type.",
        "Provide a section with 'model' (the BetterModel id) and 'mob' (the Bukkit entity type).",
        "Both values are read as raw text, so they do not need quotes."
})
@Example("""
        set {_mob} to a new better model entity:
            model: llama_pinata
            mob: llama
            health: 500
            max health: 500
            ai: false
        better model spawn {_mob} at location of player""")
@Since("1.0")
public class SecBetterModelEntity extends SectionExpression<BetterModelEntity> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(SecBetterModelEntity.class, BetterModelEntity.class)
                        .supplier(SecBetterModelEntity::new)
                        .priority(SyntaxInfo.SIMPLE)
                        .addPattern("[a] new better model entity")
                        .build());
    }

    private String modelName;
    private EntityType mobType;
    private @Nullable Double health;
    private @Nullable Double maxHealth;
    private @Nullable Boolean ai;
    private @Nullable Boolean gravity;
    private @Nullable Boolean invulnerable;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
                        ParseResult parseResult, @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {
        if (sectionNode == null) {
            Skript.error("A new better model entity requires a section with 'model' and 'mob' entries.");
            return false;
        }

        String model = null;
        String mob = null;

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
                    Skript.error("Unexpected line '" + (line == null ? "" : line)
                            + "'. Use 'key: value' entries such as 'model: <id>'.");
                    return false;
                }
                key = line.substring(0, separator);
                value = line.substring(separator + 1);
            }

            key = key.trim().toLowerCase(Locale.ROOT);
            value = value.trim();
            switch (key) {
                case "model" -> model = value;
                case "mob" -> mob = value;
                case "health" -> {
                    this.health = parseDouble(key, value);
                    if (this.health == null) return false;
                }
                case "max health", "maxhealth", "max_health" -> {
                    this.maxHealth = parseDouble(key, value);
                    if (this.maxHealth == null) return false;
                }
                case "ai", "has ai" -> {
                    this.ai = parseBoolean(key, value);
                    if (this.ai == null) return false;
                }
                case "gravity" -> {
                    this.gravity = parseBoolean(key, value);
                    if (this.gravity == null) return false;
                }
                case "invulnerable" -> {
                    this.invulnerable = parseBoolean(key, value);
                    if (this.invulnerable == null) return false;
                }
                default -> {
                    Skript.error("Unknown entry '" + key + "'. Expected one of: model, mob, health, "
                            + "max health, ai, gravity, invulnerable.");
                    return false;
                }
            }
        }

        if (model == null || model.isEmpty()) {
            Skript.error("Missing required entry 'model' (the BetterModel id, e.g. llama_pinata).");
            return false;
        }
        if (mob == null || mob.isEmpty()) {
            Skript.error("Missing required entry 'mob' (the carrier entity type, e.g. llama).");
            return false;
        }

        EntityType type = parseEntityType(mob);
        if (type == null) {
            Skript.error("'" + mob + "' is not a valid mob/entity type.");
            return false;
        }

        this.modelName = model;
        this.mobType = type;
        return true;
    }

    private static @Nullable EntityType parseEntityType(String raw) {
        String normalized = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        try {
            return EntityType.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
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

    @Override
    protected BetterModelEntity @Nullable [] get(Event event) {
        BetterModelEntity template = new BetterModelEntity(this.modelName, this.mobType);
        template.setHealth(this.health);
        template.setMaxHealth(this.maxHealth);
        template.setAi(this.ai);
        template.setGravity(this.gravity);
        template.setInvulnerable(this.invulnerable);
        return new BetterModelEntity[]{template};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends BetterModelEntity> getReturnType() {
        return BetterModelEntity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "a new better model entity (model: " + this.modelName + ", mob: " + this.mobType + ")";
    }
}
