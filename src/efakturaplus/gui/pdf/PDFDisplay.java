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

	public ArrayList<BufferedImage> pages;
	public JPanel pagesPanel;

	public int pageIndex = 0;
	public float scale = 1.0F;
	public int x_offset = 0;
	public int y_offset = 0;

	private final PDDocument pdfDocument;
	private final PDFRenderer renderer;

	private PDFNavigator navigator;
    public PDFInputListener inputListener;

	public PDFDisplay(PDDocument pdfDocument) throws IOException {
		this.pdfDocument = pdfDocument;
		this.renderer = new PDFRenderer(pdfDocument);

		this.setLayout(new BorderLayout());

		drawPages();
		updateLayout();

		this.inputListener = new PDFInputListener(navigator);

		this.addMouseListener(inputListener);
		this.addMouseMotionListener(inputListener);
	}

	public void drawPages() throws IOException {
		pages = new ArrayList<>();
		for(int i = 0; i<pdfDocument.getNumberOfPages(); ++i){
			pages.add(renderer.renderImage(i, scale));
		}
	}

	public void updateLayout(){
		this.removeAll();

        this.navigator = new PDFNavigator(this);
		this.add(navigator, BorderLayout.NORTH);

		this.pagesPanel = new JPanel(){
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				int pageWidth = pages.getFirst().getWidth();
				int pageHeight = pages.getFirst().getHeight() + 5;

				for(int i = 0; i<pages.size(); ++i) {
					g.drawImage(pages.get(i),
								x_offset + (getWidth() - pageWidth)/2,
								(i - pageIndex) * pageHeight + y_offset,
								null);
				}

				g.dispose();
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(pages.getFirst().getWidth(), pages.getFirst().getHeight());
			}
		};

		this.add(pagesPanel, BorderLayout.CENTER);
	}

	public ArrayList<BufferedImage> getPages(){
		return pages;
	}

	public JPanel getPagesPanel(){
		return pagesPanel;
	}

	public PDFInputListener getInputListener() {
		return inputListener;
	}

	public int getX_offset() {
		return x_offset;
	}

	public int getY_offset() {
		return y_offset;
	}
}