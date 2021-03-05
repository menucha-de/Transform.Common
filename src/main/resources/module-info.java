module havis.transform.common {
    requires java.scripting;
    requires jdk.scripting.nashorn;

    requires transitive havis.transform.api;

    exports havis.transform.common;

    provides havis.transform.TransformerFactory with
        havis.transform.common.JsTransformerFactory;

}