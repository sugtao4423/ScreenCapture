import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;

public class StartCapture{

	private JFrame[] frame;
	private Component component;

	public StartCapture(Component component){
		this.component = component;

		JFrame.setDefaultLookAndFeelDecorated(false);
		GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		frame = new JFrame[gds.length];
		for(int i = 0; i < gds.length; i++){
			frame[i] = new JFrame();
			frame[i].setLocationRelativeTo(null);
			frame[i].setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame[i].getContentPane().add(new CapturePane());
			frame[i].setUndecorated(true);
			AWTUtilities.setWindowOpaque(frame[i], false);
			frame[i].setLocation(gds[i].getDefaultConfiguration().getBounds().x, gds[i].getDefaultConfiguration().getBounds().y);
			frame[i].setBounds(gds[i].getDefaultConfiguration().getBounds());
			frame[i].setVisible(true);
		}
	}

	public class CapturePane extends JPanel{

		private static final long serialVersionUID = 1634922976324586633L;
		private Rectangle selectionBounds;
		private Point clickPoint;
		private Point startPoint;
		private Point endPoint;

		public CapturePane(){
			setOpaque(false);

			MouseAdapter mouseHandler = new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
						System.exit(0);
					}
				}

				@Override
				public void mousePressed(MouseEvent e){
					startPoint = e.getLocationOnScreen();
					clickPoint = e.getPoint();
					selectionBounds = null;
				}

				@Override
				public void mouseReleased(MouseEvent e){
					endPoint = e.getLocationOnScreen();
					clickPoint = null;

					for(JFrame f : frame)
						f.dispose();

					Robot robot = null;
					try{
						robot = new Robot();
					}catch(AWTException e2){
						e2.printStackTrace();
					}
					BufferedImage bi = robot.createScreenCapture(new Rectangle(
							(int)startPoint.getX(), (int)startPoint.getY(),
							(int)(endPoint.getX() - startPoint.getX()), (int)(endPoint.getY() - startPoint.getY())));
					try{
						ImageIO.write(bi, "PNG", new File("C:\\Users\\tao\\Desktop\\tempppp.png"));
						//ImageIO.write(bi, "PNG", new File("/Users/tao/Desktop/tempppp.png"));
					}catch(IOException e2){
						JOptionPane.showMessageDialog(component, "だめみたいですね・・・");
						e2.printStackTrace();
					}
					JOptionPane.showMessageDialog(component, "OK!");
				}

				@Override
				public void mouseDragged(MouseEvent e){
					Point dragPoint = e.getPoint();
					int x = Math.min(clickPoint.x, dragPoint.x);
					int y = Math.min(clickPoint.y, dragPoint.y);
					int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
					int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);
					selectionBounds = new Rectangle(x, y, width, height);
					repaint();
				}
			};

			addMouseListener(mouseHandler);
			addMouseMotionListener(mouseHandler);
		}

		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g.create();
			g2d.setColor(new Color(255, 255, 255, 128));

			Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
			if(selectionBounds != null){
				fill.subtract(new Area(selectionBounds));
			}
			g2d.fill(fill);
			if(selectionBounds != null){
				g2d.setColor(Color.BLACK);
				g2d.draw(selectionBounds);
			}
			g2d.dispose();
		}
	}
}
