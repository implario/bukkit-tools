package clepto.bukkit.gui;

import lombok.Data;

@Data
public class GuiContext {

	private Gui openedGui;
	private String payload;
	private boolean opened;

	public void clear() {
		setOpenedGui(null);
		setPayload(null);
		setOpened(false);
	}

}
