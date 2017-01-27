import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class ScreenCapture extends JFrame implements ActionListener{

	private static final long serialVersionUID = -3324196305523625594L;
	private JButton tweet;
	private JButton captureBtn;
	private JTextArea textArea;
	private JLabel imageField;
	private JButton settings;
	private String saveDir;
	private Twitter twitter;
	private StartCapture startCapture;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		Config config = new Config();
		config.loadTwitter();
		config.loadConfig();
		String[] twitterAccess = config.getTwitter();
		if(twitterAccess[2].equals("") || twitterAccess[3].equals("")){
			new OAuth().setVisible(true);
			return;
		}
		if(config.getSaveDir().equals("")){
			new Settings().setVisible(true);
			return;
		}
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					ScreenCapture frame = new ScreenCapture();
					frame.setVisible(true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ScreenCapture(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1){
		}
		Config config = new Config();
		config.loadTwitter();
		config.loadConfig();
		saveDir = config.getSaveDir();
		String[] twitterAccess = config.getTwitter();
		Configuration jconf = new ConfigurationBuilder()
				.setOAuthConsumerKey(twitterAccess[0]).setOAuthConsumerSecret(twitterAccess[1]).build();
		AccessToken token = new AccessToken(twitterAccess[2], twitterAccess[3]);
		twitter = new TwitterFactory(jconf).getInstance(token);

		setTitle("Screen Capture");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(120, 120, 420, 215);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		tweet = new JButton("Tweet(Ctrl+Enter)");
		tweet.setBounds(257, 143, 143, 30);
		tweet.addActionListener(this);
		contentPane.add(tweet);

		captureBtn = new JButton("Capture");
		captureBtn.setBounds(112, 143, 110, 30);
		captureBtn.addActionListener(this);
		contentPane.add(captureBtn);

		textArea = new JTextArea();
		textArea.setBounds(0, 0, 400, 130);
		textArea.setFont(new Font("メイリオ", Font.BOLD, 20));
		textArea.setLineWrap(true);
		textArea.setOpaque(false);
		textArea.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				keyPress(e);
			}
		});
		contentPane.add(textArea);

		imageField = new JLabel("");
		imageField.setBounds(0, 0, 400, 130);
		contentPane.add(imageField);

		settings = new JButton("Settings");
		settings.setBounds(0, 143, 80, 30);
		settings.addActionListener(this);
		contentPane.add(settings);
	}

	public void actionPerformed(ActionEvent event){
		if(event.getSource() == tweet)
			tweet();
		else if(event.getSource() == settings)
			new Settings().setVisible(true);
		else if(event.getSource() == captureBtn)
			startCapture = new StartCapture(ScreenCapture.this, imageField, saveDir);
	}

	public void keyPress(KeyEvent e){
		// VK_ENTER: EnterKey, CTRL_DOWN_MASK: CtrlKey, META_DOWN_MASK: OS X's CommandKey
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			if((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0 || (e.getModifiersEx() & InputEvent.META_DOWN_MASK) > 0)
				tweet();
		}
	}

	public void tweet(){
		new Thread(new Runnable(){

			@Override
			public void run(){
				StatusUpdate status = new StatusUpdate(textArea.getText());
				if(startCapture != null){
					File image = startCapture.getImage();
					if(image != null)
						status.media(image);
				}
				try{
					twitter.updateStatus(status);
				}catch(TwitterException e){
					JOptionPane.showMessageDialog(ScreenCapture.this, "だめみたいですね・・・");
				}
				startCapture = null;
				imageField.setIcon(null);
				textArea.setText("");
			}
		}).start();
	}
}
