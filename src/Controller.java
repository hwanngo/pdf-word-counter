import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

class Controller {
    private Model theModel;
    private View theView;

    interface CountModelActions {
        void runCountWord(File file) throws IOException;

        void clearProcessingState();

        String getOriginalText();

        int getResult();
    }

    interface CountViewActions {
        void setBusy(boolean busy);

        void setText(String text);

        void setResult(int result);

        void displayError(String error);
    }

    Controller(View theView, Model theModel) {
        this.theView = theView;
        this.theModel = theModel;
        this.theView.addActionListener(new  btnChooseActionListener(), new btnCountActionListener(), new btnAboutActionListener(), new btnTokenizedTextActionListener(), new btnPdfStopWordsActionListener());
    }

    static boolean shouldProcessSelection(int returnVal, File selectedFile) {
        return returnVal == JFileChooser.APPROVE_OPTION && selectedFile != null;
    }

    static SwingWorker<Void, Void> createCountWorker(CountModelActions model, CountViewActions view, File selectedFile) {
        return new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                model.runCountWord(selectedFile);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.setText(model.getOriginalText());
                    view.setResult(model.getResult());
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    String message = cause.getMessage();
                    model.clearProcessingState();
                    view.setText("");
                    view.setResult(0);
                    view.displayError(message == null ? cause.toString() : message);
                } finally {
                    view.setBusy(false);
                }
            }
        };
    }

    private void processFileInBackground(File selectedFile) {
        theView.setBusy(true);
        createCountWorker(new CountModelActions() {
            @Override
            public void runCountWord(File file) throws IOException {
                theModel.runCountWord(file);
            }

            @Override
            public void clearProcessingState() {
                theModel.clearProcessingState();
            }

            @Override
            public String getOriginalText() {
                return theModel.getOriginalText();
            }

            @Override
            public int getResult() {
                return theModel.getResult();
            }
        }, new CountViewActions() {
            @Override
            public void setBusy(boolean busy) {
                theView.setBusy(busy);
            }

            @Override
            public void setText(String text) {
                theView.setText(text);
            }

            @Override
            public void setResult(int result) {
                theView.setResult(result);
            }

            @Override
            public void displayError(String error) {
                theView.displayError(error);
            }
        }, selectedFile).execute();
    }

    public class btnCountActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            theView.setResult(theModel.getResult());
        }
    }

    public class btnChooseActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter extFilter = new FileNameExtensionFilter("PDF File", "pdf");
            chooser.setFileFilter(extFilter);

            int returnVal = chooser.showOpenDialog((JButton) e.getSource());
            File selectedFile = chooser.getSelectedFile();
            if (!shouldProcessSelection(returnVal, selectedFile)) {
                return;
            }

            theView.setFile(selectedFile);
            theView.setFilePathField(selectedFile.getPath());
            processFileInBackground(selectedFile);
        }

    }

    public static class btnAboutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Danh sách thành viên \n - Ngô Công Hoan - 16001788 \n - Nguyễn Sơn Tùng - 16001888 \n - Đào Hồng Hà - 16001775", "Thông tin nhóm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

//    public class btnStopWordsActionListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            theView.setTextResult(theModel.getStopWords());
//        }
//    }

    public class btnPdfStopWordsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            theView.setTextResult(theModel.getPdfStopWords());
        }
    }

    public class btnTokenizedTextActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            theView.setTextResult(theModel.getTokenizedText());
        }
    }
}
