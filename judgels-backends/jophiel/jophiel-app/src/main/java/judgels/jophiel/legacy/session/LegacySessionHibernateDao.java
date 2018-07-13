package judgels.jophiel.legacy.session;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class LegacySessionHibernateDao extends UnmodifiableHibernateDao<LegacySessionModel>
        implements LegacySessionDao {

    @Inject
    public LegacySessionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<LegacySessionModel> findByAuthCode(String authCode) {
        return selectByUniqueColumn(LegacySessionModel_.authCode, authCode);
    }

    @Override
    public Optional<LegacySessionModel> findByToken(String token) {
        return selectByUniqueColumn(LegacySessionModel_.token, token);
    }
}
