package com.tomaszekem.userservice.user;

import com.tomaszekem.userservice.exception.RequestValidationException;
import com.tomaszekem.userservice.notification.NotificationService;
import com.tomaszekem.userservice.notification.SendEmailNotificationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.*;

@Service
class UserService {

    private static final String WELCOME_SUBJECT = "Welcome!";
    private static final String WELCOME_MESSAGE_CONTENT = "Welcome to our site";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    UserService(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public List<UserDTO> registerNewUsers(Set<RegisterUserCommand> commands) {
        log.info("Registering new users: {}", commands);
        validateElementsUniquenessForCreation(commands);

        List<UserEntity> usersToSave = toEntities(commands);

        List<UserEntity> savedUsers = userRepository.saveAll(usersToSave);
        sendWelcomeNotifications(savedUsers);

        return toDTOs(savedUsers);
    }

    @Transactional
    public List<UserDTO> updateUsers(List<UpdateUserCommand> commands) {
        log.info("Updating users: {}", commands);
        validateUpdatedElementsUniqueness(commands);
        Set<Long> userIds = commands.stream()
                .map(UpdateUserCommand::getId)
                .collect(toSet());

        List<UserEntity> users = userRepository.findByIdIn(userIds);
        Map<Long, UpdateUserCommand> userIdsToCommands = commands.stream()
                .collect(toMap(UpdateUserCommand::getId, Function.identity()));


        users.forEach(u -> {
            var updateUserCommand = userIdsToCommands.get(u.getId());
            if (updateUserCommand == null) {
                throw new RequestValidationException(format("User with id %d does not exist. Cannot update", updateUserCommand.getId()));
            }
            u.update(updateUserCommand);
        });


        return toDTOs(users);
    }

    @Transactional
    public void deleteUsers(DeleteUsersCommand command) {
        log.info("Deleting users: {}", command);
        List<UserEntity> users = userRepository.findByIdIn(command.getIds());
        users.forEach(UserEntity::markAsDeleted);
    }

    public Page<UserDTO> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDTO::fromEntity);
    }

    private List<UserDTO> toDTOs(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(UserDTO::fromEntity)
                .collect(toList());
    }

    private List<UserEntity> toEntities(Set<RegisterUserCommand> commands) {
        return commands.stream()
                .map(c -> new UserEntity(c.getFirstName(), c.getLastName(), c.getEmail()))
                .collect(toList());
    }

    private void validateElementsUniquenessForCreation(Set<RegisterUserCommand> commands) {
        validateUniquenessAmongEachOther(commands);
        validateEmailsUniquenessInDatabase(commands);
    }

    private void validateUniquenessAmongEachOther(Set<RegisterUserCommand> commands) {
        Set<String> nonUniqueEmails = commands.stream()
                .collect(groupingBy(RegisterUserCommand::getEmail))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(toSet());
        if (!nonUniqueEmails.isEmpty()) {
            String joinedEmails = String.join(",", nonUniqueEmails);
            throw new RequestValidationException(format("Attempted to create users with same emails: %s", joinedEmails));
        }
    }

    private void validateUpdatedElementsUniqueness(List<UpdateUserCommand> commands) {
        validateUniquenessAmongEachOther(commands);
        validateEmailUniquenessForUpdate(commands);
    }

    private void validateEmailUniquenessForUpdate(List<UpdateUserCommand> commands) {
        Map<String, Long> updatedEmailsToUserIds = commands.stream()
                .collect(toMap(UpdateUserCommand::getEmail, UpdateUserCommand::getId));

        Set<String> updatedEmails = commands.stream()
                .map(UpdateUserCommand::getEmail)
                .collect(toSet());

        List<UserToEmailProjection> usersToEmails = userRepository.findByEmails(updatedEmails);

        List<String> usedEmails = usersToEmails.stream()
                .filter(u -> !updatedEmailsToUserIds.get(u.getUserEmail()).equals(u.getUserId()))
                .map(UserToEmailProjection::getUserEmail)
                .collect(toList());
        if (!usedEmails.isEmpty()) {
            throwNonUniqueEmailsException(usedEmails);
        }

    }

    private void validateUniquenessAmongEachOther(List<UpdateUserCommand> commands) {
        Set<Long> idsForNonUniqueUpdates = commands.stream()
                .collect(groupingBy(UpdateUserCommand::getId))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(toSet());
        if (!idsForNonUniqueUpdates.isEmpty()) {
            String joinedNonUniqueIds = idsForNonUniqueUpdates.stream().map(String::valueOf)
                    .collect(joining(","));
            throw new RequestValidationException(format("Attempted to update more than once users with ids: %s", joinedNonUniqueIds));
        }
    }

    private void sendWelcomeNotifications(List<UserEntity> registeredUsers) {
        List<SendEmailNotificationCommand> sendEmailCommands = registeredUsers.stream()
                .map(u -> new SendEmailNotificationCommand(u.getEmail(), WELCOME_SUBJECT, WELCOME_MESSAGE_CONTENT))
                .collect(toList());
        notificationService.queueEmailNotifications(sendEmailCommands);
    }

    private void validateEmailsUniquenessInDatabase(Set<RegisterUserCommand> commands) {
        Set<String> emails = commands.stream()
                .map(RegisterUserCommand::getEmail)
                .collect(Collectors.toSet());

        List<String> usedEmails = userRepository.findUsedEmails(emails);
        if (!usedEmails.isEmpty()) {
            throwNonUniqueEmailsException(usedEmails);
        }
    }

    private void throwNonUniqueEmailsException(List<String> usedEmails) {
        String collectedEmails = String.join("", usedEmails);
        throw new RequestValidationException(format("Following emails are already in use: %s", collectedEmails));
    }
}
