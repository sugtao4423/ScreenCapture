import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;

public class StartCapture{

	private JLabel imageField;
	private JFrame[] frame;
	private Component component;
	private String saveDir;
	private File image = null;

	public StartCapture(Component component, JLabel imageField, String saveDir){
		this.component = component;
		this.imageField = imageField;
		this.saveDir = saveDir;

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
							(int)(endPoint.getX() - startPoint.getX()),
							(int)(endPoint.getY() - startPoint.getY())));
					try{
						image = new File(saveDir + "/" + getFileName() + ".png");
						ImageIO.write(bi, "PNG", image);
						imageField.setIcon(new ImageIcon(getResizedBackgroundImage(image)));
					}catch(IOException e2){
						JOptionPane.showMessageDialog(component, "だめみたいですね・・・");
					}
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

	public BufferedImage getResizedBackgroundImage(File file){
		try{
			int maxWidth = 400;
			int maxHeight = 130;

			BufferedImage sourceImage = ImageIO.read(file);

			int sourceWidht = sourceImage.getWidth();
			int sourceHeight = sourceImage.getHeight();

			BigDecimal bdW = new BigDecimal(maxWidth);
			bdW = bdW.divide(new BigDecimal(sourceWidht), 8, BigDecimal.ROUND_HALF_UP);
			BigDecimal bdH = new BigDecimal(maxHeight);
			bdH = bdH.divide(new BigDecimal(sourceHeight), 8, BigDecimal.ROUND_HALF_UP);

			if(bdH.compareTo(bdW) < 0)
				maxWidth = -1;
			else
				maxHeight = -1;

			Image targetImage = sourceImage.getScaledInstance(maxWidth, maxHeight, Image.SCALE_DEFAULT);

			BufferedImage targetBufferedImage =
					new BufferedImage(targetImage.getWidth(null), targetImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = targetBufferedImage.createGraphics();
			g.drawImage(targetImage, 0, 0, null);

			if(sourceHeight < maxHeight){
				sourceWidht *= (float)maxHeight / sourceHeight;
				sourceHeight = maxHeight;
			}
			Rectangle2D rect = new Rectangle2D.Double(0, 0, sourceWidht, sourceHeight);
			g.setColor(new Color(255, 255, 255, 170));
			g.fill(rect);

			return targetBufferedImage;
		}catch(IOException e){
			System.exit(1);
			return null;
		}
	}

	public String getFileName(){
		return new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
	}

	public File getImage(){
		return image;
	}
}
