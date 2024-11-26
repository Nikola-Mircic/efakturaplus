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

	private int pageIndex = 0;
	private float scale = 1.0F;
	private int x_offset = 0;
	private int y_offset = 0;

	private final PDDocument pdfDocument;
	private final PDFRenderer renderer;

	private PDFNavigator navigator;
    public PDFInputListener inputListener;

	public PDFDisplay(PDDocument pdfDocument) throws IOException {
		this.pdfDocument = pdfDocument;
		this.renderer = new PDFRenderer(pdfDocument);

		this.setLayout(new BorderLayout(3,3));

		this.inputListener = new PDFInputListener(this);

		this.addMouseListener(inputListener);
		this.addMouseMotionListener(inputListener);

		drawPages();
		updateLayout();
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

				int pageHeight = pages.getFirst().getHeight() + 5;

				for(int i = 0; i<pages.size(); ++i) {
					g.drawImage(pages.get(i),
								x_offset,
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

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;

		refresh();
	}

	public void nextPage(){
		pageIndex++;
		pageIndex = Math.min(pageIndex, this.pages.size() - 1);
		y_offset = 0;
		x_offset = 0;

		refresh();
	}

	public void previousPage(){
		pageIndex--;
		pageIndex = Math.max(pageIndex, 0);
		y_offset = 0;
		x_offset = 0;

		refresh();
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void changeScale(float dScale){
		this.scale += dScale;

		scale = Math.max(scale, 0.5F);
		scale = Math.min(scale, 2F);

        try {
            this.drawPages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

		refresh();
	}

	public void setScale(float scale) {
		this.scale = scale;

		this.scale = Math.max(this.scale, 0.5F);
		this.scale = Math.min(this.scale, 2F);

		refresh();
	}

	public float getScale() {
		return scale;
	}

	public void setOffset(int x_offset, int y_offset) {
		this.x_offset = x_offset;
		this.y_offset = y_offset;

		refresh();
	}

	public void changeOffset(int dX_offset, int dY_offset) {
		this.x_offset += dX_offset;
		this.y_offset += dY_offset;

		refresh();
	}

	private void refresh(){
		this.pagesPanel.repaint();
		this.repaint();
	}

	public int getX_offset() {
		return x_offset;
	}

	public int getY_offset() {
		return y_offset;
	}
}