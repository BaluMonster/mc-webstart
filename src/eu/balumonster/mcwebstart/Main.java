package eu.balumonster.mcwebstart;

import java.awt.EventQueue;
import java.io.File;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import lombok.Getter;
import lombok.Setter;

import sk.tomsik68.mclauncher.api.common.MCLauncherAPI;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.api.versions.IVersion;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.common.mc.MinecraftInstance;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDAuthProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.io.YDProfileIO;
import sk.tomsik68.mclauncher.impl.versions.mcdownload.MCDownloadVersion;
import sk.tomsik68.mclauncher.util.HttpUtils;

public class Main {

	public static void main(final String[] a){
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main(a[0]);
			}
		});
	}
	
	@Getter private static String serverIp;
	@Getter private static String serverPort;
	@Getter private IVersion selectedVersion;
	
	@Getter private Gui mainFrame;
	
	@Getter private File minecraftDefaultDir;
	@Getter private YDProfileIO profileIO;
	
	@Getter private File workingDir;
	@Getter private MinecraftInstance minecraftInstance;
	
	@Getter @Setter ISession session;
	
	public Main(String dataString){
		dataString=dataString.replaceFirst("minecraft://", "");
		serverIp=dataString.split(":")[0];
		serverPort=dataString.split(":")[1];
		String versionId=dataString.split(":")[2].replaceAll("/", "");
		JSONObject versionJson = null;
		try {
			versionJson = (JSONObject) JSONValue.parse(HttpUtils.httpGet(MCLauncherAPI.URLS.NEW_VERSION_URL.replaceAll("<VERSION>", versionId)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		selectedVersion=new MCDownloadVersion(versionJson);
		
		minecraftDefaultDir=Platform.getCurrentPlatform().getWorkingDirectory();
		profileIO=new YDProfileIO(minecraftDefaultDir);
		
		workingDir=new File(System.getProperty("user.home")+"/mcwebstart");
		minecraftInstance=new MinecraftInstance(workingDir);
		mainFrame=new Gui(this);
		mainFrame.setVisible(true);
		if((session=trySessionLogin())!=null){
			startMc();
		}
	}
	
	private ISession trySessionLogin(){
		try{
			ISession result = null;
	        IProfile profile = null;
	        YDLoginService yls = new YDLoginService();
			yls.load(minecraftDefaultDir);
	        IProfile[] profiles = loadProfiles();
	        profile = profiles[0];
	        result=yls.login(profile);
	        if (profile instanceof YDAuthProfile) {
	            try {
	                IProfile[] ps = profileIO.read();
	                ((YDAuthProfile) ps[0]).setPassword(result.getSessionID());
	                profileIO.write(ps);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return result;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	private IProfile[] loadProfiles() {
        try {
            return profileIO.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public void startMc(){
		Runnable updater=new Runnable() {
			@Override
			public void run() {
				try {
					mainFrame.setLocked(true);
					mainFrame.getProgressBar().setString("Downloading Resources...");
					selectedVersion.getInstaller().install(selectedVersion, minecraftInstance, mainFrame.getProgressBar());
					mainFrame.getProgressBar().setString("Starting!");
					selectedVersion.getLauncher().launch(session, minecraftInstance, new Server(), selectedVersion, new Settings());
					mainFrame.setVisible(false);
					mainFrame.dispose();
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		new Thread(updater).start();
	}
	
}
