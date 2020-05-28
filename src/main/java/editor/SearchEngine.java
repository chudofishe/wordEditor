package editor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEngine extends SwingWorker<List<List<Integer>>, Object> {


    String searchInput;

    public SearchEngine(String searchInput) {
        this.searchInput = searchInput;
    }

    @Override
    public List<List<Integer>> doInBackground() {
        Pattern pattern = Pattern.compile(this.searchInput);
        Matcher matcher = pattern.matcher(TextEditor.textArea.getText());
        List<List<Integer>> indexes = new ArrayList<>();
        while (matcher.find()){
            indexes.add(List.of(matcher.start(), matcher.end()));
        }
        return indexes;
    }
}
