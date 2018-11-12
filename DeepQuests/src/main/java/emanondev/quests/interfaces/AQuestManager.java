package emanondev.quests.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.storage.IConfig;

public abstract class AQuestManager<T extends User<T>> implements QuestManager<T> {
	
	
	public AQuestManager(String name) {
		if (name==null || name.isEmpty())
			throw new IllegalArgumentException();
		if (!Paths.ALPHANUMERIC.matcher(name).matches())
			throw new IllegalArgumentException();
		this.name = name;
		try {
			this.folder = new File(Quests.get().getDataFolder(),name);
			if (!folder.exists())
				folder.mkdirs();
			managerConfigFile = new File(folder,"config.yml");
			if (!managerConfigFile.exists())
				managerConfigFile.createNewFile();
			usersFolder = new File(folder,"user-database");
			if (!usersFolder.exists())
				usersFolder.mkdirs();
			questContainerFile = new File(folder,"quests-database.yml");
			if (!questContainerFile.exists())
				questContainerFile.createNewFile();
			managerConfig = IConfig.loadYamlConfiguration(managerConfigFile);
			questContainerConfig = IConfig.loadYamlConfiguration(questContainerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private File folder;
	private File managerConfigFile;
	private File questContainerFile;
	private File usersFolder;
	private IConfig managerConfig;
	private IConfig questContainerConfig;
	

	private final String name;

	@Override
	public IConfig getUserConfig(String uid) {
		return IConfig.loadYamlConfiguration(getUserFile(uid));
	}
	
	private File getUserFile(String uid) {
		File file = new File(usersFolder,uid+".yml");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return file;
	}
	@Override
	public File getFolder() {
		return folder;
	}
	@Override
	public File getUsersFolder() {
		return usersFolder;
	}
	@Override
	public IConfig getConfig() {
		return managerConfig;
	}
	@Override
	public IConfig getQuestContainerConfig() {
		return questContainerConfig;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String generateQuestKey(QuestContainer<T> qc) {
		int index = questContainerConfig.getInteger(Paths.QUESTCONTAINER_QUEST_COUNTER,0);
		String key;
		do {
			index++;
			key = "QUEST-"+index;
		} while (qc.getQuest(key)!=null);
		questContainerConfig.set(Paths.QUESTCONTAINER_QUEST_COUNTER,index);
		return key;
	}

	@Override
	public String generateMissionKey(Quest<T> qc) {
		int index = questContainerConfig.getInteger(Paths.QUESTCONTAINER_MISSION_COUNTER,0);
		String key;
		do {
			index++;
			key = "MISSION-"+index;
		} while (qc.getMission(key)!=null);
		questContainerConfig.set(Paths.QUESTCONTAINER_MISSION_COUNTER,index);
		return key;
	}

	@Override
	public String generateTaskKey(Mission<T> qc) {
		int index = questContainerConfig.getInteger(Paths.QUESTCONTAINER_TASK_COUNTER,0);
		String key;
		do {
			index++;
			key = "TASK-"+index;
		} while (qc.getTask(key)!=null);
		questContainerConfig.set(Paths.QUESTCONTAINER_TASK_COUNTER,index);
		return key;
	}

	@Override
	public String generateRewardKey(Set<String> keys) {
		int index = questContainerConfig.getInteger(Paths.QUESTCONTAINER_REWARD_COUNTER,0);
		String key;
		do {
			index++;
			key = "REWARD-"+index;
		} while (keys.contains(key));
		questContainerConfig.set(Paths.QUESTCONTAINER_REWARD_COUNTER,index);
		return key;
	}

	@Override
	public String generateRequireKey(Set<String> keys) {
		int index = questContainerConfig.getInteger(Paths.QUESTCONTAINER_REQUIRE_COUNTER,0);
		String key;
		do {
			index++;
			key = "REQUIRE-"+index;
		} while (keys.contains(key));
		questContainerConfig.set(Paths.QUESTCONTAINER_REQUIRE_COUNTER,index);
		return key;
	}

	@Override
	public void saveQuestContainer() {
		try {
			questContainerConfig.set(Paths.QUESTCONTAINER_BASE,getQuestContainer());
			questContainerConfig.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void save() {
		try {
			managerConfig.save();
			saveQuestContainer();
			getUserManager().saveAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
