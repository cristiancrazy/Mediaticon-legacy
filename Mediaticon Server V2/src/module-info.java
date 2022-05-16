module it.mdrunner {
	//Requested
	requires org.apache.commons.net;
	requires org.json;
	requires java.base;
	requires java.net.http;
	requires jdk.httpserver;

	//Exported
	exports it.mdrunner.exec;
	exports it.mdrunner.cfg;
}