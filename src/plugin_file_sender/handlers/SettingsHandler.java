package plugin_file_sender.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import GUI.UI.Gui;

/**
 * Handler for the settings button in the toolbar
 *
 */
public class SettingsHandler extends AbstractHandler {
	Gui gui;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		gui = new Gui();
		gui.launch();
		return null;
	}
}
