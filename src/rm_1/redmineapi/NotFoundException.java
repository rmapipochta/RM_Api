package rm_1.redmineapi;

public class NotFoundException extends RedmineException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String msg) {
        super(msg);
    }
}