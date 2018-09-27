package emanondev.quests.reward.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

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
		public String getInfo() {
			if (itemData.getItem()==null)
				return "&cno Item is set";
			if (itemData.getItem().hasItemMeta() && itemData.getItem().getItemMeta().hasDisplayName())
				return itemData.getItem().getItemMeta().getDisplayName()+" ("+itemData.getItem().getType()+") &ex"+amount;
			return itemData.getItem().getType()+" &ex"+amount;
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
			gui.putButton(9, amount.getAmountEditorButton(gui));
			gui.putButton(10, itemData.getItemSelectorButton(gui));
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
