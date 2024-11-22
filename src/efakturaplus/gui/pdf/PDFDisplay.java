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

public class PDFDisplay extends JPanel{
	@Serial
	private static final long serialVersionUID = 1L;

	private ArrayList<BufferedImage> pages;
	private JPanel pagesPanel;

	private final PDDocument pdfDocument;
	private final PDFRenderer renderer;

	public PDFNavigator navigator;

	public PDFDisplay(PDDocument pdfDocument) throws IOException {
		this.pdfDocument = pdfDocument;
		this.renderer = new PDFRenderer(pdfDocument);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.navigator = new PDFNavigator(this);

		this.addMouseListener(navigator);
		this.addMouseMotionListener(navigator);

		drawPages();
		updateLayout();
	}

	public void drawPages() throws IOException {
		pages = new ArrayList<>();
		for(int i = 0; i<pdfDocument.getNumberOfPages(); ++i){
			pages.add(renderer.renderImage(i, navigator.scale));
		}
	}

	public void updateLayout(){
		this.removeAll();

		this.pagesPanel = new JPanel(){
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				int pageHeight = pages.getFirst().getHeight() + 5;

				System.out.println("Page height: " + pageHeight);
				System.out.println("[x_offest, y_offset]: " + navigator.x_offset + "," + navigator.y_offset);

				for(int i = 0; i<pages.size(); ++i) {
					g.drawImage(pages.get(i),
								navigator.x_offset,
								(i - navigator.pageIndex) * pageHeight + navigator.y_offset,
								null);
				}

				g.dispose();
			}

			/*@Override
			public Dimension getPreferredSize() {
				return new Dimension(pages.getFirst().getWidth(), pages.getFirst().getHeight());
			}*/
		};

		this.add(pagesPanel);
	}

	public ArrayList<BufferedImage> getPages(){
		return pages;
	}

	public JPanel getPagesPanel(){
		return pagesPanel;
	}
}