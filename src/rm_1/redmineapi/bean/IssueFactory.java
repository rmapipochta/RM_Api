package rm_1.redmineapi.bean;

public class IssueFactory {

    /**
     * Each Issue object must have project Id set in order for Redmine 3.x to accept it via REST API.
     */
    public static Issue create(int projectId, String subject) {
        Issue issue = new Issue();
        issue.setProjectId(projectId);
        issue.setSubject(subject);
        return issue;
    }

    public static Issue create(Integer databaseId) {
        return new Issue(databaseId);
    }
}
