package clepto.bukkit;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import java.util.HashMap;
import java.util.Map;

public class LocalArmorStand {

	@Getter
	private final CraftPlayer owner;

	@Getter
	private Location location;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private Vector3f pose;


	private Map<EnumItemSlot, ItemStack> equipment = new HashMap<>();

	@Getter
	private EntityArmorStand handle;

	public LocalArmorStand(CraftPlayer owner) {
		this.owner = owner;
	}

	public LocalArmorStand equip(EnumItemSlot slot, org.bukkit.inventory.ItemStack bukkitItem) {
		ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
		equipment.put(slot, nmsItem);
		return this;
	}

	public int getEntityId() {
		return handle.id;
	}

	public void setLocation(Location location) {
		this.setLocation(location, false);
	}

	public void setLocation(Location location, boolean checkCollisions) {
		if (this.handle != null) {
			if (checkCollisions) {
				this.handle.move(EnumMoveType.SELF,
						location.getX() - this.handle.locX,
						location.getY() - this.handle.locY,
						location.getZ() - this.handle.locZ
								);
				location = new Location(location.getWorld(), this.handle.locX, this.handle.locY, this.handle.locZ, location.getYaw(), location.getPitch());
			} else this.handle.setPosition(location.getX(), location.getY(), location.getZ());
			owner.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(handle));
		}
		this.location = location;
	}

	public void spawn() {
		handle = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
		handle.valid = false;
		handle.setPosition(location.getX(), location.getY(), location.getZ());
		if (name != null) handle.setCustomName(name);
		handle.setCustomNameVisible(true);
		handle.setBasePlate(true);
		handle.setArms(true);
		ItemStack item = new ItemStack(Blocks.STONE);
		handle.setEquipment(EnumItemSlot.MAINHAND, item);
		if (pose != null) handle.setHeadPose(pose);
		//		ArmorStand bukkit = ((ArmorStand) handle.getBukkitEntity()).setBodyPose(new EulerAngle());
		handle.valid = true;
		owner.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntity(handle, 78));
		B.postpone(2, this::update);
	}

	private void update() {
		PlayerConnection net = owner.getHandle().playerConnection;
		net.sendPacket(new PacketPlayOutEntityTeleport(handle));
		net.sendPacket(new PacketPlayOutEntityMetadata(handle.getId(), handle.getDataWatcher(), false));
		equipment.forEach((slot, item) -> {
			if (item != null) net.sendPacket(new PacketPlayOutEntityEquipment(handle.getId(), slot, item));
		});
	}

	public void remove() {
		if (handle != null) {
			owner.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(handle.getId()));
		}
	}

}

