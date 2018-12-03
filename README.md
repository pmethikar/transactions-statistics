# transactions-statistics
transactions-statistics API is to calculate realtime statistics for the last 60 seconds of transactions.

#Endpoints
* POST /transactions – called every time a transaction is made. It is also the sole input of this rest API.
* GET /statistics – returns the statistic based of the transactions of the last 60 seconds.
* DELETE /transactions – deletes all transactions.

# Some implementation details and decisions

## Choice of Concurrent Collection
* **Requirements:**
   1. Frequent adding
   2. Frequent Removals
   3. Frequent iterations to calculate statistics
   4. Will contain huge amount of data (Even when purged)
* **Options**
    1. CopyOnWriteArrayList:
        * Lock is only used for write methods
        * Expensive writes
        * Fast iterations
        * No locking => suitable for truly concurrent environment
    2. Collections.synchronizedList:
        * Locking involved => slow in concurrent environment
        
## Strategy for storing and purging transactions
* **Way 1: Only Scheduled purging of transactions**
    * Pro: Will help to manage memory as transactions not valid anymore will not be stored
    * Con: Transactions will be picked up falsely for calculating statistics.
* **Way 2: Purge only when /statistics is called**
    * Pro: Less frequent purging => Less frequent operations
    * Con: If frequency of GET /statistics call <<< frequency of POST /transactions call => transactions will just keep on accumulating
    
* Chosen way:
    * A middle way between both the ways:
        1. Purge when /statistics is called: This will ensure that invalid transactions are not picked up
        2. Schedule purge after configured delay: This will ensure that even if there is no GET /statistics call, transactions are not unnecessarily accumulated.
    * A proper value of delay will tune purging and hence associated performance    

## Important properties in application.yml
Property | Explanation|
---|---
 txstat.purge.delay.milis|Determines the frequency of purging the old transactions in miliseconds. Value of this property will be determined based on the load. Higher the load, lower the value. For now it is kept to 5000 miliseconds(i.e. 5 seconds)
