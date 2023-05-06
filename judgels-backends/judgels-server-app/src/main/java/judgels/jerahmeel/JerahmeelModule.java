package judgels.jerahmeel;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Singleton;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.sandalphon.submission.bundle.BaseItemSubmissionStore;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.programming.BaseSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;

@Module
public class JerahmeelModule {
    private final JerahmeelConfiguration config;

    public JerahmeelModule(JerahmeelConfiguration config) {
        this.config = config;
    }

    @Provides
    @JerahmeelBaseDataDir
    Path jerahmeelBaseDataDir() {
        return Paths.get(config.getBaseDataDir());
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(SessionStore sessionStore) {
        return new ActorChecker(authHeader ->
                sessionStore.getSessionByToken(authHeader.getBearerToken()).map(Session::getUserJid));
    }

    @Provides
    @Singleton
    static SubmissionStore submissionStore(
            ProgrammingSubmissionDao submissionDao,
            ProgrammingGradingDao gradingDao,
            ObjectMapper mapper) {

        return new BaseSubmissionStore<>(submissionDao, gradingDao, mapper);
    }

    @Provides
    @Singleton
    static ItemSubmissionStore itemSubmissionStore(BundleItemSubmissionDao submissionDao) {
        return new BaseItemSubmissionStore<>(submissionDao);
    }
}