This is the Mozambique implementation of OpenLMIS.

License Terms
---------------------------
This program is part of the OpenLMIS logistics management information system platform software. Copyright © 2013, 2014, 2015, 2016 VillageReach, JSI, ThoughtWorks and CHAI.

This site contains code and related material necessary to implement a configuration of the OpenLMIS logistics management information system platform.  See https://github.com/OpenLMIS/open-lmis/ for details of OpenLMIS.

This site contains free software: you can redistribute it and/or modify it under the terms of the appropriate license.  As this site contains code developed by more than one organization and licensed under different terms you should refer to the license terms stated in each component for details.

The programs and documents on this site are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the applicable License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.

System Requirements
---------------------------
- JDK 7
- Docker
- Git
- Node.js
  * **For Linux users**
    - First, Install nvm to manage our node version. `wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.34.0/install.sh | bash`
    * Install the nodejs by nvm。 `nvm install v9.11.2` 
  * **For Mac users**
    * Install Node.js [directly](http://nodejs.org/) or using homebrew using `brew install nodejs`
    * Those who install Node.js using Homebrew should export the following (or include in `$HOME/.bash_profile` or `$HOME/.profile` or `$HOME/.bashrc` or `$HOME/.zshrc`, depending on your shell.

        ```bash
        export NODE_PATH="/usr/local/bin/node"
        export PATH="/usr/local/share/npm/bin:$PATH"
        ```
- NPM dependencies (used for linting JS, LESS files, minifying JS files & running jasmine specs etc.)
  * Install Grunt command-line runner by running (after installing Node.js)
    `> npm install -g grunt-cli`
  * Install karma test runner with karma coverage by running
    `> npm install -g karma karma-coverage`
  * Install karma command line with:
    `> npm install -g karma-cli`
  * Install project-specific grunt dependencies by navigating to `modules/openlmis-web` from project root directory and run
    `> npm install` (one-time activity)
  * Grunt tasks available can be found in `modules/openlmis-web/Gruntfile.js`

Source code
------------------
1. Get the source code using `git clone https://github.com/siglus/open-lmis.git`.
2. All work related to Mozambique should be pushed to the 2.0-moz branch, not master. After cloning, you can do `git checkout 2.0-moz` to get into the 2.0-moz branch.
3. For developer, you should check the branch to '2.0-dev'. when you finish your feature, you can merge '2.0-dev' to '2.0-moz' for release.
3. Set up dependencies on submodules & Grunt using:

    ```shell
    > cd open-lmis
    > git submodule init
    > git submodule update
    > cd modules/openlmis-web
    > npm install
    ```

IntelliJ IDEA Setup
-------------------
1. Run `./gradlew idea` to create the IntelliJ project files (may take some time downloading dependencies).
2. Open the open-lmis.ipr file (may take some time indexing files, first time only).
3. Install Lombok plugin according to the IntelliJ version.
4. To run individual tests in IntelliJ, configure your IntelliJ preferences to enable "annotation processing"

Jasmine Tests
-------------------
1. To run jasmine tests headlessly: just run the script `js_unit_test.sh`

Running App on embedded Jetty server
--------------------------------------------------
1. Clone the project repository using git.
2. Setup _postgres_ user with password as configured in `gradle.properties` file.
3. You can use `./gradlew clean setupdb setupExtensions seed build run` to start the app.
4. You can use `./java_unit_test.sh -e local` to just run all of the tests.
5. There are bunch of gradle tasks that you can see by running `./gradlew tasks`:
  - `build` is to build the app.
  - `setupdb` is to recreate the database and schema.(**Be careful, It will clean your database data**)
  - `setupExtensions` is to apply the database schema extensions added.
  - `seed` is to seed in the reference data.
  - `run` is to start the embedded jetty server.

Once the system is running, you can access the home page at `http://localhost:8081/`. You can log into the default instance with: user: `Admin123`, password: `Admin123`

> If you need to change the port(default:8081) for web server, you can change jettyRun.httpport attribute on 'modules/openlmis-web/build.gradle'. then restart the application.

Running Unit tests on Docker
--------------------------------------------------
1. You can use `./java_unit_test.sh -d -e local` to just run all of the tests.

## Issues
1. You may encounter a `java.lang.OutOfMemoryError: PermGen space`. This is a result of not enough memory for the Jetty JVM. One way to fix this is to export the following (or include in `$HOME/.bash_profile` or `$HOME/.profile` or `$HOME/.bashrc` or `$HOME/.zshrc`, depending on your shell).

    ```bash
    export JAVA_OPTS="-XX:MaxPermSize=512m"
    export JAVA_TOOL_OPTIONS="-Xmx1024m -XX:MaxPermSize=512m -Xms512m"
    ```
2. When you run the application again, you may start failed with follow message

```bash
ERROR: transport error 202: bind failed: Address already in use
ERROR: JDWP Transport dt_socket failed to initialize, TRANSPORT_INIT(510)
JDWP exit error AGENT_ERROR_TRANSPORT_INIT(197): No transports initialized [debugInit.c:750]
```

you should kill the gradle threat and restart. kill all gradle can use command `pkill gradle`

Tech Stack
---------------------------------
 - Java 1.7
 - Gradle 2.4
 - Postgres 9.4
 - Spring
 - Mybatis
 - Angularjs
 - Jasmine
 - Node.js v9.11.2
 - Grunt.js
 - Moment.js
