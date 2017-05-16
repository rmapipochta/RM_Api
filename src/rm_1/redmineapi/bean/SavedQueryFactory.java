package rm_1.redmineapi.bean;

public class SavedQueryFactory {
    public static SavedQuery create(Integer id) {
        return new SavedQuery(id);
    }
}
