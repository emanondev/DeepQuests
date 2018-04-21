package emanondev.quests.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.events.PlayerClickGuiMissionEvent;
import emanondev.quests.events.PlayerClickGuiQuestEvent;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.StringUtils;

public class GuiManager implements Listener {
	public GuiManager() {
		Quests.getInstance().registerListener(this);
	}
	public void openMissionsInventory(Player p,Quest q,int page) {
		openMissionsInventory(p,q,page,null,p);
	}
	public void openMissionsInventory(Player p,Quest q,int page,Player whoOpens) {
		openMissionsInventory(p,q,page,null,whoOpens);
	}
	public void openMissionsInventory(Player p,Quest q,int page,GuiHolder previusHolder) {
		openMissionsInventory(p,q,page,previusHolder,p);
	}
	public void openMissionsInventory(Player p,Quest q,int page,GuiHolder previusHolder,Player whoOpens) {
		whoOpens.openInventory(getMissionsInventory(p,q,page,previusHolder));
	}
	public Inventory getMissionsInventory(Player p,Quest q,int page,GuiHolder previusHolder) {
		if (previusHolder==null)
			return getMissionsInventory(p,q,page,false,null);
		return getMissionsInventory(p,q,page,previusHolder.bypassHidden,previusHolder);
	}
	public Inventory getMissionsInventory(Player p,Quest q,int page) {
		return getMissionsInventory(p,q,page,false);
	}
	public Inventory getMissionsInventory(Player p,Quest q,int page,boolean bypassHide) {
		 return getMissionsInventory(p,q,page,bypassHide,null);
	}
	private Inventory getMissionsInventory(Player p,Quest q,int page,boolean bypassHide,GuiHolder previusHolder) {
		QuestPlayer qp = Quests.getInstance().getPlayerManager().getQuestPlayer(p);
		
		ArrayList<BindMission> bindsList = new ArrayList<BindMission>();
		for (Mission mission : q.getMissions()) {
			ItemStack missionItem = qp.getGuiItem(mission,bypassHide);
			if (missionItem!=null) {
				bindsList.add(new BindMission(missionItem,mission));
			}
		}
		
		GuiHolder holder = new MissionsGuiHolder(p,page,bindsList,bypassHide,previusHolder,q);
		return holder.getInventory();
	}
	public Inventory getQuestsInventory(Player p,int page) {
		return getQuestsInventory(p,page,false,null);
	}
	public Inventory getQuestsInventory(Player p,int page,boolean bypassHidden) {
		return getQuestsInventory(p,page,bypassHidden,null);
	}
	public Inventory getQuestsInventory(Player p,int page,GuiHolder previusHolder) {
		if (previusHolder==null)
			return getQuestsInventory(p,page,false,null);
		return getQuestsInventory(p,page,previusHolder.bypassHidden,previusHolder);
	}
	private Inventory getQuestsInventory(Player p,int page,boolean bypassHidden,GuiHolder previusHolder) {
		QuestPlayer qp = Quests.getInstance().getPlayerManager().getQuestPlayer(p);
		
		ArrayList<BindQuest> bindsList = new ArrayList<BindQuest>();
		for (Quest quest : Quests.getInstance().getQuestManager().getQuests()) {
			ItemStack missionItem = qp.getGuiItem(quest,bypassHidden);
			if (missionItem!=null) {
				bindsList.add(new BindQuest(missionItem,quest));
			}
		}
		
		GuiHolder holder = new QuestsGuiHolder(p,page,bindsList,bypassHidden,previusHolder);
		return holder.getInventory();
	}
	
	public abstract class GuiHolder implements InventoryHolder {
		private final Player player;
		private int page;
		private boolean bypassHidden;
		protected final GuiHolder previusHolder;
		protected GuiHolder(Player p,int page,boolean bypassHidden,GuiHolder previusHolder) {
			this.player = p;
			this.page = page;
			this.bypassHidden = bypassHidden;
			this.previusHolder = previusHolder;
		}
		public Player getPlayer() {
			return player;
		}
		public int getPage() {
			return this.page;
		}
		public boolean isHiddenBypassed() {
			return bypassHidden;
		}
		protected void setPage(int page) {
			this.page = page;
		}
		protected abstract void incrementPage();
		protected abstract void decrementPage();
		protected abstract void update();
		protected void onSlotClick(Player clicker,int slot,ClickType click) {
			switch (slot) {
			case 45:
				if (previusHolder!=null)
					clicker.openInventory(previusHolder.getInventory());
				return;
			case 53:
				clicker.closeInventory();
				return;
			case 48:
				decrementPage();
				return;
			case 50:
				incrementPage();
				return;
			}
		}
		
	}

	private static int legacyPage(int page,List<?> list) {
		if (page>((list.size()-1)/45)+1)
			page = ((list.size()-1)/45)+1;
		if (page<1)
			page = 1;
		return page;
	}
	public class MissionsGuiHolder extends GuiHolder {
		private final ArrayList<BindMission> bindList;
		private final Inventory inv;
		private final Quest quest;
		public MissionsGuiHolder(Player p,int page,ArrayList<BindMission> bindList,boolean bypassHidden,GuiHolder previusHolder,Quest q) {
			super(p,legacyPage(page,bindList),bypassHidden,previusHolder);
			this.bindList = bindList;
			this.quest = q;
			this.inv = Bukkit.createInventory(this, 54, Quests.getInstance()
					.getConfigManager().getMissionsMenuTitle(p, q));
			for (int i = 45*(getPage()-1); i< 45*getPage() && i < bindList.size(); i++)
				inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
			if (this.previusHolder!=null)
				inv.setItem(45, Quests.getInstance()
						.getConfigManager().getMissionsMenuBackItem(p));
			inv.setItem(53, Quests.getInstance()
					.getConfigManager().getMissionsMenuCloseItem(p));
			inv.setItem(48, Quests.getInstance()
					.getConfigManager().getMissionsMenuPreviusPageItem(p));
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getMissionsMenuPageItem(p,getPage(),q));
			inv.setItem(50, Quests.getInstance()
					.getConfigManager().getMissionsMenuNextPageItem(p));
		}
		@Override
		protected void incrementPage() {
			if (legacyPage(getPage()+1,bindList)==getPage())
				return;
			for (int i = 45*(getPage()-1); i< 45*getPage(); i++) {
				if (i < bindList.size())
					inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
				else
					inv.setItem(i-45*(getPage()-1),null);
			}
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getMissionsMenuPageItem(getPlayer(),getPage(),this.quest));
		}
		@Override
		protected void decrementPage() {
			if (legacyPage(getPage()-1,bindList)==getPage())
				return;
			for (int i = 45*(getPage()-1); i< 45*getPage(); i++) {
				if (i < bindList.size())
					inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
				else
					inv.setItem(i-45*(getPage()-1),null);
			}
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getMissionsMenuPageItem(getPlayer(),getPage(),this.quest));
		}
		@Override
		protected void onSlotClick(Player clicker,int slot,ClickType click) {
			super.onSlotClick(clicker,slot,click);
			if (slot>=0 && slot<45 && slot+(getPage()-1)*45 < bindList.size()) {
				Mission m = bindList.get(slot+(getPage()-1)*45).getMission();
				PlayerClickGuiMissionEvent event = new PlayerClickGuiMissionEvent
						(clicker,click,this,m);
				QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
						.getQuestPlayer(getPlayer());
				DisplayState state = qPlayer.getDisplayState(m);
				switch(state) {
				case LOCKED:
					event.setCancelled(true);
				}
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				if(clicker.equals(getPlayer())) {
					switch (click) {
					case LEFT:
						switch (state) {
						case UNSTARTED:
							qPlayer.startMission(m);
							update();
							return;
						case ONPROGRESS:
							qPlayer.togglePauseMission(m);
							update();
						}
						return;
					case RIGHT:
						//TODO tasks gui
						return;
					}
				}
			}
		}
		@Override
		protected void update() {
			QuestPlayer qp = Quests.getInstance().getPlayerManager().getQuestPlayer(getPlayer());
			bindList.clear();
			for (Mission mission : quest.getMissions()) {
				ItemStack missionItem = qp.getGuiItem(mission,isHiddenBypassed());
				if (missionItem!=null) {
					bindList.add(new BindMission(missionItem,mission));
				}
			}
			setPage(legacyPage(getPage(),bindList));
			for (int i = 45*(getPage()-1); i< 45*getPage(); i++) {
				if (i < bindList.size())
					inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
				else
					inv.setItem(i-45*(getPage()-1),null);
			}
			if (this.previusHolder!=null)
				inv.setItem(45, Quests.getInstance()
						.getConfigManager().getMissionsMenuBackItem(getPlayer()));
			inv.setItem(53, Quests.getInstance()
					.getConfigManager().getMissionsMenuCloseItem(getPlayer()));
			inv.setItem(48, Quests.getInstance()
					.getConfigManager().getMissionsMenuPreviusPageItem(getPlayer()));
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getMissionsMenuPageItem(getPlayer(),getPage(),this.quest));
			inv.setItem(50, Quests.getInstance()
					.getConfigManager().getMissionsMenuNextPageItem(getPlayer()));
		}
		@Override
		public Inventory getInventory() {
			return inv;
		}
	}
	
	public class QuestsGuiHolder extends GuiHolder {
		private final ArrayList<BindQuest> bindList;
		private final Inventory inv;
		public QuestsGuiHolder(Player p,int page,ArrayList<BindQuest> bindList,boolean bypassHidden,GuiHolder previusHolder) {
			super(p,legacyPage(page,bindList),bypassHidden,previusHolder);
			this.bindList = bindList;
			this.inv = Bukkit.createInventory(this, 54, StringUtils
					.convertText(p, Quests.getInstance().getConfigManager().getQuestsMenuTitle(p)));
			for (int i = 45*(getPage()-1); i< 45*getPage() && i < bindList.size(); i++)
				inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
			if (this.previusHolder!=null)
				inv.setItem(45, Quests.getInstance()
						.getConfigManager().getQuestsMenuBackItem(getPlayer()));
			inv.setItem(53, Quests.getInstance()
					.getConfigManager().getQuestsMenuCloseItem(getPlayer()));
			inv.setItem(48, Quests.getInstance()
					.getConfigManager().getQuestsMenuPreviusPageItem(getPlayer()));
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getQuestsMenuPageItem(getPlayer(),getPage()));
			inv.setItem(50, Quests.getInstance()
					.getConfigManager().getQuestsMenuNextPageItem(getPlayer()));
		}
		@Override
		protected void incrementPage() {
			if (legacyPage(getPage()+1,bindList)==getPage())
				return;
			for (int i = 45*(getPage()-1); i< 45*getPage(); i++) {
				if (i < bindList.size())
					inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
				else
					inv.setItem(i-45*(getPage()-1),null);
			}
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getQuestsMenuPageItem(getPlayer(),getPage()));
		}
		@Override
		protected void decrementPage() {
			if (legacyPage(getPage()-1,bindList)==getPage())
				return;
			for (int i = 45*(getPage()-1); i< 45*getPage(); i++) {
				if (i < bindList.size())
					inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
				else
					inv.setItem(i-45*(getPage()-1),null);
			}
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getQuestsMenuPageItem(getPlayer(),getPage()));
		}
		@Override
		protected void onSlotClick(Player clicker,int slot,ClickType click) {
			super.onSlotClick(clicker,slot,click);
			if (slot>=0 && slot<45 && slot+(getPage()-1)*45 < bindList.size()) {
				Quest q = bindList.get(slot+(getPage()-1)*45).getQuest();
				PlayerClickGuiQuestEvent event = new PlayerClickGuiQuestEvent(clicker,click,
						this,q);
				if (click!=ClickType.LEFT)
					event.setCancelled(true);
				switch (Quests.getInstance().getPlayerManager()
						.getQuestPlayer(getPlayer()).getDisplayState(q)) {
				case LOCKED:
					event.setCancelled(true);
				default:
				}
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				if (click==ClickType.LEFT)
					openMissionsInventory(clicker,q,1,this);
			}	
		}
		@Override
		protected void update() {
			QuestPlayer qp = Quests.getInstance().getPlayerManager().getQuestPlayer(getPlayer());
			bindList.clear();
			for (Quest quest : Quests.getInstance().getQuestManager().getQuests()) {
				ItemStack questItem = qp.getGuiItem(quest,isHiddenBypassed());
				if (questItem!=null) {
					bindList.add(new BindQuest(questItem,quest));
				}
			}
			setPage(legacyPage(getPage(),bindList));
			for (int i = 45*(getPage()-1); i< 45*getPage(); i++) {
				if (i < bindList.size())
					inv.setItem(i-45*(getPage()-1),bindList.get(i).getItem());
				else
					inv.setItem(i-45*(getPage()-1),null);
			}
			if (this.previusHolder!=null)
				inv.setItem(45, Quests.getInstance()
						.getConfigManager().getQuestsMenuBackItem(getPlayer()));
			inv.setItem(53, Quests.getInstance()
					.getConfigManager().getQuestsMenuCloseItem(getPlayer()));
			inv.setItem(48, Quests.getInstance()
					.getConfigManager().getQuestsMenuPreviusPageItem(getPlayer()));
			inv.setItem(49, Quests.getInstance()
					.getConfigManager().getQuestsMenuPageItem(getPlayer(),getPage()));
			inv.setItem(50, Quests.getInstance()
					.getConfigManager().getQuestsMenuNextPageItem(getPlayer()));
		}
		@Override
		public Inventory getInventory() {
			return inv;
		}
		
		
	}
	
	
	
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private void onClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof GuiHolder)
			event.setCancelled(true);
		if (event.getClickedInventory()!=null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
			GuiHolder holder = (GuiHolder) event.getView().getTopInventory().getHolder();
			if (event.getWhoClicked() instanceof Player)
				holder.onSlotClick((Player) event.getWhoClicked(),
						event.getRawSlot(), event.getClick());
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private void onDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof GuiHolder)
			event.setCancelled(true);
	}
	
}
