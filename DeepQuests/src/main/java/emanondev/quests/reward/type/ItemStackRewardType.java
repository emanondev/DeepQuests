package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.button.AmountEditorButtonFactory;
import emanondev.quests.gui.button.ItemEditorButton;
import emanondev.quests.inventory.InventoryUtils;
import emanondev.quests.inventory.InventoryUtils.ExcessManage;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ItemStackRewardType extends AbstractRewardType implements RewardType {

	public ItemStackRewardType() {
		super("ITEMREWARD");
	}

	public class ItemStackReward extends AbstractReward implements Reward {
		private final static String PATH_ITEMSTACK = "itemstack";
		private final static String PATH_AMOUNT = "amount";
		private ItemStack item;
		private int amount;

		public ItemStackReward(MemorySection section, YmlLoadable parent) {
			super(section, parent);
			this.item = section.getItemStack(PATH_ITEMSTACK, null);
			this.amount = section.getInt(PATH_AMOUNT, 1);
			this.addToEditor(9, new ItemEditorButtonFactory());
			this.addToEditor(10, new ItemStackAmountButtonFactory());
		}
		public String getInfo() {
			if (item==null)
				return "&cno Item is set";
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
				return item.getItemMeta().getDisplayName()+" ("+item.getType()+") &ex"+amount;
			return item.getType()+" &ex"+amount;
		}

		@Override
		public void applyReward(QuestPlayer p,int times) {
			if (times<=0)
				return;
			if (item != null && amount > 0)
				InventoryUtils.giveAmount(p.getPlayer(), item, amount*times, ExcessManage.DROP_EXCESS);
		}

		@Override
		public RewardType getType() {
			return ItemStackRewardType.this;
		}

		public boolean setItem(ItemStack item) {
			if (this.item == null)
				if (item == null)
					return false;
				else
					this.item = new ItemStack(item);
			else if (item == null)
				this.item = null;
			else if (this.item.isSimilar(item))
				return false;
			else
				this.item = new ItemStack(item);
			if (this.item != null)
				this.item.setAmount(1);
			getSection().set(PATH_ITEMSTACK, this.item);
			getParent().setDirty(true);
			return true;
		}

		public ItemStack getItem() {
			return new ItemStack(item);
		}

		public EditorButtonFactory getItemEditorButtonFactory() {
			return new ItemEditorButtonFactory();
		}

		private class ItemEditorButtonFactory implements EditorButtonFactory {
			private class EditEntityTypeButton extends ItemEditorButton {
				private ItemStack item;

				public EditEntityTypeButton(CustomGui parent) {
					super(parent);
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				public void update() {
					if (ItemStackReward.this.item == null)
						this.item = new ItemStack(Material.BARRIER);
					else
						this.item = new ItemStack(ItemStackReward.this.item);
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lItem Editor");
					desc.add("&6Click to edit");
					if (ItemStackReward.this.item == null)
						desc.add("&7No item is set");
					else {
						desc.add("&6Current Item is the item showed with");
						desc.add("&6the following name and lore:");
						if (ItemStackReward.this.item.hasItemMeta()) {
							ItemMeta meta = ItemStackReward.this.item.getItemMeta();
							if (meta.hasDisplayName())
								desc.add(meta.getDisplayName());
							else
								desc.add("");

							if (meta.hasLore())
								desc.addAll(desc.size(), meta.getLore());
						}
					}
					StringUtils.setDescription(item, desc);
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					this.requestItem(clicker, changeTitleDesc);
				}

				@Override
				public void onReicevedItem(ItemStack item) {
					if (setItem(item)) {
						update();
						getParent().reloadInventory();
					} else
						getOwner().sendMessage(StringUtils.fixColorsAndHolders("&cSelected item was not a valid item"));
				}

			}

			@Override
			public EditEntityTypeButton getCustomButton(CustomGui parent) {
				return new EditEntityTypeButton(parent);
			}
		}

		private class ItemStackAmountButtonFactory extends AmountEditorButtonFactory {

			public ItemStackAmountButtonFactory() {
				super("&8Amount Editor", Material.DIODE);
			}

			@Override
			protected boolean onChange(int amount) {
				return setAmount(amount);
			}

			@Override
			protected int getAmount() {
				return amount;
			}

			@Override
			protected ArrayList<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lAmount Editor");
				desc.add("&6Click to edit");
				desc.add("&7Amount is &e" + getAmount());
				return desc;
			}
		}

		public int getAmount() {
			return amount;
		}

		public boolean setAmount(int value) {
			value = Math.max(1, value);
			if (this.amount == value)
				return false;
			this.amount = value;
			getSection().set(PATH_AMOUNT, amount);
			ItemStackReward.this.getParent().setDirty(true);
			return true;
		}
	}

	private final static BaseComponent[] changeTitleDesc = new ComponentBuilder(
			ChatColor.GOLD + "Click to set the item in your main hand").create();

	@Override
	public Reward getInstance(MemorySection m, YmlLoadable parent) {
		return new ItemStackReward(m, parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.GOLD_INGOT;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Give the selected item with selected amount");
	}

}
