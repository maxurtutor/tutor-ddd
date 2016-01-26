package org.maxur.ddd.domain;

import org.maxur.ddd.dao.TeamDao;
import org.maxur.ddd.dao.UserDao;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class TeamRepository {

    private final DBI dbi;

    @Inject
    public TeamRepository(DBI dbi) {
        this.dbi = dbi;
    }

    public Team findById(String id) {
        return dbi.onDemand(TeamDao.class).findById(id);
    }

}
