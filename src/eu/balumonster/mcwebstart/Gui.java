package eu.balumonster.mcwebstart;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import lombok.Getter;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDAuthProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	@Getter private Dimension screenDimension;
	@Getter private JTextField textField;
	@Getter private JPasswordField passwordField;
	@Getter private JButton btnPlay;
	@Getter private Bar progressBar;
	private Main instance;

	public Gui(Main instance) {
		this.instance=instance;
		screenDimension=Toolkit.getDefaultToolkit().getScreenSize();
		initialize();
	}
	
	private void initialize(){
		setupNimbus();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds((int)screenDimension.getWidth()/2-100, (int)screenDimension.getHeight()/2-65, 200, 157);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		textField = new JTextField();
		textField.setBounds(10, 11, 174, 28);
		contentPane.add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(10, 44, 174, 28);
		contentPane.add(passwordField);
		
		btnPlay = new JButton("Play");
		btnPlay.setBounds(10, 77, 174, 23);
		contentPane.add(btnPlay);
		
		progressBar=new Bar();
		progressBar.setBounds(10, 104, 174, 19);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonAction();
			}
		});
	}
	
	public void setLocked(boolean bool){
		textField.setEnabled(!bool);
		passwordField.setEnabled(!bool);
		btnPlay.setEnabled(!bool);
	}
	
	private void setupNimbus(){
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		}
	}
	
	private void buttonAction(){
		try{
			System.out.println("Logging in");
			setLocked(true);
			String username=textField.getText();
			String password=new String(passwordField.getPassword());
			YDLoginService yls = new YDLoginService();
			IProfile profile=new LegacyProfile(username, password);
			ISession session=yls.login(profile);
			if (profile instanceof YDAuthProfile) {
	            try {
	                IProfile[] profiles = instance.getProfileIO().read();
	                ((YDAuthProfile) profiles[0]).setPassword(session.getSessionID());
	                instance.getProfileIO().write(profiles);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
			instance.setSession(session);
			instance.startMc();
		}catch(Exception ex){
			setLocked(false);
			progressBar.setString("Bad Login!");
		}
	}
	
}

class Bar extends JProgressBar implements IProgressMonitor{

	private static final long serialVersionUID = 1L;

	@Override
	public void setProgress(int progress) {
		super.setValue(progress);
	}

	@Override
	public void setMax(int len) {
		super.setMaximum(len);
	}

	@Override
	public void incrementProgress(int amount) {
		setProgress(super.getValue() + amount);
	}

	@Override
	public void finish() {
	}
	
}
