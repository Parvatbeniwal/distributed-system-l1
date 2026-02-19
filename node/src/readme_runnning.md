## Run with Gradle (development)

``` ./gradlew clean build ```
``` ./gradlew :node:bootRun --args="--server.port=5031 --node.id=A --peer.urls=http://localhost:5032,http://localhost:5033"```
``` ./gradlew :node:bootRun --args="--server.port=5032 --node.id=B --peer.urls=http://localhost:5031,http://localhost:5033" ```
``` ./gradlew :node:bootRun --args="--server.port=5033 --node.id=C --peer.urls=http://localhost:5031,http://localhost:5032" ```





# Running Built JARs
First, build the JARs:
```
./gradlew :node:bootJar
```

Then run them:
Node
```
java -jar node/build/libs/distributed-leader-0.0.1-SNAPSHOT.jar \
--server.port=5031 \
--node.id=Leader \
--peer.url=http://localhost:5032
```

```
curl -X POST "http://localhost:5031/node/put?key=testKey&value=100"

```
