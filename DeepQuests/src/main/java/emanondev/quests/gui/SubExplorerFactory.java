package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.WithGui;

public class SubExplorerFactory<T extends WithGui> implements EditorButtonFactory {
	public SubExplorerFactory(Class<T> type,Collection<T> coll,String title) {
		this.coll = coll;
		this.type = type;
		if (title!=null)
			this.title = title;
		else
			this.title = "";
	}
	private final Collection<T> coll;
	private final Class<T> type;
	private final String title;

	public class SubExplorerButton extends CustomButton {
		public SubExplorerButton(CustomGui parent) {
			super(parent);
			item.setAmount(Math.max(1,Math.min(127,coll.size())));
			ArrayList<String> desc = new ArrayList<String>();
			if (type.isAssignableFrom(Task.class)) {
				desc.add("&6&lTasks Menu");
				desc.add("&6Click to Select a task to edit");
			}
			else if (type.isAssignableFrom(Mission.class)) {
				desc.add("&6&lMissions Menu");
				desc.add("&6Click to Select a mission to edit");
			}
			else if (type.isAssignableFrom(Quest.class)) {
				desc.add("&6&lQuests Menu");
				desc.add("&6Click to Select a quest to edit");
			}
			StringUtils.setDescription(item,desc);
		}
		private ItemStack item = new ItemStack(Material.PAINTING);
		@Override
		public ItemStack getItem() {
			return item;
		}
	
		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new WGExplorerGui(clicker,getParent(),coll).getInventory());
		}
		private class WGExplorerGui extends CustomMultiPageGui<CustomButton>{
	
			public WGExplorerGui(Player p, CustomGui previusHolder, Collection<T> coll) {
				super(p, previusHolder, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (T ld : coll)
					addButton(new WGButton(ld));
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}
			
			
			private class WGButton extends CustomButton {
				private final T ld;
				public WGButton(T ld) {
					super(WGExplorerGui.this);
					this.ld = ld;
					if (ld instanceof Quest)
						item = new ItemStack(Material.BOOK);
					else if (ld instanceof Mission)
						item = new ItemStack(Material.PAPER);
					else if (ld instanceof Task)
						item = new ItemStack(((Task) ld).getTaskType().getGuiItemMaterial());
					update();
				}
				private ItemStack item;
				@Override
				public ItemStack getItem() {
					return item;
				}
				public void update() {
					ArrayList<String> desc = new ArrayList<String>();
					if (type.isAssignableFrom(Task.class)) {
						desc.add("&6Task: '&e"+ld.getDisplayName()+"&6'");
						desc.add("&6Click to open editor");
						desc.add("");
						desc.add("&7("+((Task) ld).getTaskType().getKey()+")");
					}
					else if (type.isAssignableFrom(Mission.class)) {
						desc.add("&6Mission: '&e"+ld.getDisplayName()+"&6'");
						desc.add("&6Click to open editor");
						desc.add("");
						desc.add("&7 contains &e"+((Mission) ld).getTasks().size()+" &7tasks");
						for (Task task : ((Mission) ld).getTasks()) {
							desc.add("&7 - &e"+task.getDisplayName()+" &7("+task.getTaskType().getKey()+")");
						}
					}
					else if (type.isAssignableFrom(Quest.class)) {
						desc.add("&7Quest: '&e"+ld.getDisplayName()+"&6'");
						desc.add("&6Click to open editor");
						desc.add("");
						desc.add("&7 contains &e"+((Quest) ld).getMissions().size()+" &7missions");
						for (Mission mission : ((Quest) ld).getMissions()) {
							desc.add("&7 - &e"+mission.getDisplayName());
						}
					}
					StringUtils.setDescription(item,desc);
				}
				@Override
				public void onClick(Player clicker, ClickType click) {
					ld.openEditorGui(clicker, WGExplorerGui.this.getPreviusHolder());			
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		if (coll.isEmpty())
			return null;
		return new SubExplorerButton(parent);
	}
}

