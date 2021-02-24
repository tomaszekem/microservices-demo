package com.tomaszekem.userservice.user

import com.tomaszekem.userservice.exception.RequestValidationException
import com.tomaszekem.userservice.notification.NotificationService
import com.tomaszekem.userservice.notification.SendEmailNotificationCommand
import spock.lang.Specification
import spock.lang.Subject

class UserServiceSpec extends Specification {

    @Subject
    UserService userService
    UserRepository userRepository = Mock()
    NotificationService notificationService = Mock()

    def setup() {
        userService = new UserService(userRepository, notificationService)
    }

    def 'should not allow to use existing email during registration'() {
        given:
        def userEmail = 'j.doe@acme.com'
        def commands = registerUsersCommands(userEmail)

        userRepository.findUsedEmails([userEmail].toSet()) >> [userEmail]

        when:
        userService.registerNewUsers(commands)

        then:
        def ex = thrown(RequestValidationException)
        ex.message == 'Following emails are already in use: ' + userEmail
    }

    def 'should not allow to delete non existent user'() {
        given:
        def userId = 1L
        def userIds = Set.of(userId)
        def deleteUsersCommand = new DeleteUsersCommand(userIds)

        userRepository.findByIdIn(userIds) >> List.of()

        when:
        userService.deleteUsers(deleteUsersCommand)

        then:
        def ex = thrown(RequestValidationException)
        ex.message == 'Users with ids 1 do not exist'
    }

    def 'should not allow to register users when same email is used in the the batch'() {
        given:
        def userEmail = 'j.doe@acme.com'
        def firstCommand = new RegisterUserCommand(
                firstName: 'John',
                lastName: 'Doe',
                email: userEmail
        )

        def secondCommand = new RegisterUserCommand(
                firstName: 'Mark',
                lastName: 'Brown',
                email: userEmail
        )

        def commands = [firstCommand, secondCommand].toSet()

        when:
        userService.registerNewUsers(commands)

        then:
        def ex = thrown(RequestValidationException)
        ex.message == 'Attempted to create users with same emails: ' + userEmail
    }

    def 'should queue events for correctly registered user'() {
        given:
        def userEmail = 'j.doe@acme.com'
        def commands = registerUsersCommands(userEmail)
        def userToCreate = commands[0]

        userRepository.findUsedEmails([userEmail].toSet()) >> []
        userRepository.saveAll(_ as Iterable<UserEntity>) >> [user(userToCreate.firstName, userToCreate.lastName, userToCreate.email)]

        when:
        userService.registerNewUsers(commands)

        then:
        1 * notificationService.queueEmailNotifications({ List<SendEmailNotificationCommand> notificationCommands ->
            notificationCommands.size() == 1 && notificationCommands[0].to == userEmail && notificationCommands[0].subject == 'Welcome!' && notificationCommands[0].message == 'Welcome to our site'
        })

    }

    def 'should not allow to update non existent user'() {
        given:
        def userId = 1L
        def userIds = Set.of(userId)
        def updateCommand = new UpdateUserCommand(
                id: userId,
                firstName: 'New name',
                lastName: 'New lastName',
                email: 'newemail@acme.com'
        )

        def updateCommands = List.of(updateCommand)
        userRepository.findByEmails(_ as Set<String>) >> List.of()
        userRepository.findByIdIn(userIds) >> List.of()

        when:
        userService.updateUsers(updateCommands)

        then:
        def ex = thrown(RequestValidationException)
        ex.message == 'Users with ids 1 do not exist'
    }

    def 'should block attempt of updating same user in the same bulk'() {
        given:
        def userId = 1
        def updateCommand = new UpdateUserCommand(
                id: userId,
                firstName: 'New name',
                lastName: 'New lastName',
                email: 'newemail@acme.com'
        )

        def updateCommands = [updateCommand, updateCommand]

        when:
        userService.updateUsers(updateCommands)

        then:
        def ex = thrown(RequestValidationException)
        ex.message == 'Attempted to update more than once users with ids: 1'
    }

    def 'should block update when any new email is already used'() {
        given:
        def userId = 1
        def usedEmail = 'newemail@acme.com'
        def updateCommand = new UpdateUserCommand(
                id: userId,
                firstName: 'New name',
                lastName: 'New lastName',
                email: usedEmail
        )

        def updateCommands = [updateCommand]
        userRepository.findByEmails([usedEmail].toSet()) >> [new UserToEmailProjection(5, usedEmail)]

        when:
        userService.updateUsers(updateCommands)

        then:
        def ex = thrown(RequestValidationException)
        ex.message == 'Following emails are already in use: newemail@acme.com'
    }

    def 'should delete users with given ids'() {
        given:
        def userId = 1L
        def ids = [userId].toSet()
        def command = new DeleteUsersCommand(ids)

        def existingUser = user('John', 'Doe', 'j.doe@amce.com', userId)
        userRepository.findByIdIn(ids) >> [existingUser]
        when:
        userService.deleteUsers(command)

        then:
        existingUser.isDeleted()
    }

    static def user(firstName, lastName, email, id = null) {
        def user = new UserEntity(
                firstName,
                lastName,
                email
        )

        user.id = id
        user
    }


    private static def registerUsersCommands(String userEmail) {
        [new RegisterUserCommand(
                firstName: 'John',
                lastName: 'Doe',
                email: userEmail
        )].toSet()
    }

}
