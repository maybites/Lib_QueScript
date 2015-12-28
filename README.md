This is the Java development part for QueScript. It also serves as the Processing Library Exporter. For MaxMSP users please look [here](https://github.com/maybites/Lib_QueScriptMAX).

## Import to Eclipse

1. Fork this repository to use as a starting point.
  * Navigate to https://github.com/maybites/QueScript in your browser.
  * Click the "Fork" button in the top-right of the page.
  * Once your fork is ready, open the new repository's "Settings" by clicking the link in the menu bar on the right.
  * Change the repository name to the name of your Library and save your changes.
  * NOTE: GitHub only allows you to fork a project once. If you need to create multiple forks, you can follow these [instructions](http://adrianshort.org/2011/11/08/create-multiple-forks-of-a-github-repo/).
1. Clone your new repository to your Eclipse workspace.
  * Open Eclipse and select the File → Import... menu item.
  * Select Git → Projects from Git, and click "Next >".
  * Select "URI" and click "Next >". 
  * Enter your repository's clone URL in the "URI" field. The remaining fields in the "Location" and "Connection" groups will get automatically filled in.
  * Enter your GitHub credentials in the "Authentication" group, and click "Next >".
  * Select the `master` branch on the next screen, and click "Next >".
  * The default settings on the "Local Configuration" screen should work fine, click "Next >".
  * Make sure "Import existing projects" is selected, and click "Next >".
  * Eclipse should find and select the `Lib_QueScript` automatically, click "Finish".
1. Rename your Eclipse project.
  * In the Package Explorer, right-click (ctrl-click) on the folder icon of the `Lib_QueScript` project, and select Refactor → Rename... from the menu that pops up. 
  

## Set Up and Compile

1. Add Processing to the project build path.
  * Open your project's "Properties" window. 
  * Under "Eclipse > Preferences > Ant > Runtime", select the "Properties"" tab and then "Add Property...". 
  * create 'maybites.quescript.package.location' that points to the QueScript Max External folder
  * create 'maxmsp.library.location' that points to the Max Java Library folder
  * create 'processing.sketchbook.location' that points to the Processing sketchbook folder  
  * Confirm the setup with "OK".
1. Compile your Library using Ant.
  * From the menu bar, choose Window → Show View → Ant. A tab with the title "Ant" will pop up on the right side of your Eclipse editor. 
  * Drag the `resources/build.xml` file in there, and a new item "ProcessingLibs" will appear. 
  * Press the "Play" button inside the "Ant" tab.
1. BUILD SUCCESSFUL. The Library template will start to compile, control messages will appear in the console window, warnings can be ignored. When finished it should say BUILD SUCCESSFUL. Congratulations, you are set and you can start writing your own Library by making changes to the source code in folder `src`.
1. BUILD FAILED. In case the compile process fails, check the output in the console which will give you a closer idea of what went wrong. Errors may have been caused by
  * Incorrect path settings in the `build.properties` file.
  * Error "Javadoc failed". if you are on Windows, make sure you are using a JDK instead of a JRE in order to be able to create the Javadoc for your Library. JRE does not come with the Javadoc application, but it is required to create Libraries from this template.
