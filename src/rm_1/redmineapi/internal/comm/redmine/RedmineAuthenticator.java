package rm_1.redmineapi.internal.comm.redmine;

import java.io.UnsupportedEncodingException;

import rm_1.redmineapi.internal.comm.Communicator;
import rm_1.redmineapi.internal.comm.ContentHandler;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import rm_1.redmineapi.RedmineException;
import rm_1.redmineapi.RedmineInternalError;

public class RedmineAuthenticator<K> implements Communicator<K> {
	/**
	 * Header value.
	 */
	private String authKey;

	/**
	 * Used charset.
	 */
	private final String charset;

	/**
	 * Peer communicator.
	 */
	private final Communicator<K> peer;

	public RedmineAuthenticator(Communicator<K> peer, String charset) {
		this.peer = peer;
		this.charset = charset;
	}

	public void setCredentials(String login, String password) {
		if (login == null) {
			authKey = null;
			return;
		}
		try {
			authKey = "Basic "
					+ Base64.encodeBase64String(
							(login + ':' + password).getBytes(charset)).trim();
		} catch (UnsupportedEncodingException e) {
			throw new RedmineInternalError(e);
		}
	}

	@Override
	public <R> R sendRequest(HttpRequest request, ContentHandler<K, R> handler)
			throws RedmineException {
		if (authKey != null)
			request.addHeader("Authorization", authKey);
		return peer.sendRequest(request, handler);
	}

}
