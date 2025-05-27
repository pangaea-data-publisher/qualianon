package org.qualiservice.qualianon.gui.components;

import javafx.stage.Stage;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.tools.ContextualMainMenuItem;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Replacement;


public interface UIInterface {

    Stage getStage();

    void showReplacement(Replacement replacement);

    void searchUnmarkedOccurrences(PositionRange range, String selectedText, AnonymizedFile document);

    void showMarkersForReplacement(Replacement replacement);

    MessageLogger getMessageLogger();

    ContextualMainMenuItem getRenameMenuItem();

    void showCategoriesTab();

    void showSearchResultInMainView(SearchResult searchResult);

}
