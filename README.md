# TezKit 

### Tezos cross-platform GUI wallet & a tezos library to build, forge and send operations to the network. 

✅ No Electron, no JavaScript. 

👨‍💻 Under active development. 

![Wallet Screenshot](https://i.ibb.co/m079bbV/Screenshot-2020-08-19-at-14-15-40.png)

# Development

# Lombok

This project uses lombok. You might want to install the IntellJ plugin for it and enable annotation
processing under `Preferences -> Build, Execution, Deployment -> Compiler, Annotation Processors -> Enable Annotation Processing`.

## Fat jar
`mvn clean compile package`

`java -jar ./target/wallet-fat.jar`


## NEEDED FOR DEBUGGING - Manual JavaFX setup
Download JavaFX SDK from https://gluonhq.com/products/javafx/ and extract it into `javafx-sdk-11.0.2`.

Run with VM Options: `--module-path /path/to/project/javafx-sdk-11.0.2/lib --add-modules javafx.controls,javafx.fxml`

OPTIONAL: Add javafx-sdk-11.0.2/lib to 'Libraries' in Project Settings in IntelliJ.

## Env setup
Set the `tezkit_evn` environment variable to `dev` to load up `app-dev.properties` file with special
development options such as CSS hot reloading. 

## Scene Builder for IntellijJ 
https://www.jetbrains.com/help/idea/opening-fxml-files-in-javafx-scene-builder.html#open-in-scene-builder
