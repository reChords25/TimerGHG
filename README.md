# TimerGHG
A simple ingame timer plugin for minecraft paper servers.

## Get the code
Clone the repository in your terminal:
```cmd
> git clone https://github.com/reChords25/TimerGHG.git
```
Or download the source code as zip file [here](https://github.com/reChords25/TimerGHG/archive/refs/heads/main.zip) and unzip it.

## Setup
**The following guide only works for IntelliJ IDEA. If you are using another Java IDE, the steps may vary.**  

Open the downloaded folder or the ``build.gradle`` in IntelliJ.

If, once the automatic gradle setup process has completed, IntelliJ tells you that it cannot resolve dependencies, make sure that your Gradle Java version is the same as the ``targetJavaVersion`` in the ``build.gradle`` file.  
If not, open the Gradle settings and set the right Java version. Then make Gradle reconfigure itself.

Once that's done, you should be able to build the project with the ``build`` Gradle task under ``Tasks/build``.  

You also should have the ``runServer`` task under ``Tasks/run paper`` which automatically starts up a paper server and loads your current build. For the server to work, you have to edit the ``run/eula.txt`` file, change ``eula=false`` to ``eula=true`` and then start the server again.  

You can join the server by normally starting your regular Minecraft and joining the server with the server adress ``localhost``. If the router of your network allows it, you can join the server from any device in the network, just with the IP adress of the server-running device as server IP.  

If you changed the code, re-run the ``build`` task and restart the server or reload the plugins with ``/reload confirm``. Restarting should be your first choice because of many problems known with the ``/reload`` command, so only use it if you have to.

## How to use
Use the ``/timer`` command to show a timer for all players on the server.  
 
The arguments are as follows:  
```
start <backward | forward, optional>  
pause  
stop  
reset  
add <1y> <1d> <1h> <1m> <1s> (at least one required, replace 1 with number)
subtract <same as add>
set <same as add>
format <color | bold | underline | italic>:  
  color <all minecraft original colors | hex code>
  bold <true | false>
  underline <same as bold>
  italic <same as bold>
```

## Contributing
Contributing to this project is not desired, the reason being that it only is a starting point for me to learn plugin development.  

If you encounter issues or have questions though, don't hestitate to ask!


## Note
This plugin is still under active development, so issues may happen more often!  
I will officially release the plugin once I'm happy with it.
