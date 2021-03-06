/*
 * Copyright 2019 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.connector.cluster.consul;

import com.orbitz.consul.Consul;
import reactor.core.publisher.Flux;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ConsulContext {
  private final Consul primaryClient;
  private final Consul.Builder clientBuilder;
  private final DocumentKeys keys;
  private final String serviceName;
  private final String serviceId;

  public ConsulContext(Consul.Builder clientBuilder, String serviceName, String serviceIdOrNull) {
    this.clientBuilder = requireNonNull(clientBuilder);
    this.primaryClient = clientBuilder.build();

    this.keys = new DocumentKeys(primaryClient.keyValueClient(), serviceName);
    this.serviceName = requireNonNull(serviceName);
    this.serviceId = Optional.ofNullable(serviceIdOrNull).orElse(serviceName);
  }

  public Consul consul() {
    return primaryClient;
  }

  public Consul.Builder consulBuilder() {
    return clientBuilder;
  }

  public DocumentKeys keys() {
    return keys;
  }

  public String serviceName() {
    return serviceName;
  }

  public String serviceId() {
    return serviceId;
  }

  public Flux<Optional<String>> watchConfig() {
    return ConsulReactor.watch(consulBuilder(), keys().config());
  }

  public Flux<Optional<String>> watchControl() {
    return ConsulReactor.watch(consulBuilder(), keys().control());
  }
}
