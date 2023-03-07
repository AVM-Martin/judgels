package judgels.michael.problem.base.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.base.BaseProblemResource;
import judgels.michael.resource.EditStatementForm;
import judgels.michael.resource.EditStatementView;
import judgels.michael.resource.ListStatementLanguagesView;
import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.resource.WorldLanguageRegistry;

@Path("/problems/{problemId}/statements")
public class ProblemStatementResource extends BaseProblemResource {
    @Inject public ProblemStatementResource() {}

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editStatement(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = problemStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        ProblemStatement statement = problemStore.getStatement(actor.getUserJid(), problem.getJid(), language);

        EditStatementForm form = new EditStatementForm();
        form.title = statement.getTitle();
        form.text = statement.getText();

        return renderEditStatement(actor, problem, form, language, enabledLanguages);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response postEditStatement(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditStatementForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Set<String> enabledLanguages = problemStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.updateStatement(actor.getUserJid(), problem.getJid(), language, new ProblemStatement.Builder()
                .title(form.title)
                .text(form.text)
                .build());

        return Response
                .seeOther(URI.create("/problems/" + problem.getType().name().toLowerCase() + "/" + problem.getId() + "/statements"))
                .build();
    }

    @GET
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public View listStatementLanguages(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Map<String, StatementLanguageStatus> availableLanguages = problemStore.getStatementAvailableLanguages(actor.getUserJid(), problem.getJid());
        String defaultLanguage = problemStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("languages");
        return new ListStatementLanguagesView(template, availableLanguages, defaultLanguage);
    }

    @POST
    @Path("/languages")
    @UnitOfWork(readOnly = true)
    public Response postAddStatementLanguage(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormParam("language") String language) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        problemStore.addStatementLanguage(actor.getUserJid(), problem.getJid(), language);

        return Response
                .seeOther(URI.create("/problems/" + problem.getId() + "/statements/languages"))
                .build();
    }

    private View renderEditStatement(Actor actor, Problem problem, HtmlForm form, String language, Set<String> enabledLanguages) {
        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditStatementView(template, (EditStatementForm) form, language, enabledLanguages);
    }
}