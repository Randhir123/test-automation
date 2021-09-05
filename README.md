To run tests on an environment (local|dev|qa|prod):

    mvn test -Denv=<environment> -Dcucumber.filter.tags="@MyTag"

Tags select the tests to run.
