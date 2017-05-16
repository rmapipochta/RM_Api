package rm_1.redmineapi.internal;

import rm_1.redmineapi.RedmineInternalError;
import rm_1.redmineapi.bean.Attachment;
import rm_1.redmineapi.bean.CustomFieldDefinition;
import rm_1.redmineapi.bean.Group;
import rm_1.redmineapi.bean.Issue;
import rm_1.redmineapi.bean.IssueCategory;
import rm_1.redmineapi.bean.IssuePriority;
import rm_1.redmineapi.bean.IssueRelation;
import rm_1.redmineapi.bean.IssueStatus;
import rm_1.redmineapi.bean.Membership;
import rm_1.redmineapi.bean.News;
import rm_1.redmineapi.bean.Project;
import rm_1.redmineapi.bean.Role;
import rm_1.redmineapi.bean.SavedQuery;
import rm_1.redmineapi.bean.TimeEntry;
import rm_1.redmineapi.bean.TimeEntryActivity;
import rm_1.redmineapi.bean.Tracker;
import rm_1.redmineapi.bean.User;
import rm_1.redmineapi.bean.Version;
import rm_1.redmineapi.bean.Watcher;
import rm_1.redmineapi.bean.WikiPage;
import rm_1.redmineapi.bean.WikiPageDetail;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URIConfigurator {
    private static final String URL_POSTFIX = ".json";

    private static final Map<Class<?>, String> urls = new HashMap<>();

    static {
        urls.put(User.class, "users");
        urls.put(Group.class, "groups");
        urls.put(Issue.class, "issues");
        urls.put(Project.class, "projects");
        urls.put(TimeEntry.class, "time_entries");
        urls.put(SavedQuery.class, "queries");
        urls.put(IssueStatus.class, "issue_statuses");
        urls.put(Version.class, "versions");
        urls.put(IssueCategory.class, "issue_categories");
        urls.put(Tracker.class, "trackers");
        urls.put(Attachment.class, "attachments");
        urls.put(News.class, "news");
        urls.put(IssueRelation.class, "relations");
        urls.put(Role.class, "roles");
        urls.put(Membership.class, "memberships");
        urls.put(IssuePriority.class, "enumerations/issue_priorities");
        urls.put(TimeEntryActivity.class, "enumerations/time_entry_activities");
        urls.put(Watcher.class, "watchers");
        urls.put(WikiPage.class, "wiki/index");
        urls.put(WikiPageDetail.class, "wiki");
        urls.put(CustomFieldDefinition.class, "custom_fields");
    }

    private final URL baseURL;
    private final String apiAccessKey;

    public URIConfigurator(String host, String apiAccessKey) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException(
                    "The host parameter is NULL or empty");
        }
        try {
            this.baseURL = new URL(host);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Illegal host URL " + host, e);
        }
        this.apiAccessKey = apiAccessKey;
    }

    public URI createURI(String query) {
        return createURI(query, new ArrayList<>());
    }

    public URI createURI(String query, NameValuePair... param) {
        return createURI(query, Arrays.asList(param));
    }

    /**
     * @param query e.g. "/issues.xml"
     * @return URI with auth parameter "key" if not in "basic auth mode.
     */
    private URI createURI(String query,
                          Collection<? extends NameValuePair> origParams) {
        final List<NameValuePair> params = new ArrayList<>(
                origParams);
        if (apiAccessKey != null) {
            params.add(new BasicNameValuePair("key", apiAccessKey));
        }
        try {
            final URIBuilder builder = new URIBuilder(baseURL.toURI());
            builder.addParameters(new ArrayList<>(origParams));
            //extra List creation needed because addParameters doesn't accept Collection<? extends NameValuePair>
            if (apiAccessKey != null) {
                builder.addParameter("key", apiAccessKey);
            }
            if (!query.isEmpty()) {
                builder.setPath(builder.getPath() + "/" + query);
            }
            return builder.build();
        } catch (URISyntaxException e) {
            throw new RedmineInternalError(e);
        }
    }

    public URI getChildObjectsURI(Class<?> parent, String parentId,
                                  Class<?> child, NameValuePair... args) {
        final String base = getConfig(parent);
        final String detal = getConfig(child);
        return createURI(base + "/" + parentId + "/" + detal + URL_POSTFIX,
                args);
    }

    public URI getChildIdURI(Class<?> parent, String parentId,
                             Class<?> child, int value, NameValuePair... params) {
        return this.getChildIdURI(parent, parentId, child, String.valueOf(value), params);
    }

    public URI getChildIdURI(Class<?> parent, String parentId,
                             Class<?> child, String value, NameValuePair... params) {
        final String base = getConfig(parent);
        final String detal = getConfig(child);
        return createURI(base + "/" + parentId + "/" + detal +
                "/" + value + URL_POSTFIX, params);
    }

    public URI getObjectsURI(Class<?> child, NameValuePair... args) {
        final String detal = getConfig(child);
        return createURI(detal + URL_POSTFIX, args);
    }

    public URI getObjectsURI(Class<?> child,
                             Collection<? extends NameValuePair> args) {
        final String detal = getConfig(child);
        return createURI(detal + URL_POSTFIX, args);
    }

    public URI getObjectURI(Class<?> object, String id, NameValuePair... args) {
        final String detal = getConfig(object);
        return createURI(detal + "/" + id + URL_POSTFIX, args);
    }

    private String getConfig(Class<?> item) {
        final String guess = urls.get(item);
        if (guess == null)
            throw new RedmineInternalError("Unsupported item class "
                    + item.getCanonicalName());
        return guess;
    }

    public URI getUploadURI() {
        return createURI("uploads" + URL_POSTFIX);
    }

    /**
     * Adds API key to URI, if the key is specified
     *
     * @param uri Original URI string
     * @return URI with API key added
     */
    public URI addAPIKey(String uri) {
        try {
            final URIBuilder builder = new URIBuilder(uri);
            if (apiAccessKey != null) {
                builder.setParameter("key", apiAccessKey);
            }
            return builder.build();
        } catch (URISyntaxException e) {
            throw new RedmineInternalError(e);
        }
    }
}
