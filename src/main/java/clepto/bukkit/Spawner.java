package clepto.bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Consumer;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class Spawner<T extends LivingEntity> {

	private Class<T> type;
	private double radius;
	private Location center;
	private int limit;
	private Consumer<T> compositor;

	public void tick() {
		Collection<T> entities = center.getNearbyEntitiesByType(type, radius * 2);
		int amount = entities.size();
		if (amount >= limit) return;
		Location placeForSpawn = getPlaceForSpawn();
		if (!placeForSpawn.isChunkLoaded()) return;
		center.getWorld().spawn(placeForSpawn, type, compositor);
	}

	public Location getPlaceForSpawn() {
		Location loc = center.clone().add(B.randomVector().setY(0));
		while (loc.getBlock().getType() != Material.AIR) loc.add(0, 1, 0);
		return loc;
	}

	public boolean isInside(Location location) {
		if (center.getWorld() != location.getWorld()) return false;
		double x = location.getX() - center.getX();
		double z = location.getZ() - center.getZ();
		return x * x + z * z <= radius * radius * 4;
	}

}

