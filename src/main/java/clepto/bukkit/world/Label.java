package clepto.bukkit.world;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class Label extends Location {

	private final String name;
	private final String tag;

	public Label(String name, String tag, World world, double x, double y, double z) {
		super(world, x, y, z);
		this.name = name;
		this.tag = tag;
	}

	public int getTagInt() {
		validateTag();
		try {
			return Integer.parseInt(tag);
		} catch (NumberFormatException ex) {
			throw exception("Invalid int number '" + tag + "'");
		}
	}

	public double getTagDouble() {
		validateTag();
		try {
			return Double.parseDouble(tag);
		} catch (NumberFormatException ex) {
			throw exception("Invalid double number '" + tag + "'");
		}
	}

	public float getTagFloat() {
		validateTag();
		try {
			return Float.parseFloat(tag);
		} catch (NumberFormatException ex) {
			throw exception("Invalid float number '" + tag + "'");
		}
	}

	private void validateTag() {
		if (tag == null) throw exception("Null tag");
	}

	private WorldConfigurationException exception(String reason) {
		return new WorldConfigurationException(reason + " on label " + name + " at " + getCoords());
	}

	public String getCoords() {
		return getBlockX() + ", " + getBlockY() + ", " + getBlockZ();
	}

	@Override
	public String toString() {
		return "Label{name=" + name + ",tag=" + tag + ",x=" + x + ",y=" + y + ",z=" + z + "}";
	}

}
