package judgels.michael.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class EditStatementView extends TemplateView {
    private final String basePath;
    private final String language;
    private final Set<String> enabledLanguages;

    public EditStatementView(
            HtmlTemplate template,
            EditStatementForm form,
            String basePath,
            String language,
            Set<String> enabledLanguages) {

        super("editStatementView.ftl", template, form);
        this.basePath = basePath;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getLanguage() {
        return language;
    }

    public Map<String, String> getEnabledLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        for (String lang : enabledLanguages) {
            languages.put(lang, WorldLanguageRegistry.getInstance().getLanguages().get(lang));
        }
        return languages;
    }
}
