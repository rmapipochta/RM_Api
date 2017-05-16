package rm_1.redmineapi.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rm_1.redmineapi.RedmineCommunicationException;
import rm_1.redmineapi.RedmineException;
import rm_1.redmineapi.internal.comm.BasicHttpResponse;
import rm_1.redmineapi.internal.comm.ContentHandler;

public final class CopyBytesHandler implements ContentHandler<BasicHttpResponse, Void> {

	private final OutputStream outStream;

	public CopyBytesHandler(OutputStream outStream) {
		this.outStream = outStream;
	}

	@Override
	public Void processContent(BasicHttpResponse content)
			throws RedmineException {
		final byte[] buffer = new byte[4096 * 4];
		int numberOfBytesRead;
		try {
			try (InputStream input = content.getStream()) {
				while ((numberOfBytesRead = input.read(buffer)) > 0)
					outStream.write(buffer, 0, numberOfBytesRead);
			}
		} catch (IOException e) {
			throw new RedmineCommunicationException(e);
		}
		return null;
	}
}
