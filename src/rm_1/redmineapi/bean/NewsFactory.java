package rm_1.redmineapi.bean;

public class NewsFactory {
    public static News create(Integer id) {
        return new News(id);
    }
}
