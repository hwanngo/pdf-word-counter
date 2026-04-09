import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;

class View extends JFrame {

    private JPanel contentPane;
    private JMenuBar menuBar;
    private JMenu mnHelp;
    private JButton mnhAbout;
    private JPanel mainPane;
    private File file;
    private JTextArea resultArea;
    private JTextArea textArea;
    private JButton btnChooseFile;
    private JButton btnCountWord;
//    private JButton btnStopWords;
    private JButton btnPdfStopWords;
    private JButton btnTokenizedText;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JTextField filePathField;

    View() {
    	setFont(new Font("Dialog", Font.PLAIN, 15));
        setTitle("WordCountPDF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        menuBar = new JMenuBar();
        mnHelp = new JMenu("Trợ giúp");
        mnhAbout = new JButton("Thông tin nhóm");
        mnHelp.add(mnhAbout);
        menuBar.add(mnHelp);
        getContentPane().add(menuBar, BorderLayout.PAGE_START);

        mainPane = new JPanel();
        mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.add(mainPane, BorderLayout.CENTER);

        GridBagLayout gbl_mainPane = new GridBagLayout();
        gbl_mainPane.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_mainPane.rowHeights = new int[]{38, 104, 0, 104, 0};
        gbl_mainPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_mainPane.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        mainPane.setLayout(gbl_mainPane);

        JLabel lblFile = new JLabel("File:");
        lblFile.setFont(new Font("Tahoma", Font.PLAIN, 15));
        GridBagConstraints gbc_lblFile = new GridBagConstraints();
        gbc_lblFile.insets = new Insets(0, 0, 5, 5);
        gbc_lblFile.anchor = GridBagConstraints.WEST;
        gbc_lblFile.gridx = 0;
        gbc_lblFile.gridy = 0;
        mainPane.add(lblFile, gbc_lblFile);

        filePathField = new JTextField();
        filePathField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        filePathField.setEditable(false);
        GridBagConstraints gbc_filePathField = new GridBagConstraints();
        gbc_filePathField.insets = new Insets(0, 0, 5, 5);
        gbc_filePathField.fill = GridBagConstraints.BOTH;
        gbc_filePathField.gridx = 1;
        gbc_filePathField.gridy = 0;
        mainPane.add(filePathField, gbc_filePathField);
        filePathField.setColumns(10);

        btnChooseFile = new JButton("Chọn file .pdf");
        btnChooseFile.setFont(new Font("Tahoma", Font.PLAIN, 15));

        GridBagConstraints gbc_btnChooseFile = new GridBagConstraints();
        gbc_btnChooseFile.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnChooseFile.insets = new Insets(0, 0, 5, 0);
        gbc_btnChooseFile.gridx = 3;
        gbc_btnChooseFile.gridy = 0;
        mainPane.add(btnChooseFile, gbc_btnChooseFile);

        JLabel lblText = new JLabel("Nội dung văn bản:");
        lblText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        GridBagConstraints gbc_lblText = new GridBagConstraints();
        gbc_lblText.anchor = GridBagConstraints.WEST;
        gbc_lblText.insets = new Insets(0, 0, 5, 5);
        gbc_lblText.gridx = 0;
        gbc_lblText.gridy = 1;
        mainPane.add(lblText, gbc_lblText);

        scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 1;
        mainPane.add(scrollPane, gbc_scrollPane);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setViewportView(textArea);
        
        btnTokenizedText = new JButton("Nhận diện từ");
        btnTokenizedText.setFont(new Font("Tahoma", Font.PLAIN, 15));
        GridBagConstraints gbc_btnTokenizedText = new GridBagConstraints();
        gbc_btnTokenizedText.insets = new Insets(0, 0, 5, 0);
        gbc_btnTokenizedText.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnTokenizedText.gridx = 3;
        gbc_btnTokenizedText.gridy = 1;
        mainPane.add(btnTokenizedText, gbc_btnTokenizedText);
        
//        btnStopWords = new JButton("Từ điển từ dừng");
//        btnStopWords.setFont(new Font("Tahoma", Font.PLAIN, 15));
//        GridBagConstraints gbc_btnStopWords = new GridBagConstraints();
//        gbc_btnStopWords.insets = new Insets(0, 0, 5, 0);
//        gbc_btnStopWords.fill = GridBagConstraints.HORIZONTAL;
//        gbc_btnStopWords.gridx = 3;
//        gbc_btnStopWords.gridy = 1;
//        mainPane.add(btnStopWords, gbc_btnStopWords);
//        gbc_btnStopWords.gridx = 3;
//        gbc_btnStopWords.gridy = 2;
        
        btnCountWord = new JButton("Đếm số từ");
        btnCountWord.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        	}
        });
        btnCountWord.setFont(new Font("Tahoma", Font.PLAIN, 15));
        GridBagConstraints gbc_btnCountWord = new GridBagConstraints();
        gbc_btnCountWord.insets = new Insets(0, 0, 5, 5);
        gbc_btnCountWord.gridx = 1;
        gbc_btnCountWord.gridy = 2;
        mainPane.add(btnCountWord, gbc_btnCountWord);
        gbc_btnCountWord.gridx = 2;
        gbc_btnCountWord.gridy = 3;

        JLabel lblResult = new JLabel("Kết quả:");
        lblResult.setFont(new Font("Tahoma", Font.PLAIN, 15));
        GridBagConstraints gbc_lblResult = new GridBagConstraints();
        gbc_lblResult.anchor = GridBagConstraints.WEST;
        gbc_lblResult.insets = new Insets(0, 0, 0, 5);
        gbc_lblResult.gridx = 0;
        gbc_lblResult.gridy = 3;
        mainPane.add(lblResult, gbc_lblResult);

        scrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane_1.gridx = 1;
        gbc_scrollPane_1.gridy = 3;
        mainPane.add(scrollPane_1, gbc_scrollPane_1);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        scrollPane_1.setViewportView(resultArea);
        
        btnPdfStopWords = new JButton("Từ dừng");
        btnPdfStopWords.setFont(new Font("Tahoma", Font.PLAIN, 15));
        GridBagConstraints gbc_btnPdfStopWords = new GridBagConstraints();
        gbc_btnPdfStopWords.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnPdfStopWords.gridx = 3;
        gbc_btnPdfStopWords.gridy = 3;
        mainPane.add(btnPdfStopWords, gbc_btnPdfStopWords);
        gbc_btnPdfStopWords.gridx = 3;
        gbc_btnPdfStopWords.gridy = 2;

    }

    void setResult(int result){
        resultArea.setText(String.valueOf(result));
    }

    void setTextResult(String result) {
        resultArea.setText(result);
    }

    void setText(String text){
        textArea.setText(text);
    }

    File getFile() {
        return file;
    }

    void setFile(File file) {
        this.file = file;
    }

    void setFilePathField(String filePath) {
        this.filePathField.setText(filePath);
    }

    void setBusy(boolean busy) {
        btnChooseFile.setEnabled(!busy);
        btnCountWord.setEnabled(!busy);
        btnPdfStopWords.setEnabled(!busy);
        btnTokenizedText.setEnabled(!busy);
        mnhAbout.setEnabled(!busy);
    }

    void addActionListener(ActionListener btnChooseAL, ActionListener btnCountAL, ActionListener btnAbout, ActionListener btnTokenizedTextAL, ActionListener btnPdfStopWordsAL){
        btnChooseFile.addActionListener(btnChooseAL);
        btnCountWord.addActionListener(btnCountAL);
        btnPdfStopWords.addActionListener(btnPdfStopWordsAL);
        btnTokenizedText.addActionListener(btnTokenizedTextAL);
        mnhAbout.addActionListener(btnAbout);
    }

    void displayError(String error){
        JOptionPane.showMessageDialog(this, error);
    }
}
