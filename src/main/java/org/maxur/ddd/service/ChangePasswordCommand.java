package org.maxur.ddd.service;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import org.maxur.ddd.domain.*;

import javax.inject.Inject;
import java.util.Optional;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
public class ChangePasswordCommand extends Command<User> {

    private final String userId;

    private final String password;

    private User user;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private EventBus eventBus;

    private Team team;

    public ChangePasswordCommand(String userId, String password) {
        super();
        this.userId = userId;
        this.password = password;
    }

    @Override
    public User result() {
        return null;
    }

    @Override
    public void validate() throws BusinessException {
        if (Strings.isNullOrEmpty(password)) {
            throw new BusinessException("password cannot be empty");
        }
        user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User", userId);
        }
        team = teamRepository.findById(user.getTeamId());
        if (team == null) {
            throw new NotFoundException("Team", user.getTeamId());
        }

    }

    @Override
    public void execute() throws BusinessException {
        user.changePassword(password);
        userRepository.update(user, team);
        final OnPasswordChangedEvent event = new OnPasswordChangedEvent(user);
        eventBus.post(event);
        final Optional<RuntimeException> response = event.response(10000);
        if (response.isPresent()) {
            rollback();
            throw response.get();
        }
    }

}
