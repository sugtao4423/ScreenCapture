import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class Settings extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;
	private JButton find;
	private JButton save;

	private Config config;

	/**
	 * Create the frame.
	 */
	public Settings(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1){
		}
		config = new Config();
		config.loadConfig();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("設定");
		setBounds(120, 120, 352, 169);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lb = new JLabel("保存するフォルダー");
		lb.setBounds(6, 25, 130, 16);
		contentPane.add(lb);

		textField = new JTextField();
		textField.setBounds(6, 45, 250, 28);
		textField.setText(config.getSaveDir());
		contentPane.add(textField);

		find = new JButton("参照");
		find.setBounds(260, 46, 70, 29);
		find.addActionListener(this);
		contentPane.add(find);

		save = new JButton("保存");
		save.setBounds(213, 85, 117, 29);
		save.addActionListener(this);
		contentPane.add(save);
	}

	public void actionPerformed(ActionEvent event){
		if(event.getSource() == find)
			selectDirectory();
		else if(event.getSource() == save)
			save();
	}

	public void selectDirectory(){
		JFileChooser dirChoose = new JFileChooser();
		dirChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(dirChoose.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION)
			textField.setText(dirChoose.getSelectedFile().getAbsolutePath());
	}

	public void save(){
		config.addProperties("saveDir", textField.getText());
		ScreenCapture.main(new String[]{});
		dispose();
	}
}
