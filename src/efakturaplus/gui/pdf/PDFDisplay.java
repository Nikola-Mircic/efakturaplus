package efakturaplus.gui.pdf;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class PDFDisplay extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	// byte array containing the PDF file content
	private int pageIndex = 0;
	private float scale = 1.0F;
	private int x_offset = 0;
	private int y_offset = 0;

	private ArrayList<BufferedImage> pages;
	private CardLayout pagesLayout;
	private JPanel pagesPanel;

	private PDDocument pdfDocument;
	private PDFRenderer renderer;

	public PDFDisplay(PDDocument pdfDocument) throws IOException {
		this.pdfDocument = pdfDocument;
		this.renderer = new PDFRenderer(pdfDocument);

		pagesLayout = new CardLayout();
		pagesPanel = new JPanel(pagesLayout);
		this.add(pagesPanel);

		drawPages();
		updateLayout();
	}

	private void drawPages() throws IOException {
		pages = new ArrayList<>();
		for(int i = 0; i<pdfDocument.getNumberOfPages(); ++i){
			pages.add(renderer.renderImage(i, scale));
		}
	}

	private void updateLayout() throws IOException {
		pagesPanel.removeAll();

		for(int i = 0; i<pdfDocument.getNumberOfPages(); ++i){
			final int idx = i;
			JPanel imagePanel = new JPanel(){
				@Override
				public void paint(Graphics g) {
					g.drawImage(pages.get(idx), x_offset, y_offset,  null);
					g.dispose();
				}

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(pages.get(idx).getWidth(), pages.get(idx).getHeight());
				}
			};

			pagesPanel.add(imagePanel, String.valueOf(i));
		}

		pagesLayout.show(pagesPanel, String.valueOf(pageIndex));
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
					this.scale += 0.05;
                    try {
                        drawPages();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
				case KeyEvent.VK_SUBTRACT -> {
					this.scale -= 0.05;
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
				case KeyEvent.VK_UP -> {
					y_offset -= 20;
				}
				case KeyEvent.VK_DOWN -> {
					y_offset += 20;
				}
			}

			try {
				this.updateLayout();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}else{
			// Switching between pages
			switch (e.getKeyCode()){
				case KeyEvent.VK_RIGHT -> {
					this.pageIndex++;

					this.pageIndex = Math.min(this.pageIndex, this.pdfDocument.getNumberOfPages() - 1);

					pagesLayout.show(pagesPanel, String.valueOf(pageIndex));
					System.out.println(pageIndex);
				}
				case KeyEvent.VK_LEFT -> {
					this.pageIndex--;

					this.pageIndex = Math.max(this.pageIndex, 0);

					pagesLayout.show(pagesPanel, String.valueOf(pageIndex));
					System.out.println(pageIndex);
				}
			}
		}
    }

	@Override
	public void keyReleased(KeyEvent e) {

	}
}