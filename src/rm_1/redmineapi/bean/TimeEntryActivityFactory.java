package rm_1.redmineapi.bean;

public class TimeEntryActivityFactory {
    public static TimeEntryActivity create(Integer id) {
        return new TimeEntryActivity(id);
    }
}
