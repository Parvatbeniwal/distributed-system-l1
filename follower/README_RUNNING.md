# Running the services (Leader / Follower)

Each service has its own properties file:
- `leader/src/main/resources/application-leader.properties`
- `follower/src/main/resources/application-follower.properties`

Example contents:

**leader/src/main/resources/application-leader.properties**

server.port=5031
node.id=Leader
peer.url=http://localhost:5032

logging.level.com.distributedsystem=DEBUG


**follower/src/main/resources/application-follower.properties**
server.port=5032
node.id=Follower

follower doesn't need peer url (or it can)


## Run with Gradle (development)

From project root open two terminals.

Start Leader:

```bash
./gradlew :leader:bootRun --args="--server.port=5031 --node.id=Leader --peer.url=http://localhost:5032"

```
Start Follower:
```bash
./gradlew :follower:bootRun --args="--server.port=5032 --node.id=Follower"
```

Notes:

You can also use --spring.config.location=classpath:/application-leader.properties
or enable profiles with --spring.profiles.active=leader if you adopt profile-based files.


Easier: pass overrides directly on command line:
```bash
./gradlew :leader:bootRun --args="--server.port=5031 --node.id=Leader --peer.url=http://localhost:5032"
```

Notes

You can instead load config files with:
```
--spring.config.location=classpath:/application-leader.properties


Or enable profiles (if you use application-leader.yml / application-follower.yml):

--spring.profiles.active=leader


Easiest: Pass overrides directly with --args
```


## Running Built JARs

### First, build the JARs:
```
./gradlew :leader:bootJar :follower:bootJar
```


Then run them:

Leader
```
java -jar leader/build/libs/distributed-leader-0.0.1-SNAPSHOT.jar \
--server.port=5031 \
--node.id=Leader \
--peer.url=http://localhost:5032
```

Follower
```
java -jar follower/build/libs/distributed-follower-0.0.1-SNAPSHOT.jar \
--server.port=5032 \
--node.id=Follower
```

### Convenience Scripts (Optional)

```
To simplify starting services, you can add shell scripts:
```
```
scripts/start-leader.sh


#!/usr/bin/env bash
./gradlew :leader:bootRun --args="--server.port=5031 --node.id=Leader --peer.url=http://localhost:5032"


scripts/start-follower.sh

#!/usr/bin/env bash
./gradlew :follower:bootRun --args="--server.port=5032 --node.id=Follower"
```

### Make scripts executable:
```
chmod +x scripts/start-*.sh
```

### Tips

If the port is already in use, change --server.port.

Verify startup by checking logs: node.id will be printed on boot.

Use --spring.config.location if you want to run with predefined config files.

Add more nodes by duplicating modules and adjusting ports/IDs.



## Quick test (curl)

### Write via Leader:
```
curl -X POST "http://localhost:5031/put?key=foo&value=bar"
```

### Read from follower:
```
curl "http://localhost:5032/get?key=foo"
```