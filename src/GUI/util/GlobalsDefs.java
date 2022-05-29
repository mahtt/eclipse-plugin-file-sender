package GUI.util;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Contains constants that are used by more than one class
 *
 */
public class GlobalsDefs {
	// Serialization File location
	public static final String serFilePath = getSerFileLocation() + "\\plugin_storage.ser";

	// Error messages
	public static final String NO_PROFILE_SELECTED = "Please select a profile!";
	public static final String NO_PROFILE_ADDED_OR_SELECTED = "Please add or select a profile";

	// request to mikrocontroller
	public static final int DEPLOY_FILE = 2;
	public static final int DEPLOY_AND_RUN = 3;
	public static final int GET_INTERPRETER_STATE = 4;
	public static final int STOP = 5;
	public static final int RUN = 6;

	// responses from mikrocontroller
	public static final int SERVER_RESPONDS_OK = 1;

	// interpreter states
	public static final int I_WAITING = 10;
	public static final int I_READY = 11;
	public static final int I_RUNNING = 12;
	public static final int I_INTERRUPTED = 13;
	public static final int I_EXECUTION_FINISHED = 14;
	public static final int I_ERROR = 15;

	// mikrocontroller denies request / error handling
	public static final int REQUEST_DENIED = -1;
	public static final int FILE_DEPLOYMENT_NOT_POSSIBLE = -2;
	public static final int RUN_NOT_POSSIBLE = -3;
	public static final int NO_COMMAND_CODE_WAS_SEND = -4;
	public static final int NO_COMMAND_HEADER_WAS_SEND = -5;
	public static final int COMMAND_CODE_COULD_NOT_BE_INTERPRETED = -6;
	public static final int UNSPECIFIED_ERROR = -99;

	// EXCEPTIONS
	public static final int CONNECTION_EXCEPTION_OCCURED = -10;
	public static final int IO_EXCEPTION_OCCURED = -20;

	/**
	 * helper method for serFilePath;
	 * 
	 * @return the location for the serialization file
	 */
	private static String getSerFileLocation() {

		String path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "\\.metadata\\.plugins";
		String dirName = path.concat("\\Plugin_File_Sender");
		File dir = new File(dirName);

		if (!dir.exists()) {
			dir.mkdir();
		}
		return dirName;
	}
}
