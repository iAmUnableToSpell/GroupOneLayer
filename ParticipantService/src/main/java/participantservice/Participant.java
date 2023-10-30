package participantservice;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Optional;
import java.util.UUID;

@Entity("participants")
public record Participant(
        @Id UUID uuid,
        UUID eventId,
        String name,
        String email) {
    public static String validateEmail(String email) throws HandledIllegalValueException {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        // note -- the actual email regex is much more sophisticated than this,
        // but there's no good way to add all the proper escapes for java, and
        // java has no raw strings
        if (!email.matches(emailRegex)) {
            throw new HandledIllegalValueException("Email %s is invalid".formatted(email));
        }
        return email;
    }

    private static Participant create(Optional<String> uuid, String eventId, String name, String email) throws HandledIllegalValueException {
        UUID uid;
        try {
            uid = uuid.map(UUID::fromString).orElse(UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            throw new HandledIllegalValueException("UUID is invalid, please try again");
        }
        try{
            return new Participant(uid, UUID.fromString(eventId), name, validateEmail(email));
        } catch (IllegalArgumentException e) {
            throw new HandledIllegalValueException("Event ID parameter is invalid");
        }

    }
    public static Participant create(String uuid, String eventId, String name, String email) throws HandledIllegalValueException {
        return Participant.create(Optional.ofNullable(uuid), eventId, name, email);
    }
    public static class HandledIllegalValueException extends Exception{
        public HandledIllegalValueException(String message) {
            super(message);
        }
    }


}
