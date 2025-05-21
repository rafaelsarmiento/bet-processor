# bet-processor
SpringBoot REST Service to process bets.

To check the functionalities it offers in detail, please take a look at the **swagger.yaml** file.

#### How to run it
asdasd

#### Configuration
asdasd

#### Design decisions
* Use of the Spring built-in task executor to take advantage of the easy configuration, the queueing and the gracefully shutdown.
* It is not clear to me in the specification how the procession waiting time should be applied. I will apply it at the beginning of the processing.
* A restriction has been added to not allow bets with an amount of 0 or lower than 0.
* A restriction has been added to not allow bets with odds of 0 or lower than 0.
* A restriction has been added to not allow bets with odds of 1 or greater than 1.
* No validation has been added regarding checking the values for a bet when it is open and these values when it is closed. Simply the bet values when it is being closed are being used.


#### Potential improvements

