# bet-processor
SpringBoot REST Service to process bets.

To check the functionalities it offers in detail, please take a look at the **swagger.yaml** file.

#### How to run it
asdasd

#### Configuration
asdasd

#### Design decisions
* New column in the  _PRICES_  database table,  _ID_ , used like primary key. After reading carefully the specification is not clear for me if the  _PRICE_LIST_  can be used as primary key.


#### Potential improvements
* When the API is called with wrong input data, not numeric brand or product ID or a wrong data formatting, the API response could be considered like not human user readable.
