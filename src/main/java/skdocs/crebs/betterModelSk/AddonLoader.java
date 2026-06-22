package skdocs.crebs.betterModelSk;

// inspired from https://raw.githubusercontent.com/3add/PacketEventsSK/refs/heads/main/src/main/java/dev/threeadd/packeteventssk/AddonLoader.java
import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.elements.conditions.CondIsBetterModel;
import skdocs.crebs.betterModelSk.elements.effects.EffBetterModelAnimate;
import skdocs.crebs.betterModelSk.elements.effects.EffBetterModelRemove;
import skdocs.crebs.betterModelSk.elements.effects.EffBetterModelSpawn;
import skdocs.crebs.betterModelSk.elements.effects.EffBetterModelTeleport;
import skdocs.crebs.betterModelSk.elements.effects.EffBetterModelUndisguise;
import skdocs.crebs.betterModelSk.elements.expressions.ExprBetterModelId;
import skdocs.crebs.betterModelSk.elements.expressions.ExprBetterModelSource;
import skdocs.crebs.betterModelSk.elements.expressions.ExprLastSpawnedBetterModel;
import skdocs.crebs.betterModelSk.elements.sec.SecBetterModelDisguise;
import skdocs.crebs.betterModelSk.elements.sec.SecBetterModelEntity;
import skdocs.crebs.betterModelSk.elements.types.BetterModelEntityType;

import java.util.List;
import java.util.function.Consumer;

public final class AddonLoader {

    private final BetterModelSk plugin;
    private final Plugin skriptPlugin;

    private SkriptAddon addon;

    private static final List<Runnable> TYPES = List.of(
            BetterModelEntityType::register
    );

    private static final List<Consumer<SyntaxRegistry>> SECTIONS = List.of(
            SecBetterModelEntity::register,
            SecBetterModelDisguise::register
    );

    private static final List<Consumer<SyntaxRegistry>> EFFECTS = List.of(
            EffBetterModelSpawn::register,
            EffBetterModelAnimate::register,
            EffBetterModelRemove::register,
            EffBetterModelTeleport::register,
            EffBetterModelUndisguise::register
    );

    private static final List<Consumer<SyntaxRegistry>> CONDITIONS = List.of(
            CondIsBetterModel::register
    );

    private static final List<Consumer<SyntaxRegistry>> EXPRESSIONS = List.of(
            ExprBetterModelSource::register,
            ExprBetterModelId::register,
            ExprLastSpawnedBetterModel::register
    );

    public AddonLoader(BetterModelSk plugin) {
        this.plugin = plugin;
        this.skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
    }

    public SkriptAddon getAddon() {
        return this.addon;
    }

    public boolean canLoad() {
        if (skriptPlugin == null || !skriptPlugin.isEnabled()) {
            plugin.getLogger().severe("Skript plugin not found or is disabled, Skript elements cannot load");
            return false;
        }

        if (!Skript.isAcceptRegistrations()) {
            plugin.getLogger().severe("Skript is no longer accepting registrations, BetterModelSk can no longer load");
            return false;
        }

        if (isPlugmanReloaded()) {
            plugin.getLogger().severe("BetterModelSk does not support reloading with PlugMan, stuff will break!");
            return false;
        }

        this.addon = Skript.instance().registerAddon(BetterModelSk.class, "BetterModelSk");
        SyntaxRegistry registry = this.addon.syntaxRegistry();

        TYPES.forEach(Runnable::run);
        loadElements(SECTIONS, registry);
        loadElements(EFFECTS, registry);
        loadElements(CONDITIONS, registry);
        loadElements(EXPRESSIONS, registry);

        int typeCount = TYPES.size();
        int sectionCount = SECTIONS.size();
        int effectCount = EFFECTS.size();
        int conditionCount = CONDITIONS.size();
        int expressionCount = EXPRESSIONS.size();
        int total = typeCount + sectionCount + effectCount + conditionCount + expressionCount;

        plugin.getLogger().info(String.format("Loaded %s BetterModelSk elements", total));
        plugin.getLogger().info(String.format(" - %s types", typeCount));
        plugin.getLogger().info(String.format(" - %s sections", sectionCount));
        plugin.getLogger().info(String.format(" - %s effects", effectCount));
        plugin.getLogger().info(String.format(" - %s conditions", conditionCount));
        plugin.getLogger().info(String.format(" - %s expressions", expressionCount));

        return true;
    }

    private void loadElements(List<Consumer<SyntaxRegistry>> elements, SyntaxRegistry registry) {
        for (Consumer<SyntaxRegistry> element : elements) {
            try {
                element.accept(registry);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load a BetterModelSk element", e);
            }
        }
    }

    private boolean isPlugmanReloaded() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.toString().contains("rylinaux.plugman.command."))
                return true;
        }
        return false;
    }
}
