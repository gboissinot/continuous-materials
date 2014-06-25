package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.repoconnection;

import com.github.ebx.core.MessageFilterService;
import fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.ServiceAddressRegistry;
import fr.synchrotron.soleil.ica.proxy.utilities.GETHandler;
import fr.synchrotron.soleil.ica.proxy.utilities.ProxyService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class GETPOMHandler extends GETHandler {

    public GETPOMHandler(ProxyService proxyService) {
        super(proxyService);
    }

    @Override
    public void handle(final HttpServerRequest request) {

        final List filterServiceList = new ArrayList();
        filterServiceList.add(new MessageFilterService(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, "fixWrongValue"));
        filterServiceList.add(new MessageFilterService(ServiceAddressRegistry.EB_ADDRESS_POMMETADATA_SERVICE, "cache"));

        proxyService.processGETRepositoryRequest(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(final HttpClientResponse clientResponse) {
                proxyService.sendClientResponseWithFilters(request, clientResponse, filterServiceList);
            }
        });
    }

}
