package editor;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    private Integer pointerPos;
    protected static JTextArea textArea = new JTextArea();
    private JCheckBox regularOrNot = new JCheckBox("Use Regex");
    private List<List<Integer>> ranges = new ArrayList<>();
    private ListIterator<List<Integer>> iterator = ranges.listIterator();
    private JTextField textField = new JTextField();

    private void textAreaPanel() {
        JPanel centerPanel = new JPanel();
        int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ;
        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED ;
        JScrollPane scroll = new JScrollPane(textArea, v, h);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.setVisible(true);
        textArea.setName("TextArea");
        scroll.setName("ScrollPane");
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(centerPanel, BorderLayout.CENTER);
    }

    private void saveAction () {
        JFileChooser jfc = new JFileChooser();
        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile.getAbsolutePath()));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException | NullPointerException exception) {
                exception.getMessage();
            }
        }
    }

    private void loadAction (){
        JFileChooser jfc = new JFileChooser();
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            try {
                String data = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                textArea.setText(data);
            } catch (IOException ex) {
                ex.getMessage();
                textArea.setText("");
            }
            System.out.println(selectedFile.getAbsolutePath());
        }
    }

    private void searchAction (String input) throws ExecutionException, InterruptedException {
        StringBuilder searchInput = new StringBuilder(input);
        if (!regularOrNot.isSelected()) {
            Matcher matcher = Pattern.compile("[$&+,:;=\\\\\\\\?@#|/'<>.^*()%!-]").matcher(searchInput.toString());
            while (matcher.find()) {
                searchInput.insert(matcher.start(),"\\");
            }
        }
        SearchEngine searchEngine = new SearchEngine(searchInput.toString());
        searchEngine.execute();
        ranges.clear();
        ranges.addAll(searchEngine.get());
        iterator = ranges.listIterator();
        pointerPos = 0;
        System.out.println(ranges);
        if (iterator.hasNext()){
            System.out.println(iterator.nextIndex());
            List<Integer> wordRange = new ArrayList<>(ranges.get(0));
            textArea.setCaretPosition(wordRange.get(1));
            textArea.select(wordRange.get(0), wordRange.get(1));
            textArea.grabFocus();
        } else {
            JOptionPane.showMessageDialog(null, "No matches found.");
        }
    }

    private void yieldNext () {
        if (iterator.hasNext()){
            if (iterator.nextIndex() == pointerPos) {
                iterator.next();
            }
            List<Integer> wordRange = new ArrayList<>(iterator.next());
            pointerPos++;
            textArea.setCaretPosition(wordRange.get(1));
            textArea.select(wordRange.get(0), wordRange.get(1));
        } else {
            iterator = ranges.listIterator(0);
            pointerPos = 0;
            List<Integer> wordRange = new ArrayList<>(ranges.get(pointerPos)) ;
            textArea.setCaretPosition(wordRange.get(1));
            textArea.select(wordRange.get(0), wordRange.get(1));
        }
        textArea.grabFocus();
    }

    private void yieldPrev () {
        if (iterator.hasPrevious()){
            if (iterator.previousIndex() == pointerPos && pointerPos!= 0) {
                iterator.previous();
            }
            List<Integer> wordRange = new ArrayList<>(iterator.previous());
            pointerPos--;
            textArea.setCaretPosition(wordRange.get(1));
            textArea.select(wordRange.get(0), wordRange.get(1));
        } else {
            iterator = ranges.listIterator(ranges.size() - 1);
            pointerPos = ranges.size() - 1;
            List<Integer> wordRange = new ArrayList<>(ranges.get(pointerPos)) ;
            textArea.setCaretPosition(wordRange.get(1));
            textArea.select(wordRange.get(0), wordRange.get(1));
        }
        textArea.grabFocus();
    }

    private void menu () {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem loadMenuItem = new JMenuItem("Open");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        saveMenuItem.setName("MenuSave");
        loadMenuItem.setName("MenuOpen");
        exitMenuItem.setName("MenuExit");

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
        menuBar.setVisible(true);
    }

    private void topPanel() {

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textField.setPreferredSize(new Dimension(300,30));

        TopPanelButton save = new TopPanelButton("src/main/resources/saveIcon.png",
                "SaveButton", new Dimension(64,64));
        TopPanelButton load = new TopPanelButton("src/main/resources/openIcon.png",
                "OpenButton", new Dimension(64,64));
        TopPanelButton search = new TopPanelButton("src/main/resources/searchIcon.png",
                "StartSearchButton", new Dimension(64,64));
        TopPanelButton prev = new TopPanelButton("src/main/resources/prevIcon.png",
                "PreviousMatchButton", new Dimension(40,40));
        TopPanelButton next = new TopPanelButton("src/main/resources/nextIcon.png",
                "NextMatchButton", new Dimension(40,40));

        save.addActionListener(event -> saveAction());
        load.addActionListener(event -> loadAction());
        search.addActionListener(event -> {
            try {
                searchAction(textField.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        next.addActionListener(event -> {
            if (ranges.size() != 0) {
                yieldNext();
            }
        });

        prev.addActionListener(event -> {
            if (ranges.size() != 0) {
                yieldPrev();
            }
        });

        regularOrNot.setSelected(false);

        topPanel.add(load);
        topPanel.add(save);
        topPanel.add(textField);
        topPanel.add(search);
        topPanel.add(prev);
        topPanel.add(next);
        topPanel.add(regularOrNot);

        topPanel.setVisible(true);
        topPanel.setPreferredSize(new Dimension(600,80));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 5));
        add(topPanel, BorderLayout.NORTH);
    }

    public TextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        textAreaPanel();
        topPanel();
        menu();
        setVisible(true);
        setTitle("Cool Text Editor!");
    }
}
