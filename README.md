# google-drive-lib
Library responsible for intermediating Google Drive actions and basic functionalities through a simple service interface.

## Setting up

Import the dependency on your project.

```xml
<dependency>
   <groupId>com.google.drive.api</groupId>
   <artifactId>google-drive-lib</artifactId>
   <version>1.0.0</version>
</dependency>
```

- #### Google Client Credentials
Add your Google Secret Client Credentials in resources folder with given name: `client_secret.json`.


- #### Service layer
Inject and instantiate the GoogleDriveServiceImpl in your Service layer.

```java
@Service
public class MyService {

    private GoogleDriveServiceImpl googleDriveService = new GoogleDriveServiceImpl();

.
.
.
} 
```