package rm_1.redmineapi.bean;

public class WatcherFactory {
    public static Watcher create(Integer id) {
        return new Watcher(id);
    }
}
