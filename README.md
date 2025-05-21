# bet-processor
SpringBoot REST Service to process bets.

To check the functionalities it offers in detail, please take a look at the **swagger.yaml** file.

#### How to run it
The only requirement to run the program is to have installed in your machine a **Java 21** version.

Being in the directory where the source code is, execute the below  _maven_  command:

```console
	mvnw clean spring-boot:run
```

The program is going to run a  _Tomcat_  server in port 8080 by default. If that port is currently busy in your system, please execute the below  _maven_  command instead choosing your port: 

```console
	mvnw clean spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

#### How to test it
1-. Manual testing

Once the program is running in your machine, use a program that allows you to execute HTTP POST request (the examples below are using  _curl_ ), and access the below URLs (depending on the port selected in the above chapter)

```console
	curl -v "http://localhost:8080/bet-processor/api/v1/bets" -H "Content-type: application/json" -d "{\"id\": 1, \"amount\": 10, \"odds\": 0.3, \"client\": \"rafa\", \"status\": \"OPEN\"}"
	
	curl -v -X POST "http://localhost:8080/bet-processor/api/v1/shutdown"
```

2-. Automatic testing

These are the test included in the source code that can be executed with the below  _maven_  command:

```console
	mvnw clean test
```

#### Configuration
* As the system is using the Spring built-in task executor feature, some of the Spring related configuration can be changed to check different system behaviour:

__pring.task.execution.pool.core-size__  to set the number of workers that wants to be used for the bet processing

__pring.task.execution.pool.queue-capacity__  queue size to store the bet processing requests in case that all workers are busy. If a small number is selected, busy errors from the system can be checked.

__pring.task.execution.pool.max-size__  in case the queue is full, some additional workers can be created. The number will be the difference between core-size and max-size

* The simulated bet processing time has been configured as in the specification, but if different user cases want to be tested, it can be easily changed using the below property:

__bet-processor.processor.sleeping-time__



#### Design decisions
* Use of the Spring built-in task executor to take advantage of the easy configuration, the queueing and the gracefully shutdown.
* Use of three different services to implement the solution. This has been done to allow a future potential different implementation to any of these three, for example using a database.
* It is not clear to me in the specification how the procession waiting time should be applied. I will apply it at the beginning of the processing.
* A restriction has been added to not allow bets with an amount of 0 or lower than 0.
* A restriction has been added to not allow bets with odds of 0 or lower than 0.
* A restriction has been added to not allow bets with odds of 1 or greater than 1.
* No validation has been added regarding checking the values for a bet when it is open and these values when it is closed. Simply the bet values when it is being closed are being used.
* Method  __getSummary()__  inside  __BetResultService__  could be removed because it is never used outside the class, but I have left it and I can use it in the unit testing.


#### Potential improvements
* Use of a database to persist the successful processed bets and the errors. 

This way, in a new implementation of  _BetProcessorService_ , when the system starts up the database state could be loaded to avoid repeated bets and allow to close previous open bets.

Same way, in a new implementation of  _BetResultService_ ,the errors during the bet processing would be persisted in the database for a better analysis and processing.

* To avoid discarding bets with an open and close request very close in time because the close request was executed before the open one, a kind of queue by bet ID could be done.

I have been thinking that we could used  _java CountDownLatch_  by bet ID to force the next bet request to wait for the previous one to be resolved.

