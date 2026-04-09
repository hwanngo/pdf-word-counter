import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.junit.Test;

public class ControllerWorkerSmokeTest {

    @Test
    public void shouldSkipProcessingWhenChooserIsCancelled() {
        assertFalse(Controller.shouldProcessSelection(JFileChooser.CANCEL_OPTION, null));
    }

    @Test
    public void shouldRequireAFileForApprovedSelections() {
        assertFalse(Controller.shouldProcessSelection(JFileChooser.APPROVE_OPTION, null));
    }

    @Test
    public void shouldAllowApprovedSelectionWithAFile() {
        assertTrue(Controller.shouldProcessSelection(JFileChooser.APPROVE_OPTION, new File("demo.pdf")));
    }

    @Test
    public void countWorkerRunsModelOffEdtAndUpdatesViewOnSuccess() throws Exception {
        FakeCountModel model = new FakeCountModel();
        FakeCountView view = new FakeCountView();

        SwingWorker<Void, Void> worker = startWorkerOnEdt(model, view, new File("demo.pdf"));

        worker.get();
        waitForWorkerCallbacks(view);

        assertTrue(model.ran);
        assertFalse(model.ranOnEdt);
        assertEquals(Boolean.TRUE, view.firstBusyValue);
        assertEquals(Boolean.FALSE, view.lastBusyValue);
        assertEquals("original text", view.text);
        assertEquals(Integer.valueOf(42), view.result);
        assertNull(view.error);
        assertTrue(view.busyCalledOnEdt);
        assertTrue(view.textCalledOnEdt);
        assertTrue(view.resultCalledOnEdt);
    }

    @Test
    public void countWorkerSurfacesFailuresAndClearsBusyState() throws Exception {
        FakeCountModel model = new FakeCountModel();
        model.failure = new IOException("boom");
        FakeCountView view = new FakeCountView();
        view.text = "stale text";
        view.result = 99;

        SwingWorker<Void, Void> worker = startWorkerOnEdt(model, view, new File("demo.pdf"));

        try {
            worker.get();
            fail("Expected worker failure");
        } catch (ExecutionException expected) {
            assertTrue(expected.getCause() instanceof IOException);
        }
        waitForWorkerCallbacks(view);

        assertEquals(Boolean.TRUE, view.firstBusyValue);
        assertEquals(Boolean.FALSE, view.lastBusyValue);
        assertEquals("", view.text);
        assertEquals(Integer.valueOf(0), view.result);
        assertEquals("boom", view.error);
        assertTrue(model.clearProcessingStateCalled);
        assertEquals("", model.originalText);
        assertEquals("", model.tokenizedText);
        assertEquals("[]", model.pdfStopWords);
        assertTrue(view.busyCalledOnEdt);
        assertTrue(view.textCalledOnEdt);
        assertTrue(view.resultCalledOnEdt);
        assertTrue(view.errorCalledOnEdt);
    }

    @Test
    public void startupActionRunsBootstrapOnEdt() throws Exception {
        AtomicBoolean bootstrapRan = new AtomicBoolean(false);
        AtomicBoolean bootstrapOnEdt = new AtomicBoolean(false);

        Runnable startupAction = WordCount.createStartupAction(() -> {
            bootstrapRan.set(true);
            bootstrapOnEdt.set(SwingUtilities.isEventDispatchThread());
        });

        SwingUtilities.invokeAndWait(startupAction);

        assertTrue(bootstrapRan.get());
        assertTrue(bootstrapOnEdt.get());
    }

    private static class FakeCountModel implements Controller.CountModelActions {
        boolean ran;
        boolean ranOnEdt;
        IOException failure;
        boolean clearProcessingStateCalled;
        String originalText = "original text";
        String tokenizedText = "stale tokens";
        String pdfStopWords = "[stale_stop]";

        @Override
        public void runCountWord(File file) throws IOException {
            ran = true;
            ranOnEdt = SwingUtilities.isEventDispatchThread();
            if (failure != null) {
                throw failure;
            }
        }

        @Override
        public void clearProcessingState() {
            clearProcessingStateCalled = true;
            originalText = "";
            tokenizedText = "";
            pdfStopWords = "[]";
        }

        @Override
        public String getOriginalText() {
            return originalText;
        }

        @Override
        public int getResult() {
            return 42;
        }
    }

    private static SwingWorker<Void, Void> startWorkerOnEdt(Controller.CountModelActions model, FakeCountView view, File selectedFile) throws Exception {
        AtomicReference<SwingWorker<Void, Void>> workerRef = new AtomicReference<SwingWorker<Void, Void>>();
        SwingUtilities.invokeAndWait(() -> {
            view.setBusy(true);
            workerRef.set(Controller.createCountWorker(model, view, selectedFile));
            workerRef.get().execute();
        });
        return workerRef.get();
    }

    private static void waitForWorkerCallbacks(FakeCountView view) throws Exception {
        for (int i = 0; i < 50; i++) {
            SwingUtilities.invokeAndWait(() -> { });
            if (Boolean.FALSE.equals(view.lastBusyValue)) {
                return;
            }
            Thread.sleep(20L);
        }
    }

    private static class FakeCountView implements Controller.CountViewActions {
        Boolean firstBusyValue;
        Boolean lastBusyValue;
        String text;
        Integer result;
        String error;
        boolean busyCalledOnEdt = true;
        boolean textCalledOnEdt = true;
        boolean resultCalledOnEdt = true;
        boolean errorCalledOnEdt = true;

        @Override
        public void setBusy(boolean busy) {
            busyCalledOnEdt &= SwingUtilities.isEventDispatchThread();
            if (firstBusyValue == null) {
                firstBusyValue = busy;
            }
            lastBusyValue = busy;
        }

        @Override
        public void setText(String text) {
            textCalledOnEdt &= SwingUtilities.isEventDispatchThread();
            this.text = text;
        }

        @Override
        public void setResult(int result) {
            resultCalledOnEdt &= SwingUtilities.isEventDispatchThread();
            this.result = result;
        }

        @Override
        public void displayError(String error) {
            errorCalledOnEdt &= SwingUtilities.isEventDispatchThread();
            this.error = error;
        }
    }
}
