package rm_1.redmineapi.bean;

public class IssuePriorityFactory {
    /**
     * @param id database ID.
     */
    public static IssuePriority create(Integer id) {
        return new IssuePriority(id);
    }
}
