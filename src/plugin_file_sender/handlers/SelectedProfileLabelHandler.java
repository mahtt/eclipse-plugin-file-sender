package plugin_file_sender.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

import GUI.util.McProfileStorage;

/**
 * Handler for the selected profile label in the menu bar
 *
 */
public class SelectedProfileLabelHandler extends ContributionItem {
	private static MenuItem menuItem;

	public SelectedProfileLabelHandler() {
	}

	public SelectedProfileLabelHandler(String id) {
		super(id);
	}

	@Override
	public void fill(org.eclipse.swt.widgets.Menu menu, int index) {
		menuItem = new MenuItem(menu, SWT.DROP_DOWN, index);
		updateSelectedProfileLabel();
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateSelectedProfileLabel();
			}
		});
	}

	private static void updateSelectedProfileLabel() {
		String profileName = "No Profile Selected";
		McProfileStorage mcs = new McProfileStorage();
		if (mcs.isProfileSelected()) {
			profileName = mcs.getSelectedMcProfile();
		}
		menuItem.setText("Profile: " + profileName);
	}

}