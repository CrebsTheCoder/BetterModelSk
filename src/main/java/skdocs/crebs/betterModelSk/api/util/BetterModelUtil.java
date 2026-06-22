package skdocs.crebs.betterModelSk.api.util;

import kr.toxicity.model.api.bukkit.entity.BaseBukkitEntity;
import kr.toxicity.model.api.nms.HitBox;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class BetterModelUtil {

    private BetterModelUtil() {
    }

    public static @Nullable EntityTrackerRegistry registryOf(Entity entity) {
        UUID uuid = entity.getUniqueId();

        EntityTrackerRegistry direct = EntityTrackerRegistry.registry(uuid);
        if (direct != null) return direct;

        for (EntityTrackerRegistry registry : EntityTrackerRegistry.registries()) {
            for (HitBox hitBox : registry.hitBoxes()) {
                if (uuid.equals(hitBox.uuid())) return registry;
            }
        }
        return null;
    }

    public static @Nullable Entity sourceEntity(EntityTrackerRegistry registry) {
        if (registry.entity() instanceof BaseBukkitEntity bukkit) return bukkit.entity();
        return null;
    }
}
