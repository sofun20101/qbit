/*
 * Copyright (c) 2015. Rick Hightower, Geoff Chandler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * QBit - The Microservice lib for Java : JSON, WebSocket, REST. Be The Web!
 */
package io.advantageous.qbit.meta.provider;


import io.advantageous.boon.core.Str;
import io.advantageous.boon.core.StringScanner;
import io.advantageous.qbit.annotation.RequestMethod;
import io.advantageous.qbit.meta.*;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class StandardMetaDataProvider implements MetaDataProvider {


    final Map<String, RequestMetaData> metaDataMap = new ConcurrentHashMap<>(100);
    final NavigableMap<String, RequestMetaData> treeMap = new TreeMap<>();
    private final RequestMethod httpRequestMethod;


    public StandardMetaDataProvider(final ContextMeta context, final RequestMethod method) {
        this.httpRequestMethod = method;
        context.getServices().forEach(service -> addService(context, service));
    }

    private void addService(final ContextMeta context,
                            final ServiceMeta service) {

        service.getMethods().forEach(method -> addMethod(context, service, method));
    }

    private void addMethod(final ContextMeta context,
                           final ServiceMeta service,
                           final ServiceMethodMeta method) {

         method.getRequestEndpoints().forEach(requestMeta -> addRequest(context, service, method, requestMeta));
    }

    private void addRequest(final ContextMeta context,
                             final ServiceMeta service,
                             final ServiceMethodMeta method,
                             final RequestMeta requestMeta) {


        service.getRequestPaths().forEach(path -> addEndPoint(context, service, method, requestMeta, path));

    }

    private void addEndPoint(final ContextMeta context,
                             final ServiceMeta service,
                             final ServiceMethodMeta method,
                             final RequestMeta requestMeta,
                             final String servicePath) {

        if (!requestMeta.getRequestMethods().contains(httpRequestMethod)) {
            return;
        }

        final String requestPath = requestMeta.getCallType() == CallType.ADDRESS ? requestMeta.getRequestURI() :
                StringScanner.substringBefore(requestMeta.getRequestURI(), "{");
        final String path = Str.join('/', context.getRootURI(), servicePath, requestPath).replace("//", "/");

        addRequestEndPointUsingPath(context, service, method, requestMeta, path.toLowerCase());

    }

    private void addRequestEndPointUsingPath(ContextMeta context, ServiceMeta service, ServiceMethodMeta method, RequestMeta requestMeta, String path) {
        RequestMetaData metaData = new RequestMetaData(path, context, requestMeta, method, service);

        if (requestMeta.getCallType()== CallType.ADDRESS) {
            metaDataMap.put(path, metaData);
        } else {
            treeMap.put(path, metaData);
        }
    }


    private RequestMetaData doGet(final String path) {

        RequestMetaData requestMetaData = metaDataMap.get(path);

        if (requestMetaData == null) {
            final Map.Entry<String, RequestMetaData> entry = treeMap.lowerEntry(path);

            if (entry == null) {
                return null;
            }
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            } else {
                return null;
            }
        } else {
            return requestMetaData;
        }
    }

    @Override
    public RequestMetaData get(final String path) {
        RequestMetaData requestMetaData = doGet(path);
        if (requestMetaData==null) {
            return doGet(path.toLowerCase());
        }
        return requestMetaData;
    }
}
