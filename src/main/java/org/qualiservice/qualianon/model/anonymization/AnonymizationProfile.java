package org.qualiservice.qualianon.model.anonymization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.utility.UpdatableImpl;
import org.qualiservice.qualianon.utility.UpdateListener;

import java.beans.Transient;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class AnonymizationProfile extends UpdatableImpl {

    @JacksonXmlElementWrapper(localName = "categoryProfiles")
    @JacksonXmlProperty(localName = "categoryProfile")
    private List<CategoryProfile> categoryProfiles;

    private final UpdateListener categoryProfilesUpdateListener;


    public static AnonymizationProfile fromCategories(Categories categories) {
        final AnonymizationProfile profile = new AnonymizationProfile();
        for (final CategoryScheme category : categories.getCategories()) {
            final List<LabelProfile> labelProfiles = category.getLabels().stream()
                    .map(label -> new LabelProfile(label.getName(), false))
                    .collect(Collectors.toList());
            final CategoryProfile categoryProfile = new CategoryProfile(category.getName())
                    .setLabelProfiles(labelProfiles);
            profile.addCategoryProfile(categoryProfile);
        }
        return profile;
    }

    public AnonymizationProfile() {
        this.categoryProfiles = new LinkedList<>();
        categoryProfilesUpdateListener = (boolean isDirect) -> notifyUpdateListeners(false);
    }

    public List<CategoryProfile> getCategoryProfiles() {
        return categoryProfiles;
    }

    public CategoryProfile getCategoryProfile(CategoryScheme categoryScheme) {
        return categoryProfiles.stream()
                .filter(profile -> profile.getCategoryName().equals(categoryScheme.getName()))
                .findAny()
                .orElse(new CategoryProfile(categoryScheme.getName()));
    }

    // Called by XML deserialization
    @SuppressWarnings("unused")
    public void setCategoryProfiles(List<CategoryProfile> categoryProfiles) {
        this.categoryProfiles = categoryProfiles;
        categoryProfiles.forEach(categoryProfile -> categoryProfile.addUpdateListener(categoryProfilesUpdateListener));
        notifyUpdateListeners(true);
    }

    public AnonymizationProfile addCategoryProfile(CategoryProfile categoryProfile) {
        categoryProfiles.add(categoryProfile);
        categoryProfile.addUpdateListener(categoryProfilesUpdateListener);
        notifyUpdateListeners(true);
        return this;
    }

    public boolean isLabelEnabled(CategoryScheme categoryScheme, String labelName) {
        return getCategoryProfile(categoryScheme).isLabelEnabled(labelName);
    }

    public AnonymizationProfile setPersisted() {
        categoryProfiles.forEach(CategoryProfile::setPersisted);
        notifyUpdateListeners(true);
        return this;
    }

    @Transient
    public boolean isModified() {
        return categoryProfiles.stream()
                .map(CategoryProfile::isModified)
                .reduce((b1, b2) -> b1 || b2)
                .orElse(false);
    }

    public AnonymizationProfile clone(MessageLogger messageLogger) {
        try {
            final XmlMapper xmlMapper = new XmlMapper();
            final String xmlString = xmlMapper.writeValueAsString(this);
            final AnonymizationProfile clone = xmlMapper.readValue(xmlString, AnonymizationProfile.class);
            clone.categoryProfiles.forEach(categoryProfile -> categoryProfile.addUpdateListener(categoryProfilesUpdateListener));
            return clone;
        } catch (JsonProcessingException e) {
            messageLogger.logError("Clone error", e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnonymizationProfile that = (AnonymizationProfile) o;
        return Objects.equals(categoryProfiles, that.categoryProfiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryProfiles);
    }

    @Override
    public String toString() {
        return "AnonymizationProfile{" +
                "categoryProfiles=" + categoryProfiles +
                '}';
    }
}
