package rm_1.redmineapi.internal.comm;

import org.apache.http.HttpRequest;
import rm_1.redmineapi.RedmineException;

public interface SimpleCommunicator<T> {
	/**
	 * Performs a request.
	 * 
	 * @return the response body.
	 */
	T sendRequest(HttpRequest request) throws RedmineException;

}
