# NULL
Hackaton team 6

# Setup for Linux
You need to have at least version 11 of java SDK and add openfx libraries to path (in InteliJ: File -> Project Structure -> Project Settings -> Libraries). You will need to add classes and sources.

In build configuration under VM options you also need to add path as:

--module-path <pathToProject>/NULL/Blockchain/openjfx-11.0.2_linux-x64_bin-sdk/javafx-sdk-11.0.2/lib --add-modules javafx.controls,javafx.fxml
  
# Usage
First you need to define a new user under New user tab and it's hour pay.
Then you can go to tab Workday in which you insert the users work day (Date, hour of check-in and check-out, breaks where the number represents minutes on break). It creates a new block in a blockchain and also a new csv file entry. The validity can then be checked in Ckeck csv tab.

If a user's hour pay has increased(+,-) it can be changed in Update pay tab. Under tab Info you can check how much they earned the currently selected month.

# Authors
Tadej Kraševec, Rok Švikart
