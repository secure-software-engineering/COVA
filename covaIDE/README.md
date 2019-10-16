# How to build covaIDE?
- covaIDE uses [MagpieBridge](https://github.com/MagpieBridge/MagpieBridge), check out the develop branch and install it into your local m2 repository with `mvn install -DskipTests` at first 

- build jar file with `mvn install -DskipTests`. This jar file can be used to run COVA in Android Studio. 

- build vscode extension with this jar file and install.
    - `cd vscode`
    - `npm install` (if the first time)
    - `npm install -g vsce` (if the first time)
    - `vsce package` (this will create vscode extension under vscode directory)
    - `code --install-extension cova-ide-0.0.1.vsix` (install the extension)

- Configuration
    - TODO

