package main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.UUID;
import java.time.LocalDateTime;

public record Event(UUID uuid, LocalDateTime eventDateTime, String title, String description, String hEmail) {
    private static final int TITLE_MAX_LENGTH = 255;
    private static final int DESCRIPTION_MAX_LENGTH = 600;
    //returns the LocalDateTime if the date and the time are valid
    private static LocalDateTime validateDateTime(String date, String time) throws HandledIllegalValueException {
        //YYYY-MM-DD
        String[] dateSplit = date.split("-");
        //HH:mm (AM|PM)
        String[] timeSplit = time.split(" |:");

        timeSplit[0] = "" + (
                Integer.parseInt(timeSplit[0])
                        + (timeSplit[2].contains("PM") ? 12 : 0)
        );
        //TODO: parse
        int year = Integer.parseInt(dateSplit[0]);
        Month month = Month.of(Integer.parseInt(dateSplit[1]));
        int day = Integer.parseInt(dateSplit[2]);
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        assert false; //remove after parsing is implemented
        return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hour, minute));
    }
    public static String validateEmail(String email) throws HandledIllegalValueException {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        // note -- the actual email regex is much more sophisticated than this,
        // but there's no good way to add all the proper escapes for java, and
        // java has no raw strings
        if(!email.matches(emailRegex)){
            throw new HandledIllegalValueException("Email is invalid, please try again");
        }
        return email;
    }
    public Event validateEmail() throws HandledIllegalValueException{
        //these should be handled
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new HandledIllegalValueException(String.format("Title may not exceed %d", TITLE_MAX_LENGTH));
        }
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new HandledIllegalValueException(String.format("Description may not exceed %d", DESCRIPTION_MAX_LENGTH));
        }

        return this;
    }
    public static Event create(String uuid, String date, String time, String title, String description, String hEmail) throws HandledIllegalValueException{
        return new Event(UUID.fromString(uuid), validateDateTime(date, time), title, description, validateEmail(hEmail)).validateEmail();
    }
    public static Event create(String date, String time, String title, String description, String hEmail) throws HandledIllegalValueException{
        return Event.create(UUID.randomUUID().toString(), date, time, title, description, validateEmail(hEmail));
    }
    public static class HandledIllegalValueException extends Exception{
        public HandledIllegalValueException(String message) {
            super(message);
        }
    }
}