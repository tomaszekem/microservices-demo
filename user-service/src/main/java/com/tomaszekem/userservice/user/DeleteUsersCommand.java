package com.tomaszekem.userservice.user;

import javax.validation.constraints.Size;
import java.util.Set;

import static com.tomaszekem.userservice.user.Constants.BULK_MAX_SIZE;

public class DeleteUsersCommand {

    @Size(min = 1, max = BULK_MAX_SIZE)
    private Set<Long> ids;

    public DeleteUsersCommand(Set<Long> ids) {
        this.ids = ids;
    }

    public DeleteUsersCommand() {
    }

    public Set<Long> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return "DeleteUsersCommand{" +
                "ids=" + ids +
                '}';
    }
}
