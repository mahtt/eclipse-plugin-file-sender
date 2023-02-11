package GUI.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import GUI.UI.Gui;

/**
 * handles storage of microcontroller profiles and setup values e.g. the
 * filepath
 *
 */
public class McProfileStorage implements Serializable {

	private Gui gui;

	/**
	 * uniquely identifies class during deserialization process
	 */
	private static final long serialVersionUID = -1572982432467224770L;

	/**
	 * contains the microcontroller profiles
	 */
	private HashMap<String, Microcontroller> mc;

	/**
	 * filepath of file to send
	 */
	private String filePath;

	/**
	 * The name of the selected Microcontroller Profile
	 */
	private String selectedMcProfile;

	/**
	 * true, if filepath was set false, else
	 */
	private boolean isFilepathSet;

	/**
	 * true, if a profile is selected, else false
	 */
	private boolean isProfileSelected;

	/**
	 * Constructor
	 */
	public McProfileStorage() {
		if (hasStorageBeenInitialized()) {
			initializeMember();

		} else {
			this.mc = new HashMap<String, Microcontroller>();
			this.isFilepathSet = false;
			this.isProfileSelected = false;
		}
	}

	/**
	 * 
	 * @return Hashmap containing the profiles
	 */
	public HashMap<String, Microcontroller> getMc() {
		return mc;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isFilepathSet() {
		return isFilepathSet;
	}

	public void setFilepathSet(boolean isFilepathSet) {
		this.isFilepathSet = isFilepathSet;
	}

	public String getSelectedMcProfile() {
		return selectedMcProfile;
	}

	public void setSelectedMcProfile(String selectedMcProfile) {
		this.selectedMcProfile = selectedMcProfile;
	}

	public boolean isProfileSelected() {
		return isProfileSelected;
	}

	public void setProfileSelected(boolean isProfileSelected) {
		this.isProfileSelected = isProfileSelected;
	}

	/**
	 * writes the mcs object into a file for storage purposes
	 */
	public void writeMcS() {
		try {
			// Saving of object in a file
			FileOutputStream file = new FileOutputStream(GlobalsDefs.serFilePath);
			ObjectOutputStream out = new ObjectOutputStream(file);

			//serialization of object
			out.writeObject(this);

			out.close();
			file.close();
		}

		catch (IOException ex) {
			Gui gui = new Gui();
			gui.displayMessage("Error when writing serialization file.");
		}
	}

	/**
	 * deep copy of object
	 * @return a deep copy of the object
	 */
	public McProfileStorage getDeepCopy() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (McProfileStorage) ois.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public void setMc(HashMap<String, Microcontroller> mc) {
		this.mc = mc;
	}

	/**
	 * check if a serialization file has been created previously
	 * 
	 * @return true, if it has been created, false else
	 */
	private boolean hasStorageBeenInitialized() {
		File mcsSerializable = new File(GlobalsDefs.serFilePath);
		return mcsSerializable.exists() && !mcsSerializable.isDirectory();
	}

	/**
	 * initialize the member variables by reading from the previously saved
	 * serialization file
	 */
	private void initializeMember() {
		McProfileStorage mcs_temp;
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream(GlobalsDefs.serFilePath);
			ObjectInputStream in = new ObjectInputStream(file);

			// deserialization of object
			mcs_temp = (McProfileStorage) in.readObject();
			this.filePath = mcs_temp.getFilePath();
			this.selectedMcProfile = mcs_temp.getSelectedMcProfile();
			this.mc = mcs_temp.getMc();
			this.isFilepathSet = mcs_temp.isFilepathSet();
			this.isProfileSelected = mcs_temp.isProfileSelected();

			in.close();
			file.close();
		}

		catch (IOException ex) {
			Gui gui = new Gui();
			gui.displayMessage("An IOException occurred.");
		}

		catch (ClassNotFoundException ex) {
			Gui gui = new Gui();
			gui.displayMessage("A ClassNotFoundException occurred.");
		}
	}

	public boolean doesSelectedFileStillExist() {
		boolean res = false;
		if (isFilepathSet) {
			File fileToCheck = new File(filePath);
			res = fileToCheck.exists() && !fileToCheck.isDirectory();
			if (res == false) {
				isFilepathSet = false;
			}
		}
		return res;
	}
}
