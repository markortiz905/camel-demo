## Tooling

Following tools will be required:

* gradle v4.3.1 or higher.
You can use [SDK Man](http://sdkman.io/install.html) for installation

```
sdk install gradle
```
* [Google Cloud SDK and CLI](https://cloud.google.com/sdk/downloads)

* One-off: Authenticate to GCP.

```bash
gcloud auth login
```

### Maven
To build the project one needs Weblogic Client jar to be in the local maven repo.
TO install one:

```bash
mvn install:install-file -Dfile=locallib/wlthint3client.jar -DgeneratePom=true -DgroupId=com.oracle.weblogic -DartifactId=wlthint3client -Dversion=10.3 -Dpackaging=jar
```

### Docker
Docker build depends on gcr.io/scg-container-registry/base/zulu-jdk-8:144.4 image to be available locally.
Make sure the first build attempt has the connectivity to given image.

### Other setup

```bash
gradle wrapper
```

## Build

To compile and and build the local docker image:

```bash
./gradlew docker
./gradlew generateCompose

```

### Docker repo push

Observe the kronos version. Has to match the app version defined in build.gradle:

```bash
gcloud docker -a
gradle dockerPush
```

### Running locally

#### IntelliJ Idea

At this stage, with everything is working up to this point, and especially if you had to rectify steps along the way, delete your /.idea directory.

Can be executed in IntelliJ Idea as an application.

Main Class : HrisKronosApplication
Module : hris-kronos_main

Set the following environment variables:

* external_config: PATH_TO_connection.properties
* JASYPT_ENCRYPTOR_PASSWORD: PASSWORD
* APM_SERVICE_ACCOUNT: GCP_SERVICE_ACCOUNT
* APM_SERVICE_KEY: GCP_SERVICE_ACCOUNT_ACCESS_KEY
* APM_PROJECT_ID: GCP_PROJECT_ID
* APM_HOSTNAME: NAME_OF THE_HOST(Logging purposes)
* APM_META_NAME: (will be used to group under a named context in logging)

Please get help from someone for setting these environment variables, especially JASYPT_ENCRYPTOR_PASSWORD, APM_SERVICE_ACCOUNT and APM_SERVICE_KEY.
This is "Edit Configurations" in IntelliJ. As an example:

external_config : /Users/you/ScentreGroup/work/hris-kronos/config/connection.properties
APM_SERVICE_ACCOUNT : 478780302124...82pee3gcoeij@developer.gserviceaccount.com
APM_SERVICE_KEY : QugAe3k7fcPvgPz/CgYEA...iRkJgMVLVXopZvYpVPV4qV8/dgz2oI+VPRbWRNWSxsca5MQe1Hn8YqIuHo+AGXtsCbIaP4H20E\u003d
APM_PROJECT_ID : scg-bi-sandbox
APM_HOSTNAME : CJM_ICM_MAC (Just a name you want to give your machine)
APM_META_NAME : hris-kronos

(Yes that is a lowercase environment variable!)

#### Springboot

This option can be run directly from intellij Idea and is debuggable.

```bash
export external_config=$(pwd)/config/external.properties
export jasypt_encryptor_password=PASSWORD
./gradlew bootRun
```

#### Docker local

Directly in the shell:

```bash
export kronos_version=0.0.1-SNAPSHOT
export jasypt_encryptor_password=PASSWORD
docker rm hris-kronos
docker run -it --name hris-kronos -v $(pwd)/config:/u00/config:ro -e "jasypt_encryptor_password=${jasypt_encryptor_password}" -e "external_config=/u00/config/external.properties" scentre/hris/hris-kronos:$kronos_version
```

OR using gradle wrapper.
Be mindful of the mapping - the directory defined in DockerRun task in build.gradle for config should exist:

```bash
export jasypt_encryptor_password=PASSWORD
docker-compose up
```

### How to run on the server

Use docker-compose.yml to configure the details.
IMPORTANT!  In order to make environment variable available for docker compose, it has to be prefixed with `COMPOSE_`.

```bash
export COMPOSE_JASYPT_ENCRYPTOR_PASSWORD=JASYPT_ENCRYPTOR_PASSWORD
docker-compose up -d
 ```

### Docker logs on the local machine

Use docker compose to start container even on the local machine.
Then use

```
docker logs hris-kronos
```

### Docker logs on the server

Whatever the logs are they need to be written into the STDOUT to be picked up by Docker.
The logs will be captured by FluentD and forwarded to GCP and to the local file.

 ```bash
tail -f /var/log/td-agent/hris-kronos.log
 ```

where hris-kronos is the container name.

#### Docker login into running container

```bash
docker exec -ti hris-kronos sh
```

### Access routes through Hawtio
Hawtio is an open source HTML5 web application for visualizing, starting/stopping and tracing Apache Camel routes, browsing endpoints, sending messages to endpoints as well as browsing and sending to ActiveMQ destinations, viewing logs and metric charting etc.
Jolokia is enabled to expose the application to Hawtio.

Install hawtio in your (development machine) computer (http://hawt.io/), then run the followings to access the routes through the howtio running in your local machine.

Note: You have to update the server(aupdc00-tcs01t.ad.scentregroup.com) in the commands to the server where is your container is running

In the server(tcs box) where container running:
```bash
docker exec -ti hris-kronos ssh -R 8888:localhost:8091 -N <USER_NAME>@aupdc00-tcs01t.ad.scentregroup.com
```

In your local machine:
```bash
ssh -L 8091:localhost:8888 -N <USER_NAME>@aupdc00-tcs01t.ad.scentregroup.com
```

Now hawtio can connect on localhost:8091


### Other

```bash
gradle build -x test
```

### Automatic build - Container Builder
Build configured on master and development branch can be skipped by including [skip ci] or [ci skip] in the commit message.
