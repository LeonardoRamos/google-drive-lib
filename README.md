# google-drive-lib
Library responsible for intermediating Google Drive actions and basic functionalities through a simple service interface.

## Setting up

Import the dependency in your project.

```xml
<dependency>
   <groupId>com.google.drive.api</groupId>
   <artifactId>google-drive-lib</artifactId>
   <version>1.1.0</version>
</dependency>

<dependency>
   <groupId>com.google.drive.api</groupId>
   <artifactId>google-drive-lib</artifactId>
   <version>1.1.0</version>
   <type>test-jar</type>
   <scope>test</scope>
</dependency>
```

- #### Google Client Credentials
Copy your Google Secret Client Credentials into your project's resources folder with given name: `client_secret.json`.

Add the following property for the app's name Credential:

```properties 
google.api.app_name=myApp
```


- #### Service layer
Inject and instantiate the GoogleDriveServiceImpl in your Service layer.

```java
@Service
public class MyService {

    @Autowired
    private GoogleDriveServiceImpl googleDriveService;

.
.
.
} 
```
