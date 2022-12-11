package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemStatusTicket;

	public Tickets(Boolean isAdmin) {

		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);
		
		//added status ticket to Admin Menu as its an admin feature
		mnuItemStatusTicket = new JMenuItem("Ticket Status");
		mnuAdmin.add(mnuItemStatusTicket);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemStatusTicket.addActionListener(this);
		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above
		 */


	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		bar.add(mnuAdmin);
		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
			String ticketDESC = JOptionPane.showInputDialog(null, "Enter a ticket description");

			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDESC);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				JOptionPane.showMessageDialog(null, "You do not have access to this feature");
		}
		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == mnuItemUpdate)
		{
			if(chkIfAdmin == true)
			{
				int id = 0;
				
				//asks user for ID and descriptiob they want to update
				String ticketID = JOptionPane.showInputDialog(null, "Enter ticket ID to update: ");
				String ticketDESC = JOptionPane.showInputDialog(null, "Enter ticket description to update");
				
				id = Integer.parseInt(ticketID);
				dao.updateRecords(ticketID, ticketDESC);
				
				if(id != 0)
				{
					//if the person is an admin, update the ticket
					System.out.println("Ticket: " + id + " updated.");
					JOptionPane.showMessageDialog(null, "Ticket: " + id + " updated.");
					setVisible(true); 
				}
				else
				{
					//if they are not an admin tell them they do not have access
					JOptionPane.showMessageDialog(null, "You do not have access to this feature");
					System.out.println("Cant update ticket");
					setVisible(true); 
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You do not have access to this feature");
				System.out.println("You do not have access to this feature");
			}
			
		}
		else if (e.getSource() == mnuItemDelete)
		{
			if(chkIfAdmin == true)
			{
				int id = 0;
				
				//asks user for ID to delete
				String ticketID = JOptionPane.showInputDialog(null, "Enter ID to delete");
				
				if(ticketID == null || (ticketID == null && ticketID.isEmpty()))
				{
					//if the ticket ID is invalid ask for another
					JOptionPane.showInputDialog(null, "Enter a ticket ID again");
					System.out.println("The entered ID is not registered");
				}
				else
				{
					//if the ticket ID is valid
					id = Integer.parseInt(ticketID);
					//prompt the user whether or not they want to delete the ticket
					int question = JOptionPane.showConfirmDialog(null, "Please confirm to delete ticket: " + ticketID + "?", "YES OR NO", JOptionPane.YES_NO_OPTION);
					
					//if the person says yes
					if(question == JOptionPane.YES_OPTION)
					{
						dao.deleteRecords(id);
						if(id != 0)
						{
							//delete the ticket
							System.out.println("Ticket: " + id + " deleted.");
							JOptionPane.showMessageDialog(null, "Ticket: " + id + " deleted.");
						}
						else
						{
							JOptionPane.showMessageDialog(null, "You do not have access to this feature");
							System.out.println("You do not have access to this feature");
						}
					}
				}
				
			}
			else
			{
				//if they are not an admin tell them they do not have access
				JOptionPane.showMessageDialog(null, "You do not have access to this feature");
				System.out.println("You do not have access to this feature");
			}
		}
		else if (e.getSource() == mnuItemStatusTicket)
		{
			if(chkIfAdmin == true)
			{
				int id = 0;
				//ask the user to input an ID for the ticket they would like to update
				String ticketID = JOptionPane.showInputDialog(null, "Which ticket ID status would you like to update? ");
				id = Integer.parseInt(ticketID);
				//ask them if the ticket is still in progress
				int question = JOptionPane.showConfirmDialog(null, "Is this ticket still in progress" + ticketID + "?", "YES OR NO", JOptionPane.YES_NO_OPTION);
				if(question == JOptionPane.YES_OPTION)
				{
					//if the person enters yes for the ticket being in progress, set the status to in progress
					JOptionPane.showMessageDialog(null, "Ticket: " + id + " has been set to IN PROGRESS.");
					String ticketDESC = "In Progress";
					dao.updateRecords(ticketID, ticketDESC);
				}
				else
				{
					//if the person enters no for the ticket being in progress, set the status to closed
					JOptionPane.showMessageDialog(null, "Ticket: " + id + " has been set to CLOSED.");
					String ticketDESC = "CLOSED";
					dao.updateRecords(ticketID, ticketDESC);
				}

			}
			else
			{
				//if they are not an admin tell them they do not have access
				JOptionPane.showMessageDialog(null, "You do not have access to this feature");
				System.out.println("You do not have access to this feature");
			}
		}
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */

	}

}
