#Example project for working with Atmosphere in a Jira Plugin
###Work in progress

Follow progress at:
https://answers.atlassian.com/questions/32528175/using-atmosphere-in-a-jira-plugin


Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - 'pi' reinstalls the plugin into the running product instance
* atlas-help  -- prints description for all commands in the SDK



#Note

Skriv en ny AtmosphereServlet basert på org.atmosphere.cpr.AtmosphereServlet.java

Stikkord: 	useServlet30(false)

http://atmosphere.github.io/atmosphere/apidocs/index.html?org/atmosphere/cpr/AtmosphereFramework.html
