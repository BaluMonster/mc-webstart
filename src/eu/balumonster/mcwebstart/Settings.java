package eu.balumonster.mcwebstart;

import java.io.File;
import java.util.List;
import java.util.Map;

import sk.tomsik68.mclauncher.api.common.ILaunchSettings;

public class Settings implements ILaunchSettings{

	@Override
	public String getInitHeap() {
		return "512M";
	}

	@Override
	public String getHeap() {
		return "1G";
	}

	@Override
	public Map<String, String> getCustomParameters() {
		return null;
	}

	@Override
	public boolean isErrorStreamRedirected() {
		return true;
	}

	@Override
	public List<String> getCommandPrefix() {
		return null;
	}

	@Override
	public boolean isModifyAppletOptions() {
		return false;
	}

	@Override
	public File getJavaLocation() {
		return null;
	}

	@Override
	public List<String> getJavaArguments() {
		return null;
	}

}
