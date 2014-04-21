package eu.balumonster.mcwebstart;

import sk.tomsik68.mclauncher.api.servers.ISavedServer;

public class Server implements ISavedServer{
	
	@Override
	public String getIP() {
		return Main.getServerIp()+":"+Main.getServerPort();
	}

	@Override
	public String getName() {
		return "Selected Server";
	}

}
