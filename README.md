hyper-theremin
==============

CSE 481 Sound Capstone

Members
-------
Mike Davis davism78@uw.edu
**add names**

Setup
-----
To run this code you will need to obtain the LeapMotion SDK for your platform at: https://developer.leapmotion.com/

Once you have extracted the SDK locally, configure a classpath variable in eclipse to point to it.  This option 
is in preferences>Java>build path>classpath variables>add variable.  Call the variable LEAP_SDK and point it to
the folder in the sdk containing "LeapSDK" and "Examples" directories.  Eclipse should now find the java and windows
libraries needed to compile and run.

Furthermore, you will need to install the leapmotion drivers available on leapmotion.com and you will need to install
PureData environment in order to run the PureData source backend.
