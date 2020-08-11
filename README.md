# [Adyen Checkout](https://docs.adyen.com/checkout) integration demo

This repository includes examples of PCI-compliant UI integrations for online payments with Adyen. Within this demo app, we've created a simplified version of an e-commerce website, complete with commented code to highlight key features and concepts of Adyen's API. Check out the underlying code to see how you can integrate Adyen to give your shoppers the option to pay with their preferred payment methods, all in a seamless checkout experience.

## Integrations

**Java with Spark Java** demos of the following client-side integrations are available in this repository:

* [Drop-in](https://docs.adyen.com/checkout/drop-in-web)
* [Component](https://docs.adyen.com/checkout/components-web)
  * Alipay
  * Boleto
  * Card
  * Dotpay
  * Giropay
  * iDEAL
  * Klarna
  * Sofort


## Requirements

* Java 1.8
* Network access to maven central

## Dependencies
The Gradle build will install the following jars from maven central
* Java Spark v2.8.0
    * Simple Logging Facade (slf4j-simple v1.7.25)
    * Jinjava template v2.7.1
* org.apache.http URLEncodedUtils v4.5.11
* Adyen Java API Library v5.0.0


## Installation

1. Clone this repo
2. Make sure you have Java installed

## Usage

1. Update the config file `config.properties` with your [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key), [Client Key](https://docs.adyen.com/user-management/client-side-authentication), and merchant account name like below:
    ```
    merchantAccount = YourTestMerchantAccount
    apiKey = YourAPIKey
    clientKey = YourClientKey
    ```
3. Run `./gradlew run`
3. Visit [http://localhost:8080](http://localhost:8080) and select an integration type!

## Contributing

We commit all our new features directly into our GitHub repository. Feel free to request or suggest new features or code changes yourself as well!!

## License

MIT license. For more information, see the **LICENSE** file in the root directory
