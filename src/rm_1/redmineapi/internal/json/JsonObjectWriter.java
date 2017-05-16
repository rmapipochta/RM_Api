package rm_1.redmineapi.internal.json;

import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Json object writer.
 */
public interface JsonObjectWriter<T> {
	void write(JSONWriter writer, T object) throws JSONException;
}
