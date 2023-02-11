package plugin_file_sender.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import GUI.util.*;

/**
 * Handler for the deploy button in the toolbar
 *
 */
public class DeployFileHandler extends AbstractHandler {
	private Client client;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		client = new Client();
		client.sendRequest(GlobalsDefs.DEPLOY_FILE);
		return null;
	}
}
