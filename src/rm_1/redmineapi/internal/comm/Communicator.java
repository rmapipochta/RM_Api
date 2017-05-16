package rm_1.redmineapi.internal.comm;

import org.apache.http.HttpRequest;
import rm_1.redmineapi.RedmineException;

public interface Communicator<K> {

	/**
	 * Performs a request.
	 * 
	 * @return the response body.
	 */
	<R> R sendRequest(HttpRequest request, ContentHandler<K, R> contentHandler) throws RedmineException;

}