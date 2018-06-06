package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.require.QuestRequire;
import emanondev.quests.utils.StringUtils;

public class QuestRequireExplorerFactory implements EditorButtonFactory {
	private final Collection<QuestRequire> requires;
	private final String title;

	public QuestRequireExplorerFactory(Collection<QuestRequire> requires, String title) {
		this.requires = requires;
		this.title = title;
	}

	private class QuestRequireExplorerButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);

		public QuestRequireExplorerButton(CustomGui parent) {
			super(parent);
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSelect/Show requires");
			desc.add("&6Click to Select a require to edit");
			StringUtils.setDescription(item, desc);
			update();
		}
		public void update() {
			item.setAmount(Math.max(1, Math.min(127, requires.size())));
		}

		@Override
		public ItemStack getItem() {
			if (requires.isEmpty())
				return null;
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (requires.isEmpty())
				return;
			clicker.openInventory(new RequireExplorer(clicker, this.getParent()).getInventory());
		}

		private class RequireExplorer extends CustomMultiPageGui<CustomButton> {

			public RequireExplorer(Player p, CustomGui parent) {
				super(p, parent, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (QuestRequire require : requires)
					addButton(new RequireButton(require));
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}

			private class RequireButton extends CustomButton {
				private final QuestRequire req;
				private final ItemStack item;

				public RequireButton(QuestRequire require) {
					super(RequireExplorer.this);
					this.req = require;
					this.item = new ItemStack(req.getRequireType().getGuiItemMaterial());
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6"+req.getDescription());
					desc.add("&7"+req.getRequireType().getKey());
					StringUtils.setDescription(item, desc);
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					req.openEditorGui(clicker, QuestRequireExplorerButton.this.getParent());
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new QuestRequireExplorerButton(parent);
	}

}
