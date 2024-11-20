package efakturaplus.gui.pdf;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;

import javax.swing.*;

public class PDFDisplay extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
	@Serial
	private static final long serialVersionUID = 1L;
	// byte array containing the PDF file content
	private int pageIndex = 0;
	private float scale = 1.0F;
	private int x_offset = 0;
	private int y_offset = 0;

	private int mouse_x_origin = -1;
	private int mouse_y_origin = -1;

	private ArrayList<BufferedImage> pages;
	private JPanel pagesPanel;

	private final PDDocument pdfDocument;
	private final PDFRenderer renderer;

	public PDFDisplay(PDDocument pdfDocument) throws IOException {
		this.pdfDocument = pdfDocument;
		this.renderer = new PDFRenderer(pdfDocument);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		drawPages();
		updateLayout();
	}

	private void drawPages() throws IOException {
		pages = new ArrayList<>();
		for(int i = 0; i<pdfDocument.getNumberOfPages(); ++i){
			pages.add(renderer.renderImage(i, scale));
		}
	}

	private void updateLayout(){
		this.removeAll();

		this.pagesPanel = new JPanel(){
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				int pageHeight = pages.getFirst().getHeight() + 5;

				System.out.println("Page height: " + pageHeight);
				System.out.println("[x_offest, y_offset]: " + x_offset + "," + y_offset);

				for(int i = 0; i<pages.size(); ++i) {
					g.drawImage(pages.get(i), x_offset, (i - pageIndex) * pageHeight + y_offset, null);
				}

				g.dispose();
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(pages.getFirst().getWidth(), pages.getFirst().getHeight());
			}
		};

		this.add(pagesPanel);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isControlDown()){
			// Zooming and moving displayed PDF file
			switch (e.getKeyCode()){
				case KeyEvent.VK_ADD -> {
					this.scale += 0.05F;
                    try {
                        drawPages();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
				case KeyEvent.VK_SUBTRACT -> {
					this.scale -= 0.05F;
					try {
						drawPages();
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
				case KeyEvent.VK_LEFT -> {
					x_offset -= 20;
				}
				case KeyEvent.VK_RIGHT -> {
					x_offset += 20;
				}
			}
		}else{
			// Switching between pages
			switch (e.getKeyCode()){
				case KeyEvent.VK_RIGHT -> {
					this.pageIndex++;

					this.pageIndex = Math.min(this.pageIndex, this.pdfDocument.getNumberOfPages() - 1);
					y_offset = 0;

					System.out.println(pageIndex);
				}
				case KeyEvent.VK_LEFT -> {
					this.pageIndex--;

					this.pageIndex = Math.max(this.pageIndex, 0);
					y_offset = 0;

					System.out.println(pageIndex);
				}
				case KeyEvent.VK_UP -> {
					y_offset += 20;
				}
				case KeyEvent.VK_DOWN -> {
					y_offset -= 20;
				}
			}
		}

		this.pagesPanel.repaint();
		this.repaint();
    }

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x_offset += e.getX() - mouse_x_origin;
		y_offset += e.getY() - mouse_y_origin;
		mouse_x_origin = e.getX();
		mouse_y_origin = e.getY();

		this.pagesPanel.repaint();
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse_x_origin = e.getX();
		mouse_y_origin = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouse_x_origin = -1;
		mouse_y_origin = -1;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}