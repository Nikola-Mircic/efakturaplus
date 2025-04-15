package efakturaplus.util;

import efakturaplus.gui.pdf.PDFDisplay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.print.*;
import java.io.IOException;
import java.util.Arrays;

public class PrinterUtil {
    private static PrintService[] printServices;

    private PDFDisplay preveiw;

    private JFrame window;
    private PDDocument document;

    private PrintService selectedPrintService;
    private int copies;

    public PrinterUtil(PDDocument document) {
        findPrintService();

        this.selectedPrintService = printServices[0];
        this.copies = 1;

        this.document = document;
        try {
            this.preveiw = new PDFDisplay(document, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        createLayout();
    }

    private void findPrintService() {
        if (printServices == null) {
            printServices = PrintServiceLookup.lookupPrintServices(null, null);

            for(PrintService service : printServices){
                System.out.println(service.getName());
            }
        }
    }

    private void createLayout(){
        this.window = new JFrame("Print selected document");
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        preveiw.setBorder(BorderFactory.createEmptyBorder(10,10,10,5));
        mainPanel.add(preveiw);

        JPanel optionsPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        optionsPanel.setLayout(gridBagLayout);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10,5,10,10));
        //optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        // Printer selection
        JLabel printerLabel = new JLabel("Printer:");
        JComboBox printerComboBox = new JComboBox(Arrays.stream(printServices).map(PrintService::getName).toArray());
        //printerComboBox.setBorder(BorderFactory.createEmptyBorder(3,3,10,3));

        printerComboBox.addActionListener(e -> {
           for(PrintService service : printServices){
               if(service.getName().equals(printerComboBox.getSelectedItem().toString()))
                   selectedPrintService = service;
           }
        });

        optionsPanel.add(printerLabel, gbc(0));
        optionsPanel.add(printerComboBox, gbc(1));

        // Copies
        JLabel copiesLabel = new JLabel("Copies:");
        SpinnerModel model = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner copiesSpinner = new JSpinner(model);
        //copiesSpinner.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));

        copiesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                copies = (int) copiesSpinner.getValue();
            }
        });

        optionsPanel.add(copiesLabel, gbc(2));

        optionsPanel.add(copiesSpinner, gbc(3));

        // Page selection - TODO

        // Print
        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> {
            startPringJob();
        });
       // printButton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        GridBagConstraints printGbc = gbc(4);
        printGbc.anchor = GridBagConstraints.PAGE_END;
        printGbc.weighty = 1.0;
        optionsPanel.add(printButton, printGbc);

        // Cancel
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            this.window.dispose();
        });
        //cancelButton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        GridBagConstraints cancelGbc = gbc(5);
        cancelGbc.anchor = GridBagConstraints.PAGE_END;
        optionsPanel.add(cancelButton, cancelGbc);

        mainPanel.add(optionsPanel);
        window.add(mainPanel);
        window.pack();
    }

    private GridBagConstraints gbc(int i){
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.PAGE_START;

        return gbc;
    }

    private void startPringJob() {
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPageable(new PDFPageable(document));
        job.setCopies(copies);

        try {
            job.setPrintService(selectedPrintService);
            job.print();
        } catch (PrinterException e) {
            throw new RuntimeException(e);
        }
    }

    public void show(){
        window.setVisible(true);
    }
}
