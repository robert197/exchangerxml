_Note: See the requirements wiki for tools needed to build and release Exchanger XML._

# To build use the Ant build.xml file #

  * The target "build" will build the basic jar file.
  * The target "release" will build the basic jar file and create an EXE file for Windows using Launch4J.
  * The target "createInstaller" will use installjammer to create an installer for using the exe and jars built above.

The targets should be called in the order of:
  1. "build"
  1. "release"
  1. "createInstaller"

# InstallJammer Config File Setup #
The InstallJammer config file assumes that the project is located at:

`C:/Users/Dev/workspace_34/Exchanger`

Replace the above path in `xngr-InstallJammer.mpi` using find and replace with the location of the project.