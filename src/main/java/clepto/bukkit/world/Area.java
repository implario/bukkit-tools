package clepto.bukkit.world;

import implario.ListUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Area {

	Collection<Label> getLabels();

	CraftWorld getWorld();

	String getName();

	String getTag();

	boolean contains(Location location);

	default List<Label> getLabels(String name) {
		return ListUtils.filter(this.getLabels(), label -> label.getName().equals(name));
	}

	default List<Label> getLabels(String name, String tag) {
		return ListUtils.filter(this.getLabels(), label -> label.getName().equals(name) && label.getTag().equals(tag));
	}

	default Label getLabel(String name) {
		for (Label label : this.getLabels())
			if (label.getName().equals(name)) return label;
		return null;
	}

	default Label requireLabel(String name) {
		Label result = null;
		for (Label label : this.getLabels()) {
			if (!label.getName().equals(name)) continue;
			if (result != null)
				throw new WorldConfigurationException("Duplicate label " + name + " inside box " + this.getName() + "/" + this.getTag() +
						" on " + label.getCoords() + " and " + result.getCoords());

			result = label;
		}
		if (result == null)
			throw new WorldConfigurationException("No label '" + name + "' was found inside box '" + this.getName() + "/" + this.getTag() + "'");

		return result;
	}

	default Map<String, Box> getBoxes(String name) {
		return this.getLabels().stream()
				.filter(label -> label.getName().equals(name))
				.collect(Collectors.groupingBy(Label::getTag))
				.entrySet().stream()
				.map(e -> findBoxSmart(e.getValue(), name, e.getKey()))
				.collect(Collectors.toMap(Box::getTag, box -> box));
	}

	default Box getBox(String name, String tag) {
		List<Label> labels = this.getLabels().stream()
				.filter(label -> label.getName().equals(name))
				.filter(label -> label.getTag().equals(tag))
				.collect(Collectors.toList());

		return findBoxSmart(labels, name, tag);

	}

	default Box findBoxSmart(List<Label> labels, String name, String tag) {
		if (labels.isEmpty()) throw new WorldConfigurationException("Box " + name + "/" + tag + " wasn't found on " + this.getName());

		Iterator<Label> iterator = labels.iterator();

		Location min = iterator.next().clone();
		Location max = min.clone();

		while (iterator.hasNext()) {
			Location loc = iterator.next();
			if (loc.getBlockX() > max.getBlockX()) max.setX(loc.getBlockX());
			if (loc.getBlockY() > max.getBlockY()) max.setY(loc.getBlockY());
			if (loc.getBlockZ() > max.getBlockZ()) max.setZ(loc.getBlockZ());

			if (loc.getBlockX() < min.getBlockX()) min.setX(loc.getBlockX());
			if (loc.getBlockY() < min.getBlockY()) min.setY(loc.getBlockY());
			if (loc.getBlockZ() < min.getBlockZ()) min.setZ(loc.getBlockZ());
		}

		return new Box(this, min, max, name, tag);
	}

	default void sendMessage(String message) {
		for (Player player : getWorld().getPlayers()) {
			if (contains(player.getLocation())) player.sendMessage(message);
		}
	}



}
