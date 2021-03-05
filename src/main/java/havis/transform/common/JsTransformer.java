package havis.transform.common;

import havis.transform.TransformException;
import havis.transform.Transformer;
import havis.transform.ValidationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

class JsTransformer implements Transformer {

	final static String NAME = "javascript";
	final static String SCRIPT = Transformer.PREFIX + NAME + ".Script";

	private JsEngine engine;
	private CompiledScript script;
	private ScriptContext context;

	@Override
	public void init(Map<String, String> properties) throws ValidationException {
		if (properties != null) {
			for (Entry<String, String> entry : properties.entrySet()) {
				String key = entry.getKey();
				if (key != null) {
					switch (key) {
					case SCRIPT:
						try {
							engine = new JsEngine();
							script = engine.compile("(function(object){\n" + entry.getValue() + "\n})(object);");
							context = new SimpleScriptContext();
							context.setBindings(new SimpleBindings(), ScriptContext.ENGINE_SCOPE);
						} catch (ScriptException e) {
							throw new ValidationException("'" + SCRIPT + "' property contains invalid JavaScript: " + e.getMessage(), e);
						}
						break;
					default:
						if (key.startsWith(Transformer.PREFIX))
							throw new ValidationException("Unknown property '" + key + "'");
					}
				}
			}
		}
		if (script == null)
			throw new ValidationException("'" + SCRIPT + "' must be set for JavaScript transformation");
	}

	private Object convertJavaScriptObject(Object object) {
		if (object == null)
			return null;
		if (!(object instanceof Bindings))
			return object;

		Bindings binding = (Bindings) object;
		String typeName = binding.toString();
		if (typeName != null && typeName.startsWith("[") && typeName.endsWith(" Array]")) {
			// nashorn JS array
			List<Object> result = new ArrayList<>();
			for (int i = 0; i < binding.size(); i++)
				result.add(convertJavaScriptObject(binding.get(Integer.toString(i))));
			return result;
		} else {
			Map<String, Object> result = new LinkedHashMap<>();
			for (Entry<String, Object> entry : binding.entrySet())
				result.put(entry.getKey(), convertJavaScriptObject(entry.getValue()));
			return result;
		}
	}

	@Override
	public <T, S> T transform(S object) throws TransformException {
		context.getBindings(ScriptContext.ENGINE_SCOPE).put(VARIABLE, object);
		try {
			@SuppressWarnings("unchecked")
			T result = (T) convertJavaScriptObject(engine.eval(script, context));
			return result;
		} catch (Throwable e) {
			throw new TransformException("JavaScript transformation failed: " + e.toString());
		}
	}
}