This project demonstrates the problem I'm having getting the Infinispan Hotrod client to use protostream inside of Fuse.

It contains a blueprint which declares two beans: a factory for creating an Infinispan RemoteCache
and a bean produced by the factory for accessing the default cache.  There's also an example entity with protostream annotations. 

To reproduce the error you don't need an Infinispan server; this sample never gets as far as connecting.
Assuming you have a local Karaf up and running, install this project in your local Maven repo:

    mvn clean install

Then head to Karaf and add the following feature repos:

    features:addUrl mvn:org.infinispan/infinispan-client-hotrod/8.4.0.Final-redhat-2/xml/features
    features:addUrl mvn:com.example/infinispan-osgi-test/0.0.1-SNAPSHOT/xml/features

Now install the feature:
    
    karaf@root> features:install -v infinispan-osgi-test
    Installing feature infinispan-osgi-test 0.0.1-SNAPSHOT
    Installing feature infinispan-client-hotrod-with-query 8.4.0.Final-redhat-2
    Installing feature infinispan-client-hotrod 8.4.0.Final-redhat-2
    Installing feature infinispan-commons 8.4.0.Final-redhat-2
    Installing bundle mvn:org.infinispan/infinispan-commons/8.4.0.Final-redhat-2
    Installing bundle mvn:org.jboss.logging/jboss-logging/3.3.0.Final-redhat-1
    Installing bundle mvn:org.jboss.marshalling/jboss-marshalling-osgi/1.4.10.Final-redhat-3
    Installing bundle mvn:org.apache.logging.log4j/log4j-api/2.0
    Installing bundle mvn:org.infinispan/infinispan-client-hotrod/8.4.0.Final-redhat-2
    Found installed bundle: org.apache.commons.pool [215]
    Installing bundle mvn:org.infinispan/infinispan-remote-query-client/8.4.0.Final-redhat-2
    Installing bundle mvn:org.infinispan/infinispan-query-dsl/8.4.0.Final-redhat-2
    Installing bundle mvn:org.infinispan.protostream/protostream/4.0.0.CR1
    Installing bundle mvn:com.example/infinispan-osgi-test/0.0.1-SNAPSHOT
    Installing feature jms-condition-shell_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.jms.command [245]
    Installing feature karaf-condition-scr_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.shell.scr [183]
    Found installed bundle: org.apache.karaf.management.mbeans.scr [184]
    Installing feature scr-condition-shell_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.shell.scr [183]
    Installing feature scr-condition-management_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.management.mbeans.scr [184]
    Installing feature deployer-condition-aries_blueprint_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.deployer.blueprint [37]
    Installing feature deployer-condition-spring_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.deployer.spring [162]
    Installing feature transaction-condition-aries_blueprint_0_0_0 1.3.0
    Found installed bundle: org.apache.aries.transaction.blueprint [198]
    Installing feature jaas-condition-shell_0_0_0 2.4.0.redhat-630187
    Found installed bundle: org.apache.karaf.jaas.command [45]
    Installing feature camel-core-condition-shell_0_0_0 2.17.0.redhat-630187
    Found installed bundle: org.apache.camel.camel-commands-core [235]
    Found installed bundle: org.apache.camel.karaf.camel-karaf-commands [236]

Check the bundle state:
    karaf@root> osgi:list
    [ 297] [Active     ] [            ] [       ] [   80] Infinispan Hot Rod Client (8.4.0.Final-redhat-2)
    [ 298] [Active     ] [            ] [       ] [   80] Infinispan Remote Query Client (8.4.0.Final-redhat-2)
    [ 299] [Active     ] [            ] [       ] [   80] Infinispan Query DSL API (8.4.0.Final-redhat-2)
    [ 300] [Active     ] [            ] [       ] [   80] ProtoStream - core (4.0.0.CR1)
    [ 301] [Active     ] [Failure     ] [       ] [   80] Infinispan Fuse test (0.0.1.SNAPSHOT)

Check the logs:
    20:00:07,414 | INFO  | pool-17-thread-1 | RemoteCacheManager               | 294 - org.jboss.logging.jboss-logging - 3.3.0.Final-redhat-1 | ISPN004021: Infinispan version: 8.4.0.Final-redhat-2
    20:00:07,537 | WARN  | pool-17-thread-1 | BeanRecipe                       | 23 - org.apache.aries.blueprint.core - 1.4.5 | Object to be destroyed is not an instance of UnwrapperedBeanHolder, type: null
    20:00:07,538 | ERROR | pool-17-thread-1 | BlueprintContainerImpl           | 23 - org.apache.aries.blueprint.core - 1.4.5 | Unable to start blueprint container for bundle com.example.infinispan-osgi-test/0.0.1.SNAPSHOT
    org.osgi.service.blueprint.container.ComponentDefinitionException: Error when instantiating bean defaultCache of class org.infinispan.client.hotrod.RemoteCache
        at org.apache.aries.blueprint.container.BeanRecipe.wrapAsCompDefEx(BeanRecipe.java:361)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BeanRecipe.getInstanceFromFactory(BeanRecipe.java:297)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BeanRecipe.getInstance(BeanRecipe.java:278)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BeanRecipe.internalCreate2(BeanRecipe.java:830)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BeanRecipe.internalCreate(BeanRecipe.java:811)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.di.AbstractRecipe$1.call(AbstractRecipe.java:79)[23:org.apache.aries.blueprint.core:1.4.5]
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)[:1.8.0_152]
        at org.apache.aries.blueprint.di.AbstractRecipe.create(AbstractRecipe.java:88)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintRepository.createInstances(BlueprintRepository.java:247)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintRepository.createAll(BlueprintRepository.java:183)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintContainerImpl.instantiateEagerComponents(BlueprintContainerImpl.java:688)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintContainerImpl.doRun(BlueprintContainerImpl.java:383)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintContainerImpl.run(BlueprintContainerImpl.java:270)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintExtender.createContainer(BlueprintExtender.java:294)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintExtender.createContainer(BlueprintExtender.java:263)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BlueprintExtender.modifiedBundle(BlueprintExtender.java:253)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$Tracked.customizerModified(BundleHookBundleTracker.java:500)[17:org.apache.aries.util:1.1.0]
        at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$Tracked.customizerModified(BundleHookBundleTracker.java:433)[17:org.apache.aries.util:1.1.0]
        at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$AbstractTracked.track(BundleHookBundleTracker.java:725)[17:org.apache.aries.util:1.1.0]
        at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$Tracked.bundleChanged(BundleHookBundleTracker.java:463)[17:org.apache.aries.util:1.1.0]
        at org.apache.aries.util.tracker.hook.BundleHookBundleTracker$BundleEventHook.event(BundleHookBundleTracker.java:422)[17:org.apache.aries.util:1.1.0]
        at org.apache.felix.framework.util.SecureAction.invokeBundleEventHook(SecureAction.java:1127)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.felix.framework.util.EventDispatcher.createWhitelistFromHooks(EventDispatcher.java:696)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.felix.framework.util.EventDispatcher.fireBundleEvent(EventDispatcher.java:484)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.felix.framework.Felix.fireBundleEvent(Felix.java:4429)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.felix.framework.Felix.startBundle(Felix.java:2100)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.felix.framework.BundleImpl.start(BundleImpl.java:976)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.felix.framework.BundleImpl.start(BundleImpl.java:963)[org.apache.felix.framework-4.4.1.jar:]
        at org.apache.karaf.features.internal.FeaturesServiceImpl.doInstallFeatures(FeaturesServiceImpl.java:546)[10:org.apache.karaf.features.core:2.4.0.redhat-630187]
        at org.apache.karaf.features.internal.FeaturesServiceImpl$1.call(FeaturesServiceImpl.java:432)[10:org.apache.karaf.features.core:2.4.0.redhat-630187]
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)[:1.8.0_152]
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)[:1.8.0_152]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)[:1.8.0_152]
        at java.lang.Thread.run(Thread.java:748)[:1.8.0_152]
    Caused by: org.infinispan.protostream.annotations.ProtoSchemaBuilderException: Failed to generate marshaller implementation class
        at org.infinispan.protostream.annotations.impl.ProtoSchemaGenerator.generateAndRegister(ProtoSchemaGenerator.java:112)
        at org.infinispan.protostream.annotations.ProtoSchemaBuilder.build(ProtoSchemaBuilder.java:189)
        at com.example.CacheFactory.getCache(CacheFactory.java:36)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)[:1.8.0_152]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)[:1.8.0_152]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)[:1.8.0_152]
        at java.lang.reflect.Method.invoke(Method.java:498)[:1.8.0_152]
        at org.apache.aries.blueprint.utils.ReflectionUtils.invoke(ReflectionUtils.java:299)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BeanRecipe.invoke(BeanRecipe.java:980)[23:org.apache.aries.blueprint.core:1.4.5]
        at org.apache.aries.blueprint.container.BeanRecipe.getInstanceFromFactory(BeanRecipe.java:295)[23:org.apache.aries.blueprint.core:1.4.5]
        ... 32 more
    Caused by: protostream.javassist.CannotCompileException: by java.lang.NoClassDefFoundError: org/infinispan/protostream/RawProtobufMarshaller
        at protostream.javassist.ClassPool.toClass(ClassPool.java:1170)
        at protostream.javassist.ClassPool.toClass(ClassPool.java:1113)
        at protostream.javassist.ClassPool.toClass(ClassPool.java:1071)
        at protostream.javassist.CtClass.toClass(CtClass.java:1264)
        at org.infinispan.protostream.annotations.impl.MarshallerCodeGenerator.generateMessageMarshaller(MarshallerCodeGenerator.java:215)
        at org.infinispan.protostream.annotations.impl.ProtoSchemaGenerator.generateMarshallers(ProtoSchemaGenerator.java:145)
        at org.infinispan.protostream.annotations.impl.ProtoSchemaGenerator.generateAndRegister(ProtoSchemaGenerator.java:110)
        ... 42 more
    Caused by: java.lang.NoClassDefFoundError: org/infinispan/protostream/RawProtobufMarshaller
        at java.lang.ClassLoader.defineClass1(Native Method)[:1.8.0_152]
        at java.lang.ClassLoader.defineClass(ClassLoader.java:763)[:1.8.0_152]
        at java.lang.ClassLoader.defineClass(ClassLoader.java:642)[:1.8.0_152]
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)[:1.8.0_152]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)[:1.8.0_152]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)[:1.8.0_152]
        at java.lang.reflect.Method.invoke(Method.java:498)[:1.8.0_152]
        at protostream.javassist.ClassPool.toClass2(ClassPool.java:1183)
        at protostream.javassist.ClassPool.toClass(ClassPool.java:1164)
        ... 48 more
    Caused by: java.lang.ClassNotFoundException: org.infinispan.protostream.RawProtobufMarshaller
        at java.net.URLClassLoader.findClass(URLClassLoader.java:381)[:1.8.0_152]
        at java.lang.ClassLoader.loadClass(ClassLoader.java:424)[:1.8.0_152]
        at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:338)[:1.8.0_152]
        at java.lang.ClassLoader.loadClass(ClassLoader.java:357)[:1.8.0_152]
        ... 57 more

Alex Furmanski
alex@oh.is.it