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

package io.advantageous.qbit.servlet.integrationproto;

import io.advantageous.qbit.annotation.RequestMapping;
import io.advantageous.qbit.http.HttpTransport;
import io.advantageous.qbit.http.server.HttpServer;
import io.advantageous.qbit.server.ServiceServer;

import static io.advantageous.qbit.server.ServiceServerBuilder.serviceServerBuilder;

/**
 * @author rhightower
 * on 2/12/15.
 */
public class MyServiceModule {
    public static ServiceServer configureApp(final HttpTransport httpTransport) {
        return serviceServerBuilder().setHttpTransport(httpTransport)
                .build().initServices(new PingService()).startServer();
    }

    @RequestMapping("/ping")
    public static class PingService {

    @RequestMapping("/ping")
    public String ping() {
            return "ok";
        }
    }
}
