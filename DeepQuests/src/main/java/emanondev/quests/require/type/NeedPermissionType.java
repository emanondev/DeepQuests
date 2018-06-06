package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.TextEditorButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.QuestRequire;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadableWithCooldown;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class NeedPermissionType extends AbstractRequireType implements RequireType {
	private final static String ID = "PERMISSION";
	private final static String PATH_PERMISSION = "permission";
	private static final BaseComponent[] changeTitleDesc = new ComponentBuilder(ChatColor.GOLD
			+ "Click suggest and type the permission\n\n"
			+ ChatColor.YELLOW 	+ "/questtext <permission>")
					.create();

	public NeedPermissionType() {
		super(ID);
	}

	@Override
	public Require getRequireInstance(MemorySection section, Mission mission) {
		return new NeedPermission(section, mission);
	}

	public class NeedPermission extends AbstractRequire implements Require {
		private String permission;

		public NeedPermission(MemorySection section, YmlLoadableWithCooldown gui) {
			super(section, gui);
			this.permission = getSection().getString(PATH_PERMISSION);
			if (this.permission != null)
				this.permission = this.permission.toLowerCase();
			this.addToEditor(8, new PermissionEditorButtonFactory());
		}

		@Override
		public boolean isAllowed(QuestPlayer p) {
			if (permission== null || permission.isEmpty())
				return true;
			return p.getPlayer().hasPermission(permission);
		}

		@Override
		public RequireType getRequireType() {
			return NeedPermissionType.this;
		}

		public String getKey() {
			return getRequireType().getKey();
		}

		public String getPermission() {
			return permission;
		}

		public boolean setPermission(String permission) {
			if (!permission.isEmpty() && permission.contains(" "))
				return false;

			if (permission.isEmpty())
				permission = null;
			this.permission = permission;
			getSection().set(PATH_PERMISSION, this.permission);
			getParent().setDirty(true);
			return true;
		}

		private class PermissionEditorButtonFactory implements EditorButtonFactory {
			private class PermissionEditorButton extends TextEditorButton {
				private ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);

				public PermissionEditorButton(CustomGui parent) {
					super(parent);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lPermission Editor"));
					item.setItemMeta(meta);
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				public void update() {
					ItemMeta meta = item.getItemMeta();
					ArrayList<String> lore = new ArrayList<String>();
					lore.add("&6Click to edit");
					lore.add("&7Current Permission '&r" + getPermission() + "&7'");
					meta.setLore(StringUtils.fixColorsAndHolders(lore));
					item.setItemMeta(meta);
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					this.requestText(clicker, StringUtils.revertColors(getPermission()), changeTitleDesc);
				}

				@Override
				public void onReicevedText(String text) {
					if (text == null)
						text = "";
					if (setPermission(text)) {
						update();
						getParent().reloadInventory();
					} else
						getOwner().sendMessage(
								StringUtils.fixColorsAndHolders("&cSelected permission was not a valid permission"));

				}
			}

			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new PermissionEditorButton(parent);
			}
		}
	}

	@Override
	public Require getRequireInstance(MemorySection section, YmlLoadableWithCooldown gui) {
		return new NeedPermission(section, gui);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.TRIPWIRE_HOOK;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has the selected permission");
		return desc;
	}

	@Override
	public QuestRequire getRequireInstance(MemorySection section, Quest quest) {
		return new NeedPermission(section, quest);
	}

}
