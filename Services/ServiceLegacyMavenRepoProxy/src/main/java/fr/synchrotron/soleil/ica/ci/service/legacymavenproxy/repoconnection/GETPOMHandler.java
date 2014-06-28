package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import com.github.ebx.core.MessageFilterService;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpEndpointInfo;
import fr.synchrotron.soleil.ica.proxy.utilities.HttpServerRequestWrapper;
import org.vertx.java.core.Vertx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class GETPOMHandler extends GETHandler {

    public GETPOMHandler(Vertx vertx, String contextPath, HttpEndpointInfo httpEndpointInfo) {
        super(vertx, contextPath, httpEndpointInfo);
    }

    @Override
    public void handle(final HttpServerRequestWrapper request) {
        final List<MessageFilterService> responseFilterList = new ArrayList<>();
        responseFilterList.add(new MessageFilterService(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, "fixWrongValue"));
        responseFilterList.add(new MessageFilterService(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, "cache"));
        request.clientTemplate().getAndRespond(responseFilterList);
    }

}
