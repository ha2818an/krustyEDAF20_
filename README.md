# README


## Prerequisites

- JDK 11 or higher
- An IDE
- MySQL or MariaDB server
- Gradle (for building and running the project)

## Installation
Clone the Repository
Unix (Linux/macOS):
>[!NOTE]
>If you've recieved the [files](https://github.com/ha2818an/krustyEDAF20) in advance you can skip to [Build the Project](https://github.com/ha2818an/krustyEDAF20/blob/main/README.md#build-the-project).

Unix (Linux/macOS):

```
  sh
  git clone https://github.com/ha2818an/krustyEDAF20
  cd krusty
```

Windows:

```
  cmd
  git clone https://github.com/ha2818an/krustyEDAF20
  cd krusty
```



## Build the Project
Use Gradle to build the project:

Unix (Linux/macOS):

```
  sh
  ./gradlew build
```

Windows:

```
  cmd
  gradlew.bat build
```
>[!TIP]
>If you're encountering issues while building make sure that build.gradle dependencies are up to date!


## Database Setup
### Manual Database Setup
Run the following SQL scripts to set up your database schema and initial data:
Unix (Linux/macOS) & Windows:
First, create the schema:

```
  sh
  mysql -u krusty_user -p krusty < path/to/create-schema.sql
```

Then, populate initial data:

```
  sh
  mysql -u krusty_user -p krusty < path/to/initial-data.sql
```

### Automated Database Setup

Alternatively, you can set up your database by running the Java test file KrustyTests.java which initializes the schema and data:
Unix (Linux/macOS):

```
  sh
  ./gradlew test --tests KrustyTests
```

Windows:

```
  cmd
  gradlew.bat test --tests KrustyTests
```
>[!NOTE]
>If you're having issues during setup of the database, please read at the bottom of the page under [additional notes](https://github.com/ha2818an/krustyEDAF20/blob/main/README.md#additional-notes)


### Modify Database Connection
>[!IMPORTANT]
> Open krusty/Database.java and modify the **jdbcString, jdbcUsername, and jdbcPassword** with your database details.

## Running the Server
>[!CAUTION]
>Make sure you have updated to your local settings for the database.
### Start the Server
Use the following command to start the server:
Unix (Linux/macOS):

```
  sh
  ./gradlew run
```

Windows:

```
  cmd
  gradlew.bat run
```

### Accessing the Web Interface
Once the server is running, open your web browser and navigate to http://localhost:8888. The web interface should be accessible if the server was started successfully.
>[!TIP]
>In case you edited the database connection to something other than localhost, i.e coursegit, make sure to use the correct address when accessing the web interface.
## Additional notes: 
### Testing the API
Test the API endpoints using tools like Postman or Curl. Example to fetch customers:
Unix (Linux/macOS) & Windows:

```
  sh
  curl http://localhost:8888/api/v1/customers
```

### Troubleshooting
If you encounter issues related to database connections, ensure that your MySQL/MariaDB server is running and that the credentials in Database.java are correct.
If your still having issues with the database make sure that your credentials have the correct privileges.
>Log in to your MySQL or MariaDB server and execute the following:
  ```SQL
  CREATE USER 'krusty_user'@'localhost' IDENTIFIED BY 'password';
  GRANT ALL PRIVILEGES ON krusty.* TO 'krusty_user'@'localhost';
  FLUSH PRIVILEGES;
  ```

Check the console output for any error messages that might indicate what went wrong during the setup or runtime.
