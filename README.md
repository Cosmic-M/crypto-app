# Crypto Currency APP

<h3>To launch app:</h3>
- pull this file <br>
- follow the link https://www.coinapi.io and get free API_KEY <br>
- open application.properties and put your free key <br>
- start docker on your local machine <br>
- open terminal and run mvn clean package <br>
- after that start command: docker-compose up <br>
- open browser and follow: http://localhost:6868/swagger-ui/#/ <br>
- everything prepared to working<br>

<h3>Project description:</h3>
There are three endpoints (2 controllers): the first one represents <br>
list of supported cryptocurrencies, the other two - price at specific <br>
crypto name at a current and historic time. I hang above method which <br>
get list of crypto, @PostConstruct annotation, to fill PostgreSQL with <br>
entities while app starts. So you can test first endpoint at any time. <br>
I think, it also has sense to renew the list periodically (with hand of <br>
crone job). For more comfortable working with first endpoint pagination <br>
has been implemented. To get historical prices you need to fetch crypto <br>
and date. I restrict data, so you will be represented only first 20 <br>
exchanges. While I tested Market Data API I noticed it may need some time <br>
to get current price for specific cryptos (it works good for popular cryptos <br>
such a BITCOIN, Ethereum, etcetera). In this way I didn't set too timing. If <br>
there won't be relevant responds from API during timing, I just return result <br>
which has been cushed during previous requests, or, if there weren't any <br>
relevant information about requested crypto, server just returns specific <br>
message for this crypto. <br>

GET: /cryptos/all - get all cryptos from DB <br>
GET: /prices/current - get current price to specific crypto <br>
GET: /prices/history - get history price for specific crypto at determine time <br>

<h3>In this APP were used such technologies like:</h3>
- org.apache.maven, version 4.0.0<br>
- java, version 17 <br>
- org.hibernate <br>
- spring boot <br>
- websockets <br>
- liquibase <br>
- rabbitMQ <br>
- swagger <br>
- docker <br>
