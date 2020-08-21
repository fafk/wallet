# TezKit 

### Tezos cross-platform GUI wallet & a tezos library to build, forge and send operations to the network. 

✅ No Electron, no JavaScript. 

👨‍💻 Under active development. 

![Wallet Screenshot](https://i.ibb.co/m079bbV/Screenshot-2020-08-19-at-14-15-40.png)

### Modules

* Wallet - a GUI using the Tezos library 
* Tezos library 
    * Query a nod
    * Create and forge operations
    * Submit operations to the node
    * Generate a mnemonic and get a keypair from a mnemonic

### An Intermediate Roadmap 

✅ Send & Reveal operations. 

✅ Local forging.

✅ Main design. 

✅ Dashboard wired up to the Tezos library.

⏳ Baking & Delegation.

⏳ Smart Contracts support. 

⏳ Creating a Maven package with the library and publish it on Maven Central.

### Development

#### Lombok

This project uses lombok. You might want to install the IntellJ plugin for it and enable annotation
processing under `Preferences -> Build, Execution, Deployment -> Compiler, Annotation Processors -> Enable Annotation Processing`.
This project uses lombok. You might want to install the IntelliJ plugin for it and enable annotation
processing under Preferences -> Build, Execution, Deployment -> Compiler, Annotation Processors -> Enable Annotation Processing.

#### Fat jar
`mvn clean compile package`

`java -jar ./target/wallet-fat.jar`


#### Debugging - Manual JavaFX setup
Download JavaFX SDK from https://gluonhq.com/products/javafx/ and extract it into `javafx-sdk-11.0.2`.

Run with VM Options: `--module-path /path/to/project/javafx-sdk-11.0.2/lib --add-modules javafx.controls,javafx.fxml`

OPTIONAL: Add javafx-sdk-11.0.2/lib to 'Libraries' in Project Settings in IntelliJ.

#### Env setup
Set the `tezkit_evn` environment variable to `dev` to load `app-dev.properties` file with 
development options such as CSS hot reloading. 

#### Scene Builder for IntellijJ 

It's possible to build JavaFX GUIs with a graphical tool [Scene Builder](https://gluonhq.com/products/scene-builder/), which can be [integrated
into IntelliJ](https://www.jetbrains.com/help/idea/opening-fxml-files-in-javafx-scene-builder.html#open-in-scene-builder).
