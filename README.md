This is the core Java development part for QueScript. It also serves as the Processing Library Exporter. For MaxMSP users please look here.

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
  * Under "Java Build Path", select the "Libraries" tab and then "Add External JARs...". 
  * Locate and add Processing's `core.jar` to your build path. It is recommended that a copy of `core.jar` is located in your Eclipse workspace in a `libs` folder. If the `libs` folder does not exist yet, create it. Read the [section below](#AddingJARs) regarding where to find the `core.jar` file.
  * Confirm the setup with "OK".
1. Edit the Library properties.
  * Open the `resources` folder inside of your Java project and double-click the `build.properties` file. You should see its contents in the Eclipse editor. 
  * Edit the properties file, making changes to items 1-4 so that the values and paths are properly set for your project to compile. A path can be relative or absolute.
  * Make changes to items under 5. These are metadata used in the automatically generated HTML, README, and properties documents.
1. Compile your Library using Ant.
  * From the menu bar, choose Window → Show View → Ant. A tab with the title "Ant" will pop up on the right side of your Eclipse editor. 
  * Drag the `resources/build.xml` file in there, and a new item "ProcessingLibs" will appear. 
  * Press the "Play" button inside the "Ant" tab.
1. BUILD SUCCESSFUL. The Library template will start to compile, control messages will appear in the console window, warnings can be ignored. When finished it should say BUILD SUCCESSFUL. Congratulations, you are set and you can start writing your own Library by making changes to the source code in folder `src`.
1. BUILD FAILED. In case the compile process fails, check the output in the console which will give you a closer idea of what went wrong. Errors may have been caused by
  * Incorrect path settings in the `build.properties` file.
  * Error "Javadoc failed". if you are on Windows, make sure you are using a JDK instead of a JRE in order to be able to create the Javadoc for your Library. JRE does not come with the Javadoc application, but it is required to create Libraries from this template.

After having compiled and built your project successfully, you should be able to find your Library in Processing's sketchbook folder, examples will be listed in Processing's sketchbook menu. Files that have been created for the distribution of the Library are located in your Eclipse's `workspace/yourProject/distribution` folder. In there you will also find the `web` folder which contains the documentation, a ZIP file for downloading your Library, a folder with examples as well as the `index.html` and CSS file.

To distribute your Library please refer to the [Library Guidelines](https://github.com/processing/processing/wiki/Library-Guidelines).

## Source code

If you want to share your Library's source code, we recommend using an online repository available for free at [GitHub](https://github.com/).

## <a name='AddingJARs'/>Adding core.jar and other .jar files to your classpath</a>

The `core.jar` file contains the core classes of Processing and has to be part of your classpath when building a Library. On Windows and Linux, this file is located in the Processing distribution folder inside a folder named `lib`. On Mac OS X, right-click the Processing.app and use "Show Package Contents" to see the guts. The `core.jar` file is inside Contents → Resources → Java. For further information about the classes in `core.jar`, you can see the source [here](http://code.google.com/p/processing/source/browse/trunk/processing#processing/core) and the developer documentation [here](http://processing.googlecode.com/svn/trunk/processing/build/javadoc/core/index.html).

If you created a `libs` folder as described above, put the libraries you need to add to your classpath in there. In the "Properties" of your Java project, navigate to Java Build Path → Libraries, and click "Add External JARs...". Select the `.jar` files from the `libs` folder that are required for compiling your project. Adjust the `build.xml` file accordingly.

The `libs` folder is recommended but not a requirement, nevertheless you need to specify where your `.jar` files are located in your system in order to add them to the classpath.

In case a Library depends on system libraries, put these dependencies next to the `.jar` file. For example, Processing's `opengl.jar` Library depends on JOGL hence the DLLs (for Windows) or jnilibs (for OS X) have to be located next to the `opengl.jar` file.

