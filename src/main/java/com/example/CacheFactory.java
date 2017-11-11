package com.example;

import static org.infinispan.query.remote.client.ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME;

import java.io.IOException;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;

public class CacheFactory {

    private RemoteCacheManager remoteCacheManager;
    private SerializationContext serCtx;

    public CacheFactory() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder = builder
                .addServers("localhost")
                .marshaller(new ProtoStreamMarshaller());

        remoteCacheManager = new RemoteCacheManager(builder.build());
        serCtx = ProtoStreamMarshaller.getSerializationContext(remoteCacheManager);
    }

    public <T> RemoteCache<String, T> getCache(String name, Class<T> clazz) throws IOException {

        String schemaName = clazz.getName() + ".proto";
        String generatedSchema = new ProtoSchemaBuilder()
                .fileName(schemaName)
                .packageName(clazz.getPackage().getName())
                .addClass(clazz)
                .build(serCtx);

        RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(PROTOBUF_METADATA_CACHE_NAME);
        metadataCache.put(schemaName, generatedSchema);

        RemoteCache<String, T> remoteCache = remoteCacheManager.getCache(name);
        return remoteCache;
    }
}
