package skdocs.crebs.betterModelSk.api.util;

import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DisguiseState {

    private DisguiseState() {
    }

    private static final Map<UUID, Double> ORIGINAL_MAX_HEALTH = new ConcurrentHashMap<>();

    @SuppressWarnings("deprecation")
    public static void rememberMaxHealth(LivingEntity entity) {
        ORIGINAL_MAX_HEALTH.putIfAbsent(entity.getUniqueId(), entity.getMaxHealth());
    }

    @SuppressWarnings("deprecation")
    public static void restore(LivingEntity entity) {
        Double original = ORIGINAL_MAX_HEALTH.remove(entity.getUniqueId());
        if (original != null) entity.setMaxHealth(original);
    }
}
