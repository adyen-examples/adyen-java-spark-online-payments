# [Adyen Checkout](https://docs.adyen.com/checkout) integration demo

## Run this integration in seconds using [Gitpod](https://gitpod.io/)

* Open your [Adyen Test Account](https://ca-test.adyen.com/ca/ca/overview/default.shtml) and create a set of [API keys](https://docs.adyen.com/user-management/how-to-get-the-api-key).
* Go to [gitpod account variables](https://gitpod.io/variables).
* Set the `ADYEN_API_KEY`, `ADYEN_CLIENT_KEY`, `ADYEN_HMAC_KEY` and `ADYEN_MERCHANT_ACCOUNT variables`.
* Click the button below!

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spark-online-payments)

_NOTE: To allow the Adyen Drop-In and Components to load, you  have to add gitpod.io as allowed origin for your chosen set of [API Credentials](https://ca-test.adyen.com/ca/ca/config/api_credentials_new.shtml)_


## Details 

This repository includes examples of PCI-compliant UI integrations for online payments with Adyen. Within this demo app, we've created a simplified version of an e-commerce website, complete with commented code to highlight key features and concepts of Adyen's API. Check out the underlying code to see how you can integrate Adyen to give your shoppers the option to pay with their preferred payment methods, all in a seamless checkout experience.

![Card Checkout Demo](src/main/resources/static/img/cardcheckout.gif)

## Integrations

**Java with Spark Java** demos of the following client-side integrations are available in this repository:

* [Drop-in](https://docs.adyen.com/checkout/drop-in-web)
* [Component](https://docs.adyen.com/checkout/components-web)
  * ACH
  * Alipay
  * Boleto
  * Card
  * Dotpay
  * Giropay
  * iDEAL
  * Klarna
  * PayPal
  * Sofort

## Requirements

* Java 11 (or higher)
* Network access to maven central

## Dependencies
The Gradle build will install the following jars from maven central
* Java Spark v2.9.1
    * Simple Logging Facade (slf4j-simple v1.7.25)
    * Jinjava template v2.7.1
* org.apache.http URLEncodedUtils v4.5.11
* Adyen Java API Library v17.2.0


## Installation

1. Clone this repo
2. Make sure you have Java installed

## Usage

1. Copy the `config.properties.example` config file to `config.properties` with your [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key), [Client Key](https://docs.adyen.com/user-management/client-side-authentication) - Remember to add `http://localhost:8080` as an origin for client key, and merchant account name like below:
    ```
    merchantAccount = YourTestMerchantAccount
    apiKey = YourAPIKey
    clientKey = YourClientKey
    ```
2. Run `./gradlew run`
3. Visit [http://localhost:8080](http://localhost:8080) and select an integration type!

_NOTE: The application will also automatically pick up on the ADYEN_MERCHANT_ACCOUNT, ADYEN_API_KEY and ADYEN_CLIENT_KEY environment variables in case you do not want to create a config.properties file._

## Contributing

We commit all our new features directly into our GitHub repository. Feel free to request or suggest new features or code changes yourself as well!!

Find out more in our [Contributing](https://github.com/adyen-examples/.github/blob/main/CONTRIBUTING.md) guidelines.

## License

MIT license. For more information, see the **LICENSE** file in the root directory
