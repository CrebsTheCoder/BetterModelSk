package skdocs.crebs.betterModelSk.api;

import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class BetterModelEntity {

    private final String modelName;
    private final EntityType mobType;

    private @Nullable Double health;
    private @Nullable Double maxHealth;
    private @Nullable Boolean ai;
    private @Nullable Boolean gravity;
    private @Nullable Boolean invulnerable;

    private @Nullable Entity entity;
    private @Nullable EntityTracker tracker;

    public BetterModelEntity(String modelName, EntityType mobType) {
        this.modelName = modelName;
        this.mobType = mobType;
    }

    public String getModelName() {
        return this.modelName;
    }

    public EntityType getMobType() {
        return this.mobType;
    }

    public @Nullable Double getHealth() {
        return this.health;
    }

    public void setHealth(@Nullable Double health) {
        this.health = health;
    }

    public @Nullable Double getMaxHealth() {
        return this.maxHealth;
    }

    public void setMaxHealth(@Nullable Double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public @Nullable Boolean getAi() {
        return this.ai;
    }

    public void setAi(@Nullable Boolean ai) {
        this.ai = ai;
    }

    public @Nullable Boolean getGravity() {
        return this.gravity;
    }

    public void setGravity(@Nullable Boolean gravity) {
        this.gravity = gravity;
    }

    public @Nullable Boolean getInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(@Nullable Boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public @Nullable Entity getEntity() {
        return this.entity;
    }

    public @Nullable EntityTracker getTracker() {
        return this.tracker;
    }

    public void setSpawned(Entity entity, @Nullable EntityTracker tracker) {
        this.entity = entity;
        this.tracker = tracker;
    }

    public boolean isSpawned() {
        return this.entity != null && !this.entity.isDead();
    }

    @Override
    public String toString() {
        return "better model entity (model: " + this.modelName + ", mob: "
                + this.mobType.getKey().getKey() + ")";
    }
}
