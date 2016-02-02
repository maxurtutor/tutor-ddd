package org.maxur.ddd.planning.domain;

import org.maxur.ddd.admin.domain.UserRepository;
import org.maxur.ddd.commons.domain.BusinessException;
import org.maxur.ddd.commons.domain.Entity;
import org.maxur.ddd.commons.domain.Id;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static org.maxur.ddd.commons.domain.ServiceLocatorProvider.service;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>12.12.2015</pre>
 */
public class Team extends Entity<Team> {

    private String name;

    private Integer maxCapacity;

    private Collection<TeamMember> members = new HashSet<>();

    private Team(Id<Team> id, String name, Integer maxCapacity) {
        super(id);
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public Team(String name, Integer maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public static Team newTeam(String name, Integer maxCapacity) throws BusinessException {
        return new Team(
                checkName(name),
                checkMaxCapacity(maxCapacity)
        );
    }

    public static Team restore(Snapshot snapshot) throws BusinessException {
        final Team team = new Team(
            Id.id(snapshot.getId()),
            checkName(snapshot.getName()),
            checkMaxCapacity(snapshot.getMaxCapacity())
        );
        if (snapshot.getMembers() != null)
        for(TeamMember.Snapshot member : snapshot.getMembers()){
            team.addTeamMember(TeamMember.restore(member));
        }
        return team;
    }

    private void addTeamMember(TeamMember member) {
        members.add(member);
    }

    private static String  checkName(String name) throws BusinessException {
        if (isNullOrEmpty(name)) {
            throw new BusinessException("Team name must not be empty");
        }
        return name;
    }

    private static Integer checkMaxCapacity(Integer maxCapacity) throws BusinessException {
        if (maxCapacity < 1) {
            throw new BusinessException("Team max Capacity must be more than 0");
        }
        return maxCapacity;
    }

    public String getName() {
        return name;
    }

    public void checkTeamCapacity() throws BusinessException {
        Integer count = service(UserRepository.class).findCountByTeam(getId().asString());
        if (Objects.equals(count, maxCapacity)) {
            throw new BusinessException("The limit users in team is exceeded");
        }
    }

    public Team.Snapshot getSnapshot() {
        final Snapshot snapshot = new Snapshot();
        snapshot.setId(getId().asString());
        snapshot.setName(this.name);
        snapshot.setMaxCapacity(this.maxCapacity);
        snapshot.setMembers(members.stream().map(TeamMember::getSnapshot).collect(toList()));
        return snapshot;
    }

    @SuppressWarnings("WeakerAccess")
    public static class Snapshot {
        private String id;
        private String name;
        private Integer maxCapacity;
        private Collection<TeamMember.Snapshot> members;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public Collection<TeamMember.Snapshot> getMembers() {
            return members;
        }

        public void setMembers(Collection<TeamMember.Snapshot> members) {
            this.members = members;
        }
    }
}
