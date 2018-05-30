package emanondev.quests.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;

public class RewardGui extends CustomLinkedGui<CustomButton>{
	private AbstractReward reward;
	private ParentButton parentButton;
	private int parentButtonPos = 1;
	public RewardGui(Player p, AbstractReward reward, CustomGui previusHolder,
			HashMap<Integer, EditorButtonFactory> tools,String title) {
		super(p, previusHolder, 6);
		for (Integer key : tools.keySet()) {
			CustomButton button = tools.get(key).getCustomButton(this);
			if (button!=null)
				this.addButton(key,button);
		}
		this.reward = reward;
		this.setFromEndCloseButtonPosition(8);
		parentButton = new ParentButton();
		//TODO updateTitle();
		reloadInventory();
	}

	protected boolean setFromEndParentButtonPosition(int i) {
		if (i>0 && i <= size() && i!=parentButtonPos) {
			if (getInventory().getItem(size()-parentButtonPos).equals(parentButton.getItem()))
				getInventory().setItem(size()-parentButtonPos,null);
			parentButtonPos=i;
			
			getInventory().setItem(size()-parentButtonPos,parentButton.getItem());
			return true;
		}
		return false;
	}
	public class ParentButton extends CustomButton {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public ParentButton() {
			super(RewardGui.this);
			this.item = new ItemStack(Material.BOOK);
			ItemMeta meta = item.getItemMeta();
			if (reward.getParent() instanceof Task) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Task"));
			}
			else if (reward.getParent() instanceof Mission) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Mission"));
			}
			else if (reward.getParent() instanceof Quest) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quest"));
			}
			item.setItemMeta(meta);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (reward.getParent() instanceof Task) {
				((Task) reward.getParent()).openEditorGui(clicker, RewardGui.this);
				return;
			}
			else if (reward.getParent() instanceof Mission) {
				((Mission) reward.getParent()).openEditorGui(clicker, RewardGui.this);
				return;
			}
			else if (reward.getParent() instanceof Quest) {
				((Quest) reward.getParent()).openEditorGui(clicker, RewardGui.this);
				return;
			}
		}
	}
}
