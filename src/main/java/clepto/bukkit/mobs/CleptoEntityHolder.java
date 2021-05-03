package clepto.bukkit.mobs;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.util.Vector;

@Getter
public class CleptoEntityHolder implements Cloneable {

    private final EntityLiving entity;

    public CleptoEntityHolder(EntityLiving entity) {
        this.entity = entity;
    }

    public CleptoEntityHolder setHealth(float value) {
        entity.setHealth(value);
        return this;
    }

    public CleptoEntityHolder damage(DamageSource source, float damage) {
        entity.damageEntity(source, damage);
        return this;
    }

    public CleptoEntityHolder addHealth(float count, EntityRegainHealthEvent.RegainReason reason) {
        entity.heal(count, reason);
        return this;
    }

    public CleptoEntityHolder setLocation(Location location) {
        entity.setLocation(location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
        return this;
    }

    public CleptoEntityHolder kill() {
        entity.killEntity();
        return this;
    }

    public CleptoEntityHolder kill(Player killer) {
        entity.killer = (EntityHuman) killer;
        entity.killEntity();
        return this;
    }

    public CleptoEntityHolder addEffects(MobEffect... effects) {
        for (MobEffect effect : effects)
            entity.addEffect(effect);
        return this;
    }

    public CleptoEntityHolder setRiding(Entity entity) {
        entity.startRiding(entity);
        return this;
    }

    public CleptoEntityHolder stopRiding() {
        entity.stopRiding();
        return this;
    }

    public CleptoEntityHolder addVector(Vector vector) {
        entity.bukkitEntity.getLocation().add(vector);
        return this;
    }

    public CleptoEntityHolder addVelocity(Vector vector) {
        return addVector(vector);
    }

    public CleptoEntityHolder lastDamager(EntityLiving damager) {
        entity.lastDamager = damager;
        return this;
    }

    public double getDamage() {
        return entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
    }

    public double getAttackRange() {
        return entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).getValue();
    }

    public double getSpeed() {
        return entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    public double getMaxHealth() {
        return entity.getAttributeInstance(GenericAttributes.maxHealth).getValue();
    }

    public EntityBuilder builder() {
        return new EntityBuilder(this);
    }

    public CleptoEntityHolder spawn(Location location) {
        entity.setLocation(location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return this;
    }

}
