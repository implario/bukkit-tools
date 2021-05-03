package clepto.bukkit.mobs;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.UnsafeList;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;

@Getter
public class EntityBuilder {

    private EntityType type;
    private EntityLiving living;

    public EntityBuilder(EntityType type) {
        if (!type.living)
            throw new IllegalArgumentException("Entity can`t be not living.");
        this.type = type;
        try {
            this.living = (EntityLiving) type.clazz.newInstance();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public EntityBuilder(CleptoEntityHolder living) {
        this(living.getEntity());
    }

    public EntityBuilder(EntityLiving living) {
        this.living = living;
    }

    public EntityBuilder name(String name) {
        living.setCustomName(name);
        return this;
    }

    public EntityBuilder nameVisible(boolean visible) {
        living.setCustomNameVisible(true);
        return this;
    }

    public EntityBuilder collide(boolean collide) {
        living.collides = collide;
        return this;
    }

    public EntityBuilder damage(double damage) {
        living.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
        return this;
    }

    public EntityBuilder speed(double speed) {
        living.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
        return this;
    }

    public EntityBuilder attackRange(double range) {
        living.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(range);
        return this;
    }

    public EntityBuilder addDamage(double damage) {
        AttributeInstance instance = living.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
        instance.setValue(instance.getValue() + damage);
        return this;
    }

    public EntityBuilder addSpeed(double speed) {
        AttributeInstance instance = living.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        instance.setValue(instance.getValue() + speed);
        return this;
    }

    public EntityBuilder addAttackRange(double range) {
        AttributeInstance instance = living.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        instance.setValue(instance.getValue() + range);
        return this;
    }

    public EntityBuilder maxHealth(double health) {
        living.getAttributeInstance(GenericAttributes.maxHealth).setValue(health);
        return this;
    }

    public EntityBuilder addMaxHealth(double health) {
        AttributeInstance instance = living.getAttributeInstance(GenericAttributes.maxHealth);
        instance.setValue(instance.getValue() + health);
        return this;
    }

    public EntityBuilder doHostile() {
        EntityCreature creature = (EntityCreature) living;
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);

            bField.set(creature.targetSelector, new UnsafeList<>());
            cField.set(creature.targetSelector, new UnsafeList<>());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        creature.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(creature, EntityHuman.class, true));
        return this;
    }

    public EntityBuilder armor(EnumItemSlot slot, ItemStack armor) {
        living.setEquipment(slot, CraftItemStack.asNMSCopy(armor));
        return this;
    }

    public EntityBuilder clearPathfinding() {
        EntityCreature creature = (EntityCreature) living;
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);

            bField.set(creature.goalSelector, new UnsafeList<>());
            bField.set(creature.targetSelector, new UnsafeList<>());
            cField.set(creature.goalSelector, new UnsafeList<>());
            cField.set(creature.targetSelector, new UnsafeList<>());
        } catch (Exception e) {
            e.printStackTrace();
        }

        creature.goalSelector.a(0, new PathfinderGoalFloat(creature));
        if (living instanceof EntityVillager)
            creature.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer((EntityVillager) creature));
        creature.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(creature, 0.6D));
        creature.goalSelector.a(9, new PathfinderGoalInteract(creature, EntityHuman.class, 3.0F, 1.0F));
        creature.goalSelector.a(9, new PathfinderGoalInteract(creature, EntityVillager.class, 5.0F, 0.02F));
        creature.goalSelector.a(9, new PathfinderGoalRandomStroll(creature, 0.6D));
        creature.goalSelector.a(10, new PathfinderGoalLookAtPlayer(creature, EntityHuman.class, 8.0F));
        return this;
    }

    public EntityBuilder dropExp(int count) {
        living.expToDrop = count;
        return this;
    }

    public EntityBuilder addDrop(ItemStack... items) {
        living.drops.addAll(Arrays.asList(items));
        return this;
    }

    public EntityBuilder canPickup(boolean value) {
        living.canPickUpLoot = value;
        return this;
    }

    public EntityBuilder removeFarAway(boolean remove) {
        ((CraftLivingEntity) living.bukkitEntity).setRemoveWhenFarAway(remove);
        return this;
    }

    public CleptoEntityHolder construct() {
        return new CleptoEntityHolder(living);
    }

    public CleptoEntityHolder build() {
        return construct();
    }

}
