import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class ScreenCapture extends JFrame implements ActionListener{

	private static final long serialVersionUID = -3324196305523625594L;
	private JPanel contentPane;
	private JButton captureBtn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
		}

		setTitle("Screen Capture");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		captureBtn = new JButton("Capture");
		captureBtn.setBounds(320, 240, 117, 29);
		contentPane.add(captureBtn);
		captureBtn.addActionListener(this);
	}

	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == captureBtn)
			new StartCapture(ScreenCapture.this);
	}
}
