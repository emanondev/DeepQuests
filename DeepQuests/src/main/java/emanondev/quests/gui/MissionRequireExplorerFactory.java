package emanondev.quests.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.mission.Mission;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.utils.StringUtils;

public class MissionRequireExplorerFactory implements EditorButtonFactory {
	private final Mission mission;
	private final String title;

	public MissionRequireExplorerFactory(Mission mission, String title) {
		this.mission = mission;
		this.title = title;
	}

	private class MissionRequireExplorerButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);

		public MissionRequireExplorerButton(CustomGui parent) {
			super(parent);
			update();
		}
		public void update() {
			item.setAmount(Math.max(1, Math.min(127, mission.getRequires().size())));
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSelect/Show requires");
			desc.add("&6Click to Select a require to edit");
			if (mission.getRequires().size()>0)
				for (MissionRequire require: mission.getRequires())
					desc.add("&7"+require.getInfo());
			StringUtils.setDescription(item, desc);
		}
		@Override
		public ItemStack getItem() {
			if (mission.getRequires().isEmpty())
				return null;
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (mission.getRequires().isEmpty())
				return;
			clicker.openInventory(new RequireExplorer(clicker, this.getParent()).getInventory());
		}

		private class RequireExplorer extends CustomMultiPageGui<CustomButton> {

			public RequireExplorer(Player p, CustomGui parent) {
				super(p, parent, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (MissionRequire require : mission.getRequires())
					addButton(new RequireButton(require));
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}

			private class RequireButton extends CustomButton {
				private final MissionRequire req;
				private final ItemStack item;

				public RequireButton(MissionRequire require) {
					super(RequireExplorer.this);
					this.req = require;
					this.item = new ItemStack(req.getRequireType().getGuiItemMaterial());
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6"+req.getDescription());
					StringUtils.setDescription(item, desc);
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					req.openEditorGui(clicker, MissionRequireExplorerButton.this.getParent());
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new MissionRequireExplorerButton(parent);
	}
}