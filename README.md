# U-Fund: Pawsitivity

An online U-Fund system built in Java **24**, **Maven** and **SpringBoot**.
  
## Team

- Jason Ugbaja
- Destiny Zeng
- Meghan Tomback
- Anthony Lansing
- Trinity Hampton


## Prerequisites

- Java **24** (Make sure to have correct JAVA_HOME setup in your environment)
- Maven


## How to run it

1. Clone the repository and go to the `/ufund-api/` directory.
2. Execute `mvn compile exec:java`
3. Open a new terminal and navigate to the  `/ufund-ui/` directory.
4. Execute `npm install`
5. Execute `ng serve --open`

## Account Information
Before you can access Pawsitivity, you will be required to log into an account, or to create an account. Below are 3 accounts with differing permission levels that can be used to navigate Pawsitivity;

**Super Admin [ Highest Permission Level]**
- Username: `Admin`
- Password: `SuperCool`

**Admin [ Medium Permission Level ]**
- Username: `Hamburger`
- Password: `123`

**Helper [ Basic Permission Level ]**
- Username: `Hotdog`
- Password: `123`

## How to test it

The Maven build script provides hooks for run unit tests and generate code coverage
reports in HTML.

To run tests on all tiers together do this:

1. Execute `mvn clean test jacoco:report`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/index.html`

To run tests on a single tier do this:

1. Execute `mvn clean test-compile surefire:test@tier jacoco:report@tier` where `tier` is one of `controller`, `model`, `persistence`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/{controller, model, persistence}/index.html`

To run tests on all the tiers in isolation do this:

1. Execute `mvn exec:exec@tests-and-coverage`
2. To view the Controller tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
3. To view the Model tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
4. To view the Persistence tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`

*(Consider using `mvn clean verify` to attest you have reached the target threshold for coverage)
  
  
## How to generate the Design documentation PDF

1. Access the `PROJECT_DOCS_HOME/` directory
2. Execute `mvn exec:exec@docs`
3. The generated PDF will be in `PROJECT_DOCS_HOME/` directory


## How to setup/run/test program 
1. Tester, first obtain the Acceptance Test plan
2. IP address of target machine running the app
3. Execute ________
4. ...
5. ...

## License

MIT License

See LICENSE for details.
