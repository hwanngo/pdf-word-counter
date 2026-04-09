import javax.swing.SwingUtilities;

public class WordCount {
    static Runnable createStartupAction(Runnable bootstrap) {
        return bootstrap;
    }

    static void launch() {
        Model model = new Model();
        View view = new View();
        new Controller(view, model);
        view.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(createStartupAction(WordCount::launch));
    }

}
