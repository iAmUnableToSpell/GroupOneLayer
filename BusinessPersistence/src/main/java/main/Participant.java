package main;

import java.util.Optional;
import java.util.UUID;

public record Participant(UUID uuid, UUID eventId, String name, String email) {

    private static Participant create(Optional<String> uuid, String eventId, String name, String email) throws Event.HandledIllegalValueException {
        UUID uid;
        try {
            uid = uuid.map(UUID::fromString).orElse(UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            throw new Event.HandledIllegalValueException("UUID is invalid, please try again");
        }
        return new Participant(uid, UUID.fromString(eventId), name, Event.validateEmail(email));
    }
    public static Participant create(String uuid, String eventId, String name, String email) throws Event.HandledIllegalValueException {
            return Participant.create(Optional.ofNullable(uuid), eventId, name, email);
    }
    @Deprecated
    public static Participant create(String eventId, String name, String email) throws Event.HandledIllegalValueException {
        return Participant.create(UUID.randomUUID().toString(), eventId, name, email);
    }

}
