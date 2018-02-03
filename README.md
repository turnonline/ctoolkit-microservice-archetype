# Cloud Toolkit Microservice - Archetype
Microservice based on the Google Cloud Endpoints Frameworks (REST) designed for Google App Engine Java 1.8 standard environment.

## Usage
In order to generate microservice template from the archetype use the following command. You will be prompted to specify the information about the new project you want to create; the standard maven GAV (Group, Artifact, Version) and next ones needed for Google Cloud: **_ProjectId_** and **_MyServiceName_**.

If you don't know yet what is your **_ProjectId_** and **_MyServiceName_** no problem, choose arbitrary names and change it in the generated template once you will be done with Google Cloud Project setup, latest right before the deployment.

```bash
    mvn archetype:generate \
     -DarchetypeGroupId=org.ctoolkit.archetype \
     -DarchetypeArtifactId=ctoolkit-microservice-archetype \
     -DarchetypeVersion=1.0
```

### ProjectId
Google Cloud Project ID (source Google Cloud)

- The project ID is a single word (no spaces) that uniquely identifies your application.
- The project ID is one of the component’s of your site’s URL and it cannot be changed after the project is created.
- Thus, it is good to decide ahead of time what ID you want and to be prepared with alternatives in case your first choice is already taken.

### MyServiceName
- In an App Engine project (defined by ProjectId), you can deploy multiple microservices as separate services and each of the service must have its own name unique within project.
- The service name is one of the part of the service URL.

![Multiple services within single Google Cloud Project](https://cloud.google.com/solutions/images/microservices-project-with-modules.png)

