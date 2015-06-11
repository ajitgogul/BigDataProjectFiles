package topology;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class TwitterGui {

	protected Shell BigDataProject;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		try {
			TwitterGui window = new TwitterGui();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		BigDataProject.open();
		BigDataProject.layout();
		while (!BigDataProject.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		BigDataProject = new Shell();
		BigDataProject.setSize(480, 272);
		BigDataProject.setText("BigData Twitter Analysis Project");
		
		Label lblClickOnThe = new Label(BigDataProject, SWT.NONE);
		lblClickOnThe.setBounds(46, 51, 282, 14);
		lblClickOnThe.setText("Click on the button to extract desired tweets");
		
		Button btnClickHere = new Button(BigDataProject, SWT.NONE);
		btnClickHere.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					TwitterTopology.main(null);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnClickHere.setBounds(334, 44, 94, 28);
		btnClickHere.setText("Click here");
		
		Label label = new Label(BigDataProject, SWT.NONE);
		label.setText("Click on the button to open the filtered file!");
		label.setBounds(46, 126, 272, 14);
		
		Label label_1 = new Label(BigDataProject, SWT.NONE);
		label_1.setText("Click on the button to open the tweet file!");
		label_1.setBounds(46, 89, 265, 14);
		
		Button button = new Button(BigDataProject, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
				    Desktop.getDesktop().open(new File("/usr/local/git/repos/BigDataTwitterProject/tweets.txt"));
				} catch (IOException ex) {
				    System.out.println(ex.getMessage());
				}
			}
		});
		button.setText("Click here");
		button.setBounds(334, 82, 94, 28);
		
		Button button_1 = new Button(BigDataProject, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
				    Desktop.getDesktop().open(new File("/usr/local/git/repos/BigDataTwitterProject/users.txt"));
				} catch (IOException ex) {
				    System.out.println(ex.getMessage());
				}
				
			}
		});
		button_1.setText("Click here");
		button_1.setBounds(334, 119, 94, 28);
		
		Label lblCreatedBy = new Label(BigDataProject, SWT.NONE);
		lblCreatedBy.setBounds(10, 181, 460, 25);
		lblCreatedBy.setText("Created by : Anchal Katyal, Abhishek Mukherjee, Pratyusha Karnati, Rajit Shah");

	}
}
