package havis.transform.common;

import havis.transform.Transformer;
import havis.transform.TransformerFactory;
import havis.transform.TransformerProperties;

@TransformerProperties(value = "javascript", src = Object.class, dst = Object.class)
public class JsTransformerFactory implements TransformerFactory {

	@Override
	public Transformer newInstance() {
		return new JsTransformer();
	}
}