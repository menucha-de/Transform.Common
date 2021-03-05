package havis.transform.common;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class JsEngine {

	private ScriptEngine engine;

	public JsEngine() {
		engine = new ScriptEngineManager(ClassLoader.getSystemClassLoader()).getEngineByName("js");
	}

	public CompiledScript compile(String script) throws ScriptException {
		return ((Compilable) engine).compile(script);
	}

	public Object eval(CompiledScript script, ScriptContext context) throws ScriptException {
		return script.eval(context);
	}

	public Object eval(String script, ScriptContext context) throws ScriptException {
		return engine.eval(script, context);
	}
}