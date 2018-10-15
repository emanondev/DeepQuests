package emanondev.quests.reward.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.IntegerPositiveAmountData;
import emanondev.quests.data.ItemStackData;
import emanondev.quests.inventory.InventoryUtils;
import emanondev.quests.inventory.InventoryUtils.ExcessManage;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.QuestComponent;

public class ItemStackRewardType extends AbstractRewardType implements RewardType {

	public ItemStackRewardType() {
		super("ITEMREWARD");
	}

	public class ItemStackReward extends AbstractReward implements Reward {
		private IntegerPositiveAmountData amount;
		private ItemStackData itemData;

		public ItemStackReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			this.itemData = new ItemStackData(section,this);
			this.amount = new IntegerPositiveAmountData(section,this);
		}
		
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			ItemStack item = itemData.getItem();
			if (item == null)
				info.add("&9Item: &cnot setted");
			else {
				info.add("&9Item:");
				info.add("  &9Type: &e"+item.getType());
				info.add("  &9Amount: &e"+amount.getAmount());
				if (item.hasItemMeta()) {
					ItemMeta meta = item.getItemMeta();
					if (meta.hasDisplayName())
						info.add("  &9Title: &f"+meta.getDisplayName());
					if (meta.hasLore()) {
						info.add("  &9Lore:");
						for(String str: meta.getLore()) {
							info.add("    &5"+str);
						}
					}
					if (meta.hasEnchants()) {
						info.add("  &9Enchants:");
						meta.getEnchants().forEach((ench,lv)->{
							info.add("    &e"+ench.getName()+" &9lv &e"+lv);
						});
					}
					if (meta.isUnbreakable())
						info.add("  &9Unbreakable: &etrue");
					if (meta.getItemFlags()!= null && !meta.getItemFlags().isEmpty()) {
						info.add("  &9Flags:");
						meta.getItemFlags().forEach((flag)->{
							info.add("    &e"+flag);
						});
					}
					if (meta instanceof SkullMeta && ((SkullMeta) meta).hasOwner()) {
						info.add("  &9Skull Owner: &e"+((SkullMeta) meta).getOwningPlayer().getName());
					}
				}
				if (item.getDurability()!=0)
					info.add("  &9Damage: &e"+item.getDurability());
				
			}
			return info;
		}

		@Override
		public void applyReward(QuestPlayer p,int times) {
			if (times<=0 || itemData.getItem()==null || amount.getAmount()<=0 )
				return;
			InventoryUtils.giveAmount(p.getPlayer(), itemData.getItem(), amount.getAmount()*times, ExcessManage.DROP_EXCESS);
		}

		@Override
		public RewardType getType() {
			return ItemStackRewardType.this;
		}
		
		public ItemStackData getItemStackData() {
			return itemData;
		}
		public IntegerPositiveAmountData getAmountData() {
			return amount;
		}

		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(0, amount.getAmountEditorButton(gui));
			gui.putButton(1, itemData.getItemSelectorButton(gui));
			return gui;
		}
	}
	
	@Override
	public Reward getInstance(ConfigSection m, QuestComponent parent) {
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
