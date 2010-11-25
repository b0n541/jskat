/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.main;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for creating a new table
 */
public class CreateTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public CreateTableAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller, bitmaps);

		putValue(Action.NAME, strings.getString("new_table"));
		putValue(Action.SHORT_DESCRIPTION,
				strings.getString("new_table_tooltip"));

		setIcon(Icon.TABLE);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.createTable();
	}

}
