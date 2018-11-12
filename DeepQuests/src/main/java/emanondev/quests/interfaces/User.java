package emanondev.quests.interfaces;

import java.io.IOException;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.storage.IConfig;

public abstract class User<T extends User<T>> {
	private UserManager<T> userManager;
	private UserData<T> data = null;
	private IConfig config = null;
	public User(UserManager<T> userManager) {
		this.userManager = userManager;
	}
	
	public QuestManager<T> getQuestManager(){
		return userManager.getQuestManager();
	}
	
	public UserData<T> getData(){
		if (data==null)
			loadData();
		return data;
	}
	public void loadData() {
		if (data== null) {
			IConfig conf = getQuestManager().getUserConfig(getUID());
			this.config = conf;
			UserData<T> data = conf.get(Paths.USERDATA_BASE);
			if (data==null)
				data = getDefaultUserData();
			this.data = data;
		}
	}
	public abstract UserData<T> getDefaultUserData();
	public UserManager<T> getUserManager(){
		return userManager;
	}
	public abstract String getUID();
	public void save() {
		config.set(Paths.USERDATA_BASE,data);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
			Quests.get().consoleLog("couldn't save user with id = '"+getUID()+"'");
		}
	}
}
