package org.qualiservice.qualianon.model.text;

import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.project.Replacement;

import java.util.HashMap;


public class ReplacementCounter {

    private final HashMap<Replacement, Integer> replacementCounters;
    private final HashMap<CategoryScheme, Integer> categoryCounters;

    public ReplacementCounter() {
        replacementCounters = new HashMap<>();
        categoryCounters = new HashMap<>();
    }

    public int get(Replacement replacement) {
        if (replacementCounters.containsKey(replacement)) {
            return replacementCounters.get(replacement);
        }

        int next;
        if (!categoryCounters.containsKey(replacement.getCategoryScheme())) {
            next = 1;
        } else {
            next = categoryCounters.get(replacement.getCategoryScheme()) + 1;
        }

        categoryCounters.put(replacement.getCategoryScheme(), next);
        replacementCounters.put(replacement, next);
        return next;
    }

}
