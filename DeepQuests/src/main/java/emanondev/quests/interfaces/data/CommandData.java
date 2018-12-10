package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import emanondev.quests.interfaces.Paths;

public class CommandData extends QuestComponentData {
	
	private String command = null;
	
	public CommandData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			command = (String) map.getOrDefault(Paths.DATA_COMMAND, null);
			if (command!=null && command.isEmpty())
				throw new IllegalArgumentException("Illegal command value");
		} catch (Exception e) {
			e.printStackTrace();
			command = null;
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (command !=null)
			map.put(Paths.DATA_COMMAND,command);
		return map;
	}
	
	public String getCommand() {
		return command;
	}
	
	public boolean setCommand(String value) {
		if (this.command==value)
			return false;
		if (value!=null && value.isEmpty())
			return false;
		if (this.command!=null && this.command.equals(value)) 
			return false;
		this.command = value;
		return true;
	}
	
	
}