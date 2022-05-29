package plugin_file_sender.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import GUI.util.Client;
import GUI.util.GlobalsDefs;

/**
 * Handler for the deploy and run button in the toolbar
 *
 */
public class DeployAndRunHandler extends AbstractHandler {
	private Client client;

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		client = new Client();
		client.sendRequest(GlobalsDefs.DEPLOY_AND_RUN);
		return null;
	}

}
