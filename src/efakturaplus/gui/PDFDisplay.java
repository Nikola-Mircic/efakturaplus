package efakturaplus.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import javax.swing.JComponent;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

public class PDFDisplay extends JComponent implements KeyListener {
	private static final long serialVersionUID = 1L;
	// byte array containing the PDF file content
	private byte[] bytes = null;
	private String fileName;
	private int pageIndex;
	
	private PDFFile pdfFile = null;

	private double scale = 1.0;
	private int x_offset = 0;
	private int y_offset = 0;
	
	public PDFDisplay(String fileName, int pageIndex) {
		super();
		this.fileName = fileName;
		this.pageIndex = pageIndex;
		readFile();
	}
	
	public PDFDisplay(PDFFile pdfFile) {
		this.pdfFile = pdfFile;

	}

	private void readFile() {
		try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));){
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int b;
			while ((b = inputStream.read()) != -1) {
				outputStream.write(b);
			}
			this.bytes = outputStream.toByteArray();
			this.pdfFile = new PDFFile(ByteBuffer.wrap(this.bytes));	
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		try {
			if(this.pdfFile == null)
				return;
			PDFPage page = pdfFile.getPage(this.pageIndex);
			Paper paper = new Paper();
			int formatOrientation = page.getAspectRatio() > 1 ? PageFormat.LANDSCAPE
								: PageFormat.PORTRAIT;
			if(formatOrientation == PageFormat.LANDSCAPE) {
				paper.setSize(page.getHeight(), page.getWidth());
			}else {
				paper.setSize(page.getWidth(), page.getHeight());
			}				
			PageFormat pageFormat = new PageFormat();
			pageFormat.setPaper(paper);
			pageFormat.setOrientation(formatOrientation);

			Graphics2D g2d = (Graphics2D)g.create();
			Rectangle imgbounds = new Rectangle(x_offset, y_offset, (int)(pageFormat.getWidth() * scale),
							(int)(pageFormat.getHeight() * scale));
			PDFRenderer renderer = new PDFRenderer(page, g2d, imgbounds, null, Color.WHITE);
			try {
				page.waitForFinish();
			}
			catch (InterruptedException e) {
				// some exception handling
			}
			renderer.run();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
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
				}
				case KeyEvent.VK_SUBTRACT -> {
					this.scale -= 0.05;
				}
				case KeyEvent.VK_LEFT -> {
					x_offset -= 20;
				}
				case KeyEvent.VK_RIGHT -> {
					x_offset += 20;
				}
				case KeyEvent.VK_UP -> {
					y_offset += 20;
				}
				case KeyEvent.VK_DOWN -> {
					y_offset -= 20;
				}
			}
		}else{
			// Switching between pages
			switch (e.getKeyCode()){
				case KeyEvent.VK_LEFT -> {
					this.pageIndex++;

					this.pageIndex = Math.min(this.pageIndex, this.pdfFile.getNumPages());
				}
				case KeyEvent.VK_RIGHT -> {
					this.pageIndex--;

					this.pageIndex = Math.max(this.pageIndex, 1);
				}
			}
		}

		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}