package GUI.UI;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import GUI.util.GlobalsDefs;
import GUI.util.McProfileStorage;
import GUI.util.Microcontroller;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.io.File;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JFileChooser;

/**
 * 
 * Graphical User Interface, in which the user can add, change and delete
 * microcontroller profiles, specify the location of the file to send and
 * specify the receiving microcontroller
 *
 */
public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFieldName;
	private JTextField txtFieldIp;
	private JTextField txtFieldPort;
	private JList<String> profileList;
	private JLabel labelFilePath;
	private JLabel labelNameOfSelectedProfile;
	private McProfileStorage mcs, mcs_old;
	private HashMap<String, Microcontroller> profiles;
	private DefaultListModel<String> mod;

	/**
	 * Launch the gui
	 */
	public void launch() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
					// discard changes if user closes window without saving
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent we) {
							discardChanges();
							dispose();
						}
					});
				} catch (Exception e) {
					displayMessage("An error occured.");
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		mod = new DefaultListModel<String>();
		JScrollPane scrollPane = new JScrollPane();
		profileList = new JList<String>(mod);
		scrollPane.setViewportView(profileList);

		// Serialization
		mcs = new McProfileStorage();
		mcs_old = mcs.getDeepCopy();
		profiles = mcs.getMc();
		updateProfileList();

		txtFieldName = new JTextField();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 859, 589);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(scrollPane);
		setContentPane(contentPane);

		// add button
		JButton addButton = new JButton("Add");

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtFieldName.getText().trim();
				String ip = txtFieldIp.getText().trim();
				String port = txtFieldPort.getText().trim();
				if (isIpValid(ip)) {
					Microcontroller m = new Microcontroller(name, ip, port);
					if (!profiles.containsKey(name)) {
						if (!txtFieldName.getText().equals("") && !txtFieldIp.getText().equals("")
								&& !txtFieldPort.getText().equals("")) {

							profileList.setModel(mod);
							mod.addElement(txtFieldName.getText().trim());
							profiles.put(name, m);
						} else {
							displayMessage("Please state a name, ip and port.");
						}
					} else {
						profiles.replace(name, m);
					}
					// clear text fields
					txtFieldIp.setText("");
					txtFieldName.setText("");
					txtFieldPort.setText("");
					mcs.writeMcS();
					updateProfileList();
					addButton.setText("Add");
				} else {
					displayMessage("Please enter a valid Ip.");
				}

			}
		});

		txtFieldName.setColumns(10);

		txtFieldIp = new JTextField();
		txtFieldIp.setColumns(10);

		JLabel labelName = new JLabel("Name:");

		JLabel labelp = new JLabel("IP:");

		// delete button
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!profiles.isEmpty()) {
					if (profileList.getSelectedIndex() != -1) {
						String selectedString = getNameAssociatedWithListEntiry(
								mod.getElementAt(profileList.getSelectedIndex()));
						mod.remove(profileList.getSelectedIndex());
						profiles.remove(selectedString);

						if (mcs.getSelectedMcProfile().equals(selectedString)) {
							mcs.setProfileSelected(false);
							getLabelNameOfSelectedProfile().setText(GlobalsDefs.NO_PROFILE_SELECTED);
						}
						mcs.writeMcS();
					}
				} else {
					displayMessage(GlobalsDefs.NO_PROFILE_ADDED_OR_SELECTED);
				}

			}
		});

		txtFieldPort = new JTextField();
		txtFieldPort.setColumns(10);

		JLabel labelPort = new JLabel("Port");

		JLabel labelMcProfiles = new JLabel("Microcontroller Profiles");

		JLabel labelAdd = new JLabel("Add new profile");

		// edit button
		JButton editButton = new JButton("Edit");
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addButton.setText("Update");
				if (!profiles.isEmpty() && profileList.getSelectedIndex() > -1) {
					String selectedString = getNameAssociatedWithListEntiry(
							mod.getElementAt(profileList.getSelectedIndex()));
					Microcontroller m = profiles.get(selectedString);
					txtFieldName.setText(m.getName());
					txtFieldIp.setText(m.getIp());
					txtFieldPort.setText(m.getPort());
				} else {
					displayMessage(GlobalsDefs.NO_PROFILE_ADDED_OR_SELECTED);
				}

			}
		});

		JLabel labelFileLocation = new JLabel("File location");

		// browse button
		JButton ChooseFilePathButton = new JButton("Browse");
		ChooseFilePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPathOfFileToSend();
			}
		});

		labelFilePath = new JLabel("-");

		JLabel labelSelectProfile = new JLabel("Select Profile");

		// select button
		JButton SelectProfileButton = new JButton("Select");
		SelectProfileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = profileList.getSelectedIndex();
				int noItemSelected = -1;
				if (index != noItemSelected) {
					String nameOfSelectedProfile = profiles
							.get(getNameAssociatedWithListEntiry(mod.getElementAt(index))).getName();
					mcs.setSelectedMcProfile(nameOfSelectedProfile);
					getLabelNameOfSelectedProfile().setText(nameOfSelectedProfile);
					mcs.setProfileSelected(true);
					mcs.writeMcS();
				} else {
					displayMessage("Please add or select a profile!");
				}
			}
		});

		labelNameOfSelectedProfile = new JLabel("-");

		// cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				discardChanges();
				JComponent comp = (JComponent) e.getSource();
				Window win = SwingUtilities.getWindowAncestor(comp);
				win.dispose();
			}
		});

		// save button
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComponent comp = (JComponent) e.getSource();
				Window win = SwingUtilities.getWindowAncestor(comp);
				win.dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup().addGap(31)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(labelMcProfiles, GroupLayout.PREFERRED_SIZE, 229,
										GroupLayout.PREFERRED_SIZE)
								.addGap(85).addComponent(labelAdd))
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(
										gl_contentPane.createParallelGroup(Alignment.TRAILING)
												.addGroup(gl_contentPane.createSequentialGroup()
														.addComponent(deleteButton).addGap(18).addComponent(editButton))
												.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 229,
														GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(gl_contentPane
										.createSequentialGroup().addGap(85)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_contentPane.createSequentialGroup().addComponent(labelName)
														.addGap(10).addComponent(txtFieldName,
																GroupLayout.PREFERRED_SIZE, 379,
																GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_contentPane.createSequentialGroup()
														.addComponent(labelFileLocation).addGap(29)
														.addComponent(ChooseFilePathButton, GroupLayout.PREFERRED_SIZE,
																96, GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
														.addGroup(gl_contentPane.createSequentialGroup()
																.addComponent(labelp)
																.addPreferredGap(ComponentPlacement.RELATED,
																		GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addComponent(txtFieldIp, GroupLayout.PREFERRED_SIZE,
																		379, GroupLayout.PREFERRED_SIZE))
														.addGroup(gl_contentPane.createSequentialGroup()
																.addComponent(labelPort).addGap(21)
																.addComponent(txtFieldPort, GroupLayout.PREFERRED_SIZE,
																		379, GroupLayout.PREFERRED_SIZE))
														.addComponent(addButton, Alignment.TRAILING))
												.addComponent(labelFilePath, GroupLayout.PREFERRED_SIZE, 428,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(gl_contentPane.createSequentialGroup()
														.addComponent(labelSelectProfile, GroupLayout.PREFERRED_SIZE,
																78, GroupLayout.PREFERRED_SIZE)
														.addGap(18).addComponent(SelectProfileButton,
																GroupLayout.PREFERRED_SIZE, 98,
																GroupLayout.PREFERRED_SIZE))
												.addComponent(labelNameOfSelectedProfile, GroupLayout.DEFAULT_SIZE, 478,
														Short.MAX_VALUE)))
										.addGroup(gl_contentPane.createSequentialGroup().addGap(384)
												.addComponent(saveButton).addGap(25).addComponent(cancelButton)))))
				.addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup().addGap(21)
				.addGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING).addComponent(labelMcProfiles).addComponent(labelAdd))
				.addGap(4)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup().addGap(2).addComponent(scrollPane,
								GroupLayout.PREFERRED_SIZE, 447, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup().addGap(3)
												.addComponent(labelName))
										.addComponent(txtFieldName, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(8)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(labelp)
										.addComponent(txtFieldIp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE))
								.addGap(11)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup().addGap(3)
												.addComponent(labelPort))
										.addComponent(txtFieldPort, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(addButton).addGap(14)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(labelFileLocation).addComponent(ChooseFilePathButton))
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(labelFilePath)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(labelSelectProfile).addComponent(SelectProfileButton,
												GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(labelNameOfSelectedProfile)
								.addGap(230)))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(editButton)
						.addComponent(deleteButton).addComponent(cancelButton).addComponent(saveButton))
				.addGap(18)));
		contentPane.setLayout(gl_contentPane);

		// File Path
		if (!mcs.isFilepathSet()) {
			getLabelFilePath().setText("Please choose file!");
		} else {
			String filePath = mcs.getFilePath();
			File tmp = new File(filePath);
			if (tmp.exists() && !tmp.isDirectory()) {
				getLabelFilePath().setText(filePath);
			} else {
				getLabelFilePath().setText("Please choose file!");
			}

		}

		// selected Profile
		if (!mcs.isProfileSelected()) {
			getLabelNameOfSelectedProfile().setText(GlobalsDefs.NO_PROFILE_SELECTED);
		} else {
			String nameOfSelectedProfile = mcs.getSelectedMcProfile();
			if (profiles.containsKey(nameOfSelectedProfile)) {
				getLabelNameOfSelectedProfile().setText(nameOfSelectedProfile);
			} else {
				mcs.setProfileSelected(false);
				getLabelNameOfSelectedProfile().setText(GlobalsDefs.NO_PROFILE_SELECTED);
				mcs.writeMcS();
			}
		}

	}

	/**
	 * Display a message in a pop-up dialog
	 * 
	 * @param msg message to be displayed
	 */
	public void displayMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	/**
	 * Returns path of file to send or "don't send." if the user closes the file
	 * dialog window.
	 * 
	 */
	void setPathOfFileToSend() {
		File selectedFile;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(contentPane);
		if (result == JFileChooser.CANCEL_OPTION) {
			// user closed the window without selecting a file -> continue

		} else if (result == JFileChooser.APPROVE_OPTION) {

			selectedFile = fileChooser.getSelectedFile();
			try {
				mcs.setFilePath(selectedFile.getAbsolutePath());
				getLabelFilePath().setText(mcs.getFilePath());
				mcs.setFilepathSet(true);
				mcs.writeMcS();

			} catch (Exception e) {
				// for example if the user closes the dialog without choosing a file to send.
				displayMessage("An error occured.");
			}
		}

		else {
			displayMessage("Error when choosing file");
		}
	}

	/**
	 * selects the first item of the list
	 * 
	 * @param l list
	 */
	void selectFirstItemOfList(JList<String> l) {
		if (l.getModel().getSize() > 0) {
			l.setSelectionInterval(0, 0);
		}
	}

	public JLabel getLabelFilePath() {
		return labelFilePath;
	}

	public JLabel getLabelNameOfSelectedProfile() {
		return labelNameOfSelectedProfile;
	}

	/**
	 * update the microcontroller profiles displayed in the gui
	 */
	void updateProfileList() {
		// fill List with saved profiles
		mod.clear();
		SortedSet<String> keys = new TreeSet<>(profiles.keySet()); // sort keys alphabetically
		for (String s : keys) {
			profileList.setModel(mod);
			mod.addElement(profiles.get(s).toString());
		}
	}

	/**
	 * 
	 * @return the selected microcontroller profile
	 */
	Microcontroller getSelectedMc() {
		String selectedString = mod.getElementAt(profileList.getSelectedIndex());
		return profiles.get(selectedString);
	}

	/**
	 * checks if the ip provided by the user is valid
	 * 
	 * @param ip the ip address to validate
	 * @return true if ip is valid, else false
	 */
	boolean isIpValid(final String ip) {
		String pattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
		return ip.matches(pattern);
	}

	/**
	 * list entry has the format <name>: <ip>_<port> because the toString() method
	 * of Microcontroller is used to fill the list this method returns the <name>
	 * part
	 * 
	 * @param listEntry
	 * @return name part of list entry
	 */
	String getNameAssociatedWithListEntiry(String listEntry) {
		return listEntry.substring(0, listEntry.indexOf("("));
	}

	/**
	 * discards changes when user presses the cancel button or when user closes the
	 * window without saving
	 */
	private void discardChanges() {
		mcs = mcs_old.getDeepCopy();
		if(mcs != null) {
			mcs.writeMcS();
		}
		else {
			displayMessage("An Error occured, discarding the changes.");
		}
		
	}
}
